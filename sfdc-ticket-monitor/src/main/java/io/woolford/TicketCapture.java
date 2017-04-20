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
import io.woolford.database.entity.Notification;
import io.woolford.database.entity.Ticket;
import io.woolford.database.mapper.DbMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TicketCapture {

    // TODO: add logging throughout
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

    @Autowired
    DbMapper dbMapper;

    static PartnerConnection connection;

    @Scheduled(cron = "*/5 * * * * *")
    public void captureTickets() throws IOException, TemplateException {

        // create connection to SFDC
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername(sfdcEmail);
        config.setPassword(sfdcPassword + sfdcToken);
        config.setAuthEndpoint(sfdcAuthEndpoint);

        try {
            connection = Connector.newConnection(config);
        } catch (ConnectionException e) {
            logger.error(e.toString());
//            e.printStackTrace();
        }

        // update the status of all the tickets
        for (String accountId : getAccountIds(sfdcName)){
            getTickets(accountId);
        }

        // notify user of any new tickets
        for (Ticket ticket : dbMapper.getOpenUnnotifiedTickets()){

            // create a map of all the ticket attributes to be processed by the Freemarker template
            Map<String, String> map = new HashMap<String, String>();
            map.put("caseNumber", ticket.getCaseNumber());
            map.put("accountName", ticket.getAccountName());
            map.put("description", ticket.getDescription());
            map.put("priority", ticket.getPriority());
            map.put("problemType", ticket.getProblemType());
            map.put("reason", ticket.getReason());
            map.put("status", ticket.getStatus());

            // initialize Freemarker template
            // TODO: move the Freemarker config to the class constructor; no need to re-instantiate with each run
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
            cfg.setClassForTemplateLoading(TicketCapture.class, "/templates");
            cfg.setDefaultEncoding("UTF-8");
            Template template = cfg.getTemplate("ticket-email.ftl");

            // render the template
            StringWriter stringWriter = new StringWriter();
            template.process(map, stringWriter);
            String contentValue = stringWriter.toString();

            // email unnotified tickets
            String subject = "case " + ticket.getCaseNumber() + " opened by " + ticket.getAccountName();
            sendMail(subject, contentValue);

            // record notification as being sent to avoid duplicate emails
            Notification notification = new Notification();
            notification.setCaseNumber(ticket.getCaseNumber());
            notification.setNotificationSent(true);
            dbMapper.upsertNotification(notification);
        }
    }

    // gets AccountId's for SE
    private List<String> getAccountIds(String sfdcName) {

        List<String> accountIdList = new ArrayList<String>();

        try {
            // get the list of account ID's for SE
            QueryResult queryResults = connection.query("SELECT accountid FROM opportunity WHERE opportunity.lead_se__c = '" + sfdcName + "'");
            if (queryResults.getSize() > 0) {
                for (int i=0; i < queryResults.getRecords().length; i++) {
                    String accountId = String.valueOf(queryResults.getRecords()[i].getChildren("AccountId").next().getValue());
                    accountIdList.add(accountId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountIdList;
    }

    // gets tickets for an account ID
    private void getTickets(String accountId) {

        try {
            QueryResult queryResults = connection.query("SELECT name FROM account WHERE id='" + accountId + "'");

            String accountName = String.valueOf(queryResults.getRecords()[0].getChildren("Name").next().getValue());

            queryResults = connection.query("SELECT casenumber, contactid, description, reason, status, priority, problem_type__c, lastvieweddate FROM case WHERE accountId='" + accountId + "'");

            if (queryResults.getSize() > 0) {

                for (int i=0; i < queryResults.getRecords().length; i++) {

                    // TODO: fix date last viewed date so it's not stored as a string
                    // TODO: the way of accessing the values from the query results looks pretty ugly/amateurish
                    Ticket ticket = new Ticket();
                    ticket.setAccountId(accountId);
                    ticket.setAccountName(accountName);
                    ticket.setCaseNumber(String.valueOf(queryResults.getRecords()[i].getChildren("CaseNumber").next().getValue()));
                    ticket.setDescription(String.valueOf(queryResults.getRecords()[i].getChildren("Description").next().getValue()));
                    ticket.setContactId(String.valueOf(queryResults.getRecords()[i].getChildren("ContactId").next().getValue()));
                    ticket.setLastViewedDate(String.valueOf(queryResults.getRecords()[i].getChildren("LastViewedDate").next().getValue()));
                    ticket.setPriority(String.valueOf(queryResults.getRecords()[i].getChildren("Priority").next().getValue()));
                    ticket.setProblemType(String.valueOf(queryResults.getRecords()[i].getChildren("Problem_Type__c").next().getValue()));
                    ticket.setReason(String.valueOf(queryResults.getRecords()[i].getChildren("Reason").next().getValue()));
                    ticket.setStatus(String.valueOf(queryResults.getRecords()[i].getChildren("Status").next().getValue()));

                    dbMapper.upsertTicket(ticket);
                }
            }

        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void sendMail(String subject, String contentValue) throws IOException {
        Email from = new Email(sendgridFrom);
        Email to = new Email(sendgridTo);
        Content content = new Content("text/html", contentValue);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            Response response = sg.api(request);
            // TODO: check that email was sent successfully
        } catch (IOException ex) {
            throw ex;
        }
    }

}
