package io.woolford.database.mapper;

import io.woolford.database.entity.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public interface DbMapper {

    @Insert("INSERT INTO sfdc_ticket_monitor.ticket                                                           " +
            "    (`accountId`, `accountName`, `caseNumber`, `id`, `severity`, `productComponent`,             " +
            "     `problemStatementQuestion`, `description`, `currentStatusResolution`, `contactId`,          " +
            "     `contactName`, `priority`, `problemType`, `problemSubType`, `reason`, `status`)             " +
            "VALUES                                                                                           " +
            "    (#{accountid}, #{accountname}, #{casenumber}, #{id}, #{severity}, #{productComponent},       " +
            "     #{problemStatementQuestion}, #{description}, #{currentStatusResolution}, #{contactId},      " +
            "     #{contactname}, #{priority}, #{problemType}, #{problemSubType}, #{reason}, #{status})       " +
            "ON DUPLICATE KEY UPDATE                                                                          " +
            "     accountId=#{accountid}, accountName=#{accountname}, id=#{id}, severity=#{severity},         " +
            "     productComponent=#{productComponent}, problemStatementQuestion=#{problemStatementQuestion}, " +
            "     description=#{description}, currentStatusResolution=#{currentStatusResolution},             " +
            "     contactId=#{contactid}, contactName=#{contactname}, priority=#{priority},                   " +
            "     problemType=#{problemType}, problemSubType=#{problemSubType}, reason=#{reason},             " +
            "     status=#{status}                                                                            ")
    void upsertTicket(Map ticket);

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
            "AND Status = 'Open'                              ")
    List<Ticket> getOpenUnnotifiedTickets();

    @Select("SELECT                          " +
            "  caseNumber,                   " +
            "  id,                           " +
            "  accountId,                    " +
            "  accountName,                  " +
            "  severity,                     " +
            "  productComponent,             " +
            "  problemStatementQuestion,     " +
            "  description,                  " +
            "  currentStatusResolution,      " +
            "  contactId,                    " +
            "  contactName,                  " +
            "  priority,                     " +
            "  problemType,                  " +
            "  problemSubType,               " +
            "  reason,                       " +
            "  status                        " +
            "FROM sfdc_ticket_monitor.ticket " +
            "WHERE Status = 'Open'           ")
    List<Ticket> getOpenTickets();

    @Insert("INSERT INTO sfdc_ticket_monitor.account                  " +
            "    (`accountId`, `accountName`, `customerRecordId2`)    " +
            "VALUES                                                   " +
            "    (#{accountId}, #{accountName}, #{customerRecordId2}) " +
            "ON DUPLICATE KEY UPDATE                                  " +
            "     accountName=#{accountName},                         " +
            "     customerRecordId2=#{customerRecordId2}              ")
    void upsertAccount(Account account);

    @Select("SELECT                            " +
            "  accountId,                      " +
            "  accountName                     " +
            "FROM sfdc_ticket_monitor.account  " +
            "WHERE accountId=#{accountId}      ")
    Account getAccountById(String accountId);

    @Select("SELECT                           " +
            "  accountId,                     " +
            "  accountName,                   " +
            "  CustomerRecordId2              " +
            "FROM sfdc_ticket_monitor.account ")
    List<Account> getAllAccounts();

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

    @Insert("INSERT INTO sfdc_ticket_monitor.run_stats                                            " +
            "    (`startRun`, `endRun`, `sfdcQueries`, `cacheHits`, `exceptions`, `emails`)       " +
            "VALUES                                                                               " +
            "    (#{startRun}, #{endRun}, #{sfdcQueries}, #{cacheHits}, #{exceptions}, #{emails}) ")
    void insertRunStats(RunStats runStats);

    @Insert("INSERT INTO sfdc_ticket_monitor.bundle           " +
            "    (`bundleName`, `bundleDate`, `clusterId`)    " +
            "VALUES                                           " +
            "    (#{bundleName}, #{bundleDate}, #{clusterId}) " +
            "ON DUPLICATE KEY UPDATE                          " +
            "     bundleDate=#{bundleDate},                   " +
            "     clusterId=#{clusterId}                      ")
    void upsertBundle(Bundle bundle);

    @Select("SELECT DISTINCT  " +
            "    clusterId    " +
            "FROM bundle      ")
    List<String> getDistinctClusterIds();

    @Insert("INSERT INTO sfdc_ticket_monitor.cluster                                                          " +
            "    (`clusterId`, `clusterName`, `numMasters`, `numSlaves`, `usedStorage`, `totalStorage`)       " +
            "VALUES                                                                                           " +
            "    (#{clusterId}, #{clusterName}, #{numMasters}, #{numSlaves}, #{usedStorage}, #{totalStorage}) " +
            "ON DUPLICATE KEY UPDATE                                                                          " +
            "     clusterName=#{clusterName},                                                                 " +
            "     numMasters=#{numMasters},                                                                   " +
            "     numSlaves=#{numSlaves},                                                                     " +
            "     usedStorage=#{usedStorage},                                                                 " +
            "     totalStorage=#{totalStorage}                                                                ")
    void upsertCluster(Cluster cluster);

    @Select("SELECT                                                         " +
            "  bundle_a.AccountName,                                        " +
            "  bundle_a.clusterName,                                        " +
            "  bundle_a.bundleName,                                         " +
            "  bundle_a.bundleDate,                                         " +
            "  bundle_a.numMasters,                                         " +
            "  bundle_a.numSlaves,                                          " +
            "  bundle_a.usedStorage,                                        " +
            "  bundle_a.totalStorage                                        " +
            "FROM                                                           " +
            "  (SELECT                                                      " +
            "    AccountName,                                               " +
            "    clusterName,                                               " +
            "    bundleName,                                                " +
            "    bundleDate,                                                " +
            "    numMasters,                                                " +
            "    numSlaves,                                                 " +
            "    usedStorage,                                               " +
            "    totalStorage                                               " +
            "  FROM bundle                                                  " +
            "  INNER JOIN cluster                                           " +
            "  ON bundle.clusterId = cluster.clusterId                      " +
            "  INNER JOIN account                                           " +
            "  ON upper(substring(bundleName, 1, 10)) = CustomerRecordId2   " +
            "  ORDER BY AccountName, clusterName, bundleDate DESC) bundle_a " +
            "LEFT JOIN                                                      " +
            "  (SELECT                                                      " +
            "    AccountName,                                               " +
            "    clusterName,                                               " +
            "    bundleName,                                                " +
            "    bundleDate,                                                " +
            "    numMasters,                                                " +
            "    numSlaves,                                                 " +
            "    usedStorage,                                               " +
            "    totalStorage                                               " +
            "  FROM bundle                                                  " +
            "  INNER JOIN cluster                                           " +
            "  ON bundle.clusterId = cluster.clusterId                      " +
            "  INNER JOIN account                                           " +
            "  ON upper(substring(bundleName, 1, 10)) = CustomerRecordId2   " +
            "  ORDER BY AccountName, clusterName, bundleDate DESC) bundle_b " +
            "ON bundle_a.AccountName = bundle_b.AccountName                 " +
            "AND bundle_a.clusterName = bundle_b.clusterName                " +
            "AND bundle_b.bundleDate > bundle_a.bundleDate                  " +
            "WHERE bundle_b.bundleDate IS NULL                              " +
            "ORDER BY bundleDate DESC                                       ")
    List<BundleEnriched> getMostRecentBundles();

}