SELECT
<#list sfdcTableColumnList as sfdcTableColumnAlias>
${sfdcTableColumnAlias.columnName}<#sep>, </#list>
FROM case
WHERE accountId='${accountId}'