package io.woolford.database.service;

import io.woolford.database.entity.Notification;
import io.woolford.database.entity.Ticket;
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

}