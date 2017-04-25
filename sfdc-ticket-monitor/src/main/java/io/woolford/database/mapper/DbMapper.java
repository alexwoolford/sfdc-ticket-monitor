package io.woolford.database.mapper;

import io.woolford.database.entity.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface DbMapper {

    @Insert("INSERT INTO sfdc_ticket_monitor.ticket                                                           " +
            "    (`accountId`, `accountName`, `caseNumber`, `id`, `severity`, `productComponent`,             " +
            "     `problemStatementQuestion`, `description`, `currentStatusResolution`, `contactId`,          " +
            "     `contactName`, `priority`, `problemType`, `problemSubType`, `reason`, `status`)             " +
            "VALUES                                                                                           " +
            "    (#{accountId}, #{accountName}, #{caseNumber}, #{id}, #{severity}, #{productComponent},       " +
            "     #{problemStatementQuestion}, #{description}, #{currentStatusResolution}, #{contactId},      " +
            "     #{contactName}, #{priority}, #{problemType}, #{problemSubType}, #{reason}, #{status})       " +
            "ON DUPLICATE KEY UPDATE                                                                          " +
            "     accountId=#{accountId}, accountName=#{accountName}, id=#{id}, severity=#{severity},         " +
            "     productComponent=#{productComponent}, problemStatementQuestion=#{problemStatementQuestion}, " +
            "     description=#{description}, currentStatusResolution=#{currentStatusResolution},             " +
            "     contactId=#{contactId}, contactName=#{contactName}, priority=#{priority},                   " +
            "     problemType=#{problemType}, problemSubType=#{problemSubType}, reason=#{reason},             " +
            "     status=#{status}                                                                            ")
    void upsertTicket(Ticket ticket);


    @Insert("INSERT INTO sfdc_ticket_monitor.notification                  " +
            "    (`caseNumber`, `notificationSent`)                        " +
            "VALUES                                                        " +
            "    (#{caseNumber}, #{notificationSent})                      " +
            "ON DUPLICATE KEY UPDATE notificationSent=#{notificationSent}  ")
    void upsertNotification(Notification notification);

    @Select("SELECT                                           " +
            "  ticket.caseNumber,                             " +
            "  ticket.id,                                     " +
            "  ticket.accountId,                              " +
            "  ticket.accountName,                            " +
            "  ticket.severity,                               " +
            "  ticket.productComponent,                       " +
            "  ticket.problemStatementQuestion,               " +
            "  ticket.description,                            " +
            "  ticket.currentStatusResolution,                " +
            "  ticket.contactId,                              " +
            "  ticket.contactName,                            " +
            "  ticket.priority,                               " +
            "  ticket.problemType,                            " +
            "  ticket.problemSubType,                         " +
            "  ticket.reason,                                 " +
            "  ticket.status                                  " +
            "FROM sfdc_ticket_monitor.ticket                  " +
            "LEFT OUTER JOIN sfdc_ticket_monitor.notification " +
            "ON ticket.CaseNumber = notification.CaseNumber   " +
            "WHERE notification.CaseNumber IS NULL            " +
            "AND Status = 'Open'")
    List<Ticket> getOpenUnnotifiedTickets();

    @Insert("INSERT INTO sfdc_ticket_monitor.account            " +
            "    (`accountId`, `accountName`)                   " +
            "VALUES                                             " +
            "    (#{accountId}, #{accountName})                 " +
            "ON DUPLICATE KEY UPDATE accountName=#{accountName} ")
    void upsertAccount(Account account);

    @Select("SELECT                            " +
            "  accountId,                      " +
            "  accountName                     " +
            "FROM sfdc_ticket_monitor.account  " +
            "WHERE accountId=#{accountId}      ")
    Account getAccountById(String accountId);

    @Insert("INSERT INTO sfdc_ticket_monitor.contact            " +
            "    (`contactId`, `contactName`)                   " +
            "VALUES                                             " +
            "    (#{contactId}, #{contactName})                 " +
            "ON DUPLICATE KEY UPDATE contactName=#{contactName} ")
    void upsertContact(Contact contact);

    @Select("SELECT                            " +
            "  contactId,                      " +
            "  contactName                     " +
            "FROM sfdc_ticket_monitor.contact  " +
            "WHERE contactId=#{contactId}      ")
    Contact getContactById(String contactId);

    @Select("SELECT                                     " +
            "  TableName,                               " +
            "  ColumnName                               " +
            "FROM sfdc_ticket_monitor.sfdc_table_column " +
            "WHERE TableName=#{tableName}               ")
    List<SfdcTableColumn> getSfdcTableColumns(String tableName);

}