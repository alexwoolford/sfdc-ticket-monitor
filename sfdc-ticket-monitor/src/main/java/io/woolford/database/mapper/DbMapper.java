package io.woolford.database.mapper;

import io.woolford.database.entity.Notification;
import io.woolford.database.entity.Ticket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface DbMapper {

    @Insert("INSERT INTO sfdc_ticket_monitor.ticket                                          " +
            "    (`CaseNumber`, `AccountName`, `AccountId`, `ContactId`, `Description`,      " +
            "      `Priority`, `Problem_Type__c`, `Reason`, `Status`)                        " +
            "VALUES                                                                          " +
            "    (#{caseNumber}, #{accountName}, #{accountId}, #{contactId}, #{description}, " +
            "     #{priority}, #{problemType}, #{reason}, #{status})                         " +
            "ON DUPLICATE KEY UPDATE accountName=#{accountName}, accountId=#{accountId},     " +
            "                        contactId=#{contactId}, description=#{description},     " +
            "                        priority=#{priority}, problem_Type__c=#{problemType},   " +
            "                        reason=#{reason}, status=#{status}                      ")
    public void upsertTicket(Ticket ticket);
    // TODO: LastViewedDate temporarily removed

    @Insert("INSERT INTO sfdc_ticket_monitor.notification                  " +
            "    (`CaseNumber`, `NotificationSent`)                        " +
            "VALUES                                                        " +
            "    (#{caseNumber}, #{notificationSent})                      " +
            "ON DUPLICATE KEY UPDATE notificationSent=#{notificationSent}  ")
    public void upsertNotification(Notification notification);


    @Select("SELECT                                           " +
            "  ticket.CaseNumber,                             " +
            "  ticket.AccountName,                            " +
            "  ticket.AccountId,                              " +
            "  ticket.ContactId,                              " +
            "  ticket.Description,                            " +
            "  ticket.Priority,                               " +
            "  ticket.Problem_Type__c as ProblemType,         " +
            "  ticket.Reason,                                 " +
            "  ticket.Status                                  " +
            "FROM sfdc_ticket_monitor.ticket                  " +
            "LEFT OUTER JOIN sfdc_ticket_monitor.notification " +
            "ON ticket.CaseNumber = notification.CaseNumber   " +
            "WHERE notification.CaseNumber IS NULL            " +
            "AND Status = 'Open'")
    public List<Ticket> getOpenUnnotifiedTickets();


}