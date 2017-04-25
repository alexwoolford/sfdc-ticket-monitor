package io.woolford;

import com.sendgrid.*;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.woolford.database.entity.*;
import io.woolford.database.mapper.DbMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@Component
class TicketCapture {

    // TODO: add logging throughout
    // TODO: add cache hit stats
    // TODO: prevent multiple concurrent runs
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
    private TicketCapture(DbMapper dbMapper){
        ftlConfig.setClassForTemplateLoading(TicketCapture.class, "/templates");
        ftlConfig.setDefaultEncoding("UTF-8");
        this.dbMapper = dbMapper;
    }

    private static RunStats runStats = new RunStats();

    @Scheduled(cron = "0 */20 * * * *")
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
        for (String accountId : getAccountIds(sfdcName)){
            getTickets(accountId);
            logger.info("Persisted tickets for accountId: " + accountId + " to MySQL");
        }

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
        logger.info("got list of account ID's for " + sfdcName + ": " + accountIdList);
        return accountIdList;
    }

    // gets tickets for an account ID
    private void getTickets(String accountId) throws IOException, TemplateException {

        QueryResult queryResults;
        try {
            // TODO: consider using Freemarker templates to generate all the SFDC queries

            String accountName = getAccountName(accountId);

            List<SfdcTableColumn> sfdcTableColumnList = dbMapper.getSfdcTableColumns("case");
            Map<String, Object> map = new HashMap<>();
            map.put("sfdcTableColumnList", sfdcTableColumnList);
            map.put("accountId", accountId);
            Template sfdcCaseQueryTemplate = ftlConfig.getTemplate("sfdc-case-query.ftl");
            String caseQuery = renderTemplate(sfdcCaseQueryTemplate, map);

            queryResults = connection.query(caseQuery);
            runStats.incrementSfdcQueries();

            if (queryResults.getSize() > 0) {

                for (int i=0; i < queryResults.getRecords().length; i++) {

                    // TODO: the way of accessing the values from the query results looks pretty ugly/amateurish
                    // perhaps use reflection to eliminate all the hard-coded literals.

                    Ticket ticket = new Ticket();
                    ticket.setCaseNumber(String.valueOf(queryResults.getRecords()[i].getChildren("CaseNumber").next().getValue()));
                    ticket.setId(String.valueOf(queryResults.getRecords()[i].getChildren("Id").next().getValue()));
                    ticket.setAccountId(accountId);
                    ticket.setAccountName(accountName);
                    ticket.setSeverity(String.valueOf(queryResults.getRecords()[i].getChildren("Severity__c").next().getValue()));
                    ticket.setCurrentStatusResolution(String.valueOf(queryResults.getRecords()[i].getChildren("Current_Status_Resolution__c").next().getValue()));
                    ticket.setProductComponent(String.valueOf(queryResults.getRecords()[i].getChildren("Product_Component__c").next().getValue()));
                    ticket.setProblemStatementQuestion(String.valueOf(queryResults.getRecords()[i].getChildren("Problem_Statement_Question__c").next().getValue()));
                    ticket.setDescription(String.valueOf(queryResults.getRecords()[i].getChildren("Description").next().getValue()));

                    String contactId = String.valueOf(queryResults.getRecords()[i].getChildren("ContactId").next().getValue());
                    ticket.setContactId(contactId);

                    if (!Objects.equals(contactId, "null")){
                        String contactName = getContactName(contactId);
                        ticket.setContactName(contactName);
                    }

                    ticket.setPriority(String.valueOf(queryResults.getRecords()[i].getChildren("Priority").next().getValue()));
                    ticket.setProblemType(String.valueOf(queryResults.getRecords()[i].getChildren("Problem_Type__c").next().getValue()));
                    ticket.setProblemSubType(String.valueOf(queryResults.getRecords()[i].getChildren("Problem_Sub_Type__c").next().getValue()));
                    ticket.setReason(String.valueOf(queryResults.getRecords()[i].getChildren("Reason").next().getValue()));
                    ticket.setStatus(String.valueOf(queryResults.getRecords()[i].getChildren("Status").next().getValue()));

                    dbMapper.upsertTicket(ticket);
                    logger.info("Upserted ticket " + ticket.getCaseNumber() + " for " + ticket.getAccountName());
                }
            }

        } catch (ConnectionException e) {
            e.printStackTrace();
            runStats.incrementExceptions();
        }
    }

    private String getContactName(String contactId){

        Contact contact = new Contact();
        String contactName;
        if (dbMapper.getContactById(contactId) == null){
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
        } else {
            contactName = dbMapper.getContactById(contactId).getContactName();
            runStats.incrementCacheHits();
        }
        return contactName;
    }

    private String getAccountName(String accountId){

        Account account = new Account();
        String accountName;
        if (dbMapper.getAccountById(accountId) == null){
            QueryResult queryResults = null;
            try {
                queryResults = connection.query("SELECT name FROM account WHERE id='" + accountId + "'");
                runStats.incrementSfdcQueries();
            } catch (ConnectionException e) {
                e.printStackTrace();
                runStats.incrementExceptions();
            }
            accountName = String.valueOf(queryResults.getRecords()[0].getChildren("Name").next().getValue());
            account.setAccountId(accountId);
            account.setAccountName(accountName);
            dbMapper.upsertAccount(account);
            logger.info("account " + accountId + " retrieved from MySQL");
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
