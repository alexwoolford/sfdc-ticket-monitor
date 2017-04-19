package io.woolford.database.mapper;

import io.woolford.database.entity.Ticket;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Component;


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

}