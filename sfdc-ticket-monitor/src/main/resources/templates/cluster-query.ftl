SELECT
  Name,
  Bundle_Date__c,
  Cluster__c
FROM Bundle__c
WHERE Name LIKE <#list customerRecordId2List as customerRecordId2>
'${customerRecordId2}%'<#sep> OR Name LIKE</#list>