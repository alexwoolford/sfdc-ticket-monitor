package io.woolford;

import com.google.common.base.CaseFormat;
import com.sendgrid.*;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.bind.XmlObject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.woolford.database.entity.*;
import io.woolford.database.mapper.DbMapper;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
class DataCapture {

    // TODO: add logging throughout; rolling logs w/ gzipped archive
    // TODO: get rid of Ticket POJO and use map instead
    // TODO: periodically refresh ContactName and AccountName fields in case they're updated in SFDC
    // TODO: configure/document steps to run as systemd service
    // TODO: refactor so the renderTemplate function doesn't appear in more than one class
    // TODO: make app deployable as self-contained Docker container
    // TODO: populate account table with all accounts, not just those with open tickets. This is important for the packet detection query
    // TODO: HTML output is sooooo ugly. Fix it.

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${sfdc.name}")
    private String sfdcName;

    @Value("${sfdc.email}")
    private String sfdcEmail;

    @Value("${sfdc.password}")
    private String sfdcPassword;

    @Value("${sfdc.token}")
    private String sfdcToken;

    @Value("${sfdc.auth.endpoint}")
    private String sfdcAuthEndpoint;

    @Value("${sendgrid.apikey}")
    private String sendgridApiKey;

    @Value("${sendgrid.from}")
    private String sendgridFrom;

    @Value("${sendgrid.to}")
    private String sendgridTo;

    private final
    DbMapper dbMapper;

    private static PartnerConnection connection;

    private final Configuration ftlConfig = new Configuration(Configuration.VERSION_2_3_26);

    @Autowired
    private DataCapture(DbMapper dbMapper){
        ftlConfig.setClassForTemplateLoading(DataCapture.class, "/templates");
        ftlConfig.setDefaultEncoding("UTF-8");
        this.dbMapper = dbMapper;
    }

    private static final RunStats runStats = new RunStats();

    // run every 20 minutes; lock process for 19 minutes to prevent multiple concurrent runs
    private static final int NINETEEN_MINUTES = 19 * 60 * 1000;

    @Scheduled(cron = "0 */20 * * * *")
    @SchedulerLock(name="captureTickets", lockAtMostFor = NINETEEN_MINUTES, lockAtLeastFor = NINETEEN_MINUTES)
    public void captureTickets() throws IOException, TemplateException {

        runStats.initialize();

        // create connection to SFDC
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername(sfdcEmail);
        config.setPassword(sfdcPassword + sfdcToken);
        config.setAuthEndpoint(sfdcAuthEndpoint);
        try {
            connection = Connector.newConnection(config);
            logger.info("Created connection to SFDC");
        } catch (ConnectionException e) {
            logger.error(e.toString());
            runStats.incrementExceptions();
        }

        // update the status of all the tickets
        List<String> accountIdList = getAccountIds(sfdcName);
        getTickets(accountIdList);
        logger.info("Upserted tickets for account IDs " + accountIdList.toString());

        // notify user of any new tickets
        for (Ticket ticket : dbMapper.getOpenUnnotifiedTickets()){

            // create a map of all the ticket attributes to be processed by the Freemarker template
            Map<String, String> map = new HashMap<>();
            map.put("accountName", ticket.getAccountName());
            map.put("caseNumber", ticket.getCaseNumber());
            map.put("id", ticket.getId());
            map.put("severity", ticket.getSeverity());
            map.put("currentStatusResolution", ticket.getCurrentStatusResolution());
            map.put("productComponent", ticket.getProductComponent());
            map.put("problemStatementQuestion", ticket.getProblemStatementQuestion());
            map.put("description", ticket.getDescription());
            map.put("contactName", ticket.getContactName());
            map.put("problemType", ticket.getProblemType());
            map.put("problemSubType", ticket.getProblemSubType());
            map.put("status", ticket.getStatus());

            // render the Freemarker template
            Template ticketEmailTemplate = ftlConfig.getTemplate("ticket-email.ftl");
            String emailContentValue = renderTemplate(ticketEmailTemplate, map);

            // email unnotified ticket
            String subject = "case " + ticket.getCaseNumber() + " opened by " + ticket.getAccountName();
            sendMail(subject, emailContentValue);
            logger.info("Email sent: " + subject);
            runStats.incrementEmails();

            // record notification as being sent to avoid duplicate emails
            Notification notification = new Notification();
            notification.setCaseNumber(ticket.getCaseNumber());
            notification.setNotificationSent();
            dbMapper.upsertNotification(notification);

        }

        runStats.setEndRun();
        dbMapper.insertRunStats(runStats);

    }

