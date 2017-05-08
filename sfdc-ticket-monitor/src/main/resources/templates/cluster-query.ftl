SELECT
  Id,
  Name,
  Num_Masters__c,
  Num_Slaves__c,
  Total_Storage__c,
  Used_Storage__c
FROM Cluster__c
WHERE id IN (
<#list clusterIdList as clusterId>
'${clusterId}'<#sep>,
</#list>)