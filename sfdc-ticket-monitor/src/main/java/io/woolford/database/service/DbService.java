package io.woolford.database.service;

import io.woolford.database.entity.*;
import io.woolford.database.mapper.DbMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class DbService {

    private final DbMapper dbMapper;

    @Autowired
    public DbService(DbMapper dbMapper) {
        this.dbMapper = dbMapper;
    }

    public void upsertTicket(Map ticket){
        dbMapper.upsertTicket(ticket);
    }

    public void upsertNotification(Notification notification){
        dbMapper.upsertNotification(notification);
    }

    public List<Ticket> getOpenUnnotifiedTickets(){
        return dbMapper.getOpenUnnotifiedTickets();
    }

    public List<Ticket> getOpenTickets(){
        return dbMapper.getOpenTickets();
    }

    public void upsertAccount(Account account){
        dbMapper.upsertAccount(account);
    }

    public Account getAccountById(String accountId){
        return dbMapper.getAccountById(accountId);
    }

    public List<Account> getAllAccounts(){
        return dbMapper.getAllAccounts();
    }

    public Contact getContactById(String contactId){
        return dbMapper.getContactById(contactId);
    }

    public List<SfdcTableColumn> getSfdcTableColumns(String tableName){
        return dbMapper.getSfdcTableColumns(tableName);
    }

    public void insertRunStats(RunStats runStats){
        dbMapper.insertRunStats(runStats);
    }

    public void upsertBundle(Bundle bundle){
        dbMapper.upsertBundle(bundle);
    }

    public List<String> getDistinctClusterIds() {
        return dbMapper.getDistinctClusterIds();
    }

    public void upsertCluster(Cluster cluster){
        dbMapper.upsertCluster(cluster);
    }

    public List<BundleEnriched> getMostRecentBundles() {
        return dbMapper.getMostRecentBundles();
    }

    public void insertIgnoreTicket(String caseNumber){
        dbMapper.insertIgnoreTicket(caseNumber);
    }

    public void insertIgnoreBundle(String bundleName){
        dbMapper.insertIgnoreBundle(bundleName);
    }

}