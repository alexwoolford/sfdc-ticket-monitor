package io.woolford;

import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import io.woolford.database.entity.Ticket;
import io.woolford.database.mapper.DbMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class TicketCapture {

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

    @Autowired
    DbMapper dbMapper;

    static PartnerConnection connection;

    // TODO: replace postconstruct annotation with @Scheduled
    @PostConstruct
    public void captureTickets() {

        ConnectorConfig config = new ConnectorConfig();
        config.setUsername(sfdcEmail);
        config.setPassword(sfdcPassword + sfdcToken);
        config.setAuthEndpoint(sfdcAuthEndpoint);

        try {
            connection = Connector.newConnection(config);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

        for (String accountId : getAccountIds(sfdcName)){
            getTickets(accountId);
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

}