    @Scheduled(cron = "0 */30 * * * *")
    @SchedulerLock(name="captureBundles", lockAtMostFor = NINETEEN_MINUTES, lockAtLeastFor = NINETEEN_MINUTES)
    public void captureBundles() throws IOException, TemplateException, ParseException {
        logger.info("captureBundles");

        runStats.initialize();

        // create connection to SFDC
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername(sfdcEmail);
        config.setPassword(sfdcPassword + sfdcToken);
        config.setAuthEndpoint(sfdcAuthEndpoint);
        try {
            connection = Connector.newConnection(config);
            logger.info("Created connection to SFDC");
        } catch (ConnectionException e) {
            logger.error(e.toString());
            runStats.incrementExceptions();
        }

        List<String> customerRecordId2List = new ArrayList<>();
        for (Account account : dbMapper.getAllAccounts()){
            customerRecordId2List.add(account.getCustomerRecordId2());
        }

        Map bundleQueryMap = new HashMap();
        bundleQueryMap.put("customerRecordId2List", customerRecordId2List);

        // render the Freemarker template
        Template bundleQueryTemplate = ftlConfig.getTemplate("bundle-query.ftl");
        String bundleQuery = renderTemplate(bundleQueryTemplate, bundleQueryMap);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        QueryResult queryResults;
        try {
            queryResults = connection.query(bundleQuery);
            runStats.incrementSfdcQueries();
            if (queryResults.getSize() > 0) {
                for (int i=0; i < queryResults.getRecords().length; i++) {

                    String name = String.valueOf(queryResults.getRecords()[i].getChildren("Name").next().getValue());
                    String bundleDateString = String.valueOf(queryResults.getRecords()[i].getChildren("Bundle_Date__c").next().getValue());
                    String cluster = String.valueOf(queryResults.getRecords()[i].getChildren("Cluster__c").next().getValue());

                    Bundle bundle = new Bundle();
                    bundle.setBundleName(name);
                    bundle.setBundleDate(simpleDateFormat.parse(bundleDateString));
                    bundle.setClusterId(cluster);

                    dbMapper.upsertBundle(bundle);
                    logger.info(bundle.toString());

                }
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
            runStats.incrementExceptions();
        }


        List<String> clusterIdList = new ArrayList<>();
        clusterIdList.addAll(dbMapper.getDistinctClusterIds());

        Map clusterMap = new HashMap();
        clusterMap.put("clusterIdList", clusterIdList);

        Template clusterQueryTemplate = ftlConfig.getTemplate("cluster-query.ftl");
        String clusterQuery = renderTemplate(clusterQueryTemplate, clusterMap);

        try {
            queryResults = connection.query(clusterQuery);
            runStats.incrementSfdcQueries();
            if (queryResults.getSize() > 0) {
                for (int i=0; i < queryResults.getRecords().length; i++) {

                    String clusterId = String.valueOf(queryResults.getRecords()[i].getChildren("Id").next().getValue());
                    String clusterName = String.valueOf(queryResults.getRecords()[i].getChildren("Name").next().getValue());
                    Float numMasters = Float.parseFloat(String.valueOf(queryResults.getRecords()[i].getChildren("Num_Masters__c").next().getValue()));
                    Float numSlaves = Float.parseFloat(String.valueOf(queryResults.getRecords()[i].getChildren("Num_Slaves__c").next().getValue()));
                    BigDecimal usedStorage = new BigDecimal(String.valueOf(queryResults.getRecords()[i].getChildren("Used_Storage__c").next().getValue()));
                    BigDecimal totalStorage = new BigDecimal(String.valueOf(queryResults.getRecords()[i].getChildren("Total_Storage__c").next().getValue()));

                    Cluster cluster = new Cluster();
                    cluster.setClusterId(clusterId);
                    cluster.setClusterName(clusterName);
                    cluster.setNumMasters(numMasters);
                    cluster.setNumSlaves(numSlaves);
                    cluster.setUsedStorage(usedStorage);
                    cluster.setTotalStorage(totalStorage);

                    dbMapper.upsertCluster(cluster);
                    logger.info(cluster.toString());

                }
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
            runStats.incrementExceptions();
        }

        for (BundleEnriched bundleEnriched : dbMapper.getMostRecentBundles()){
            logger.info(bundleEnriched.toString());
        }

        logger.info(clusterIdList.toString());

    }

    private String renderTemplate(Template template, Map map) throws IOException, TemplateException {

        StringWriter stringWriter = new StringWriter();
        template.process(map, stringWriter);
        return stringWriter.toString();

    }

    // gets AccountId's for SE
    private List<String> getAccountIds(String sfdcName) {

        List<String> accountIdList = new ArrayList<>();
        QueryResult queryResults;
        try {
            // get the list of account ID's for SE
            queryResults = connection.query("SELECT accountid FROM opportunity WHERE opportunity.lead_se__c = '" + sfdcName + "'");
            runStats.incrementSfdcQueries();
            if (queryResults.getSize() > 0) {
                for (int i=0; i < queryResults.getRecords().length; i++) {
                    String accountId = String.valueOf(queryResults.getRecords()[i].getChildren("AccountId").next().getValue());
                    accountIdList.add(accountId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            runStats.incrementExceptions();
        }
        logger.info("Got list of account ID's for " + sfdcName + ": " + accountIdList);
        return accountIdList;
    }


    // gets tickets for list of accounts
    private void getTickets(List<String> accountIdList) throws IOException, TemplateException {

        QueryResult queryResults;
        try {
            // TODO: consider using Freemarker templates to generate all the SFDC queries

            List<SfdcTableColumn> sfdcTableColumnList = dbMapper.getSfdcTableColumns("case");
            Map<String, Object> map = new HashMap<>();
            map.put("sfdcTableColumnList", sfdcTableColumnList);
            map.put("accountIdList", accountIdList);
            Template sfdcCaseQueryTemplate = ftlConfig.getTemplate("sfdc-case-query.ftl");
            String caseQuery = renderTemplate(sfdcCaseQueryTemplate, map);

            queryResults = connection.query(caseQuery);
            runStats.incrementSfdcQueries();

            // iterate over case (ticket) records
            SObject[] sObjects = queryResults.getRecords();
            for (SObject sObject : sObjects){

                // create an attribute/value map for each record
                Map<String, String> recordMap = new HashMap<>();

                // iterate over XML returned by SFDC and append attribute name/value to record map
                Iterator<XmlObject> xmlObjectsIterator = sObject.getChildren();
                while (xmlObjectsIterator.hasNext()){

                    XmlObject xmlObject = xmlObjectsIterator.next();

                    // clean up the attribute names so they match MySQL naming convention
                    String name = xmlObject.getName().getLocalPart().replace("__c", "");
                    name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name); // attribute name

                    Object value = xmlObject.getValue();

                    String valueString;                                                  // attribute value
                    if (value != null){
                        valueString = value.toString();
                    } else {
                        valueString = "";
                    }

                    if (!name.equals("type")){
                        recordMap.put(name, valueString);
                    }
                }

                // lookup account name and contact name from ID's
                recordMap.put("accountname", getAccountName(recordMap.get("accountid")));
                recordMap.put("contactname", getContactName(recordMap.get("contactid")));

                // upsert ticket
                dbMapper.upsertTicket(recordMap);
                logger.info("Upserted ticket " + recordMap.get("casenumber") + " for " + recordMap.get("accountname"));

            }

        } catch (ConnectionException e) {
            e.printStackTrace();
            runStats.incrementExceptions();
        }
    }

    private String getContactName(String contactId){

        Contact contact = new Contact();
        String contactName;
        if (contactId.length() == 0){
            contactName = "";
        } else if (dbMapper.getContactById(contactId) == null){
            QueryResult queryResults = null;
            try {
                queryResults = connection.query("SELECT name FROM contact WHERE id='" + contactId + "'");
                runStats.incrementSfdcQueries();
            } catch (ConnectionException e) {
                e.printStackTrace();
                runStats.incrementExceptions();
            }
            contactName = String.valueOf(queryResults.getRecords()[0].getChildren("Name").next().getValue());
            contact.setContactId(contactId);
            contact.setContactName(contactName);
            dbMapper.upsertContact(contact);
            logger.info("ContactId: " + contactId + "; contactName: " + contactName + " retrieved from SFDC");
        } else {
            contactName = dbMapper.getContactById(contactId).getContactName();
            runStats.incrementCacheHits();
        }
        return contactName;
    }

    private String getAccountName(String accountId){

        Account account = new Account();
        String accountName;
        String customerRecordId2;
        if (accountId.length() == 0){
            accountName = "";
        } else if (dbMapper.getAccountById(accountId) == null){
            QueryResult queryResults = null;
            try {
                queryResults = connection.query("SELECT name, customer_record_id2__c FROM account WHERE id='" + accountId + "'");
                runStats.incrementSfdcQueries();
            } catch (ConnectionException e) {
                e.printStackTrace();
                runStats.incrementExceptions();
            }
            accountName = String.valueOf(queryResults.getRecords()[0].getChildren("Name").next().getValue());
            customerRecordId2 = String.valueOf(queryResults.getRecords()[0].getChildren("Customer_Record_ID2__c").next().getValue());
            account.setAccountId(accountId);
            account.setAccountName(accountName);
            account.setCustomerRecordId2(customerRecordId2);
            dbMapper.upsertAccount(account);
            logger.info("AccountId: " + accountId + "; accountName: " + accountName + " retrieved from SFDC");
        } else {
            accountName = dbMapper.getAccountById(accountId).getAccountName();
            runStats.incrementCacheHits();
        }
        return accountName;
    }

    private void sendMail(String subject, String contentValue) throws IOException {
        Email from = new Email(sendgridFrom);
        Email to = new Email(sendgridTo);
        Content content = new Content("text/html", contentValue);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        request.method = Method.POST;
        request.endpoint = "mail/send";
        request.body = mail.build();
        Response response = sg.api(request);
        // TODO: check that email was sent successfully
        logger.info("Email: " + subject + " sent");
    }

}
