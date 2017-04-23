package io.woolford.database.service;

import io.woolford.database.entity.*;
import io.woolford.database.mapper.DbMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbService {

    @Autowired
    private DbMapper dbMapper;

    public void upsertTicket(Ticket ticket){
        dbMapper.upsertTicket(ticket);
    }

    public void upsertNotification(Notification notification){
        dbMapper.upsertNotification(notification);
    }

    public List<Ticket> getOpenUnnotifiedTickets(){
        return dbMapper.getOpenUnnotifiedTickets();
    }

    public void upsertAccount(Account account){
        dbMapper.upsertAccount(account);
    }

    public Account getAccountById(String accountId){
        return dbMapper.getAccountById(accountId);
    }

    public Contact getContactById(String contactId){
        return dbMapper.getContactById(contactId);
    }

    public List<SfdcTableColumn> getSfdcTableColumns(String tableName){
        return dbMapper.getSfdcTableColumns(tableName);
    }

}