SELECT
<#list sfdcTableColumnList as sfdcTableColumnAlias>
${sfdcTableColumnAlias.columnName}<#sep>, </#list>
FROM case
WHERE accountId IN (
<#list accountIdList as accountId>
'${accountId}'<#sep>,
</#list>)
AND lastmodifieddate = LAST_90_DAYS
