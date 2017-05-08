<!DOCTYPE html>
<html lang="en">
<head>
    <title>most recent SmartSense bundles</title>
    <style>
        table, th, td {
            border: 1px solid black;
            border-collapse: collapse;
        }
        th, td {
            padding: 5px;
            text-align: left;
        }
    </style>
</head>
<body>

<table style="width:100%">

    <tr>
        <th bgcolor="#D1D1D1">account</th>
        <th bgcolor="#D1D1D1">cluster</th>
        <th bgcolor="#D1D1D1">bundle</th>
        <th bgcolor="#D1D1D1">bundle date</th>
        <th bgcolor="#D1D1D1"># masters</th>
        <th bgcolor="#D1D1D1"># slaves</th>
        <th bgcolor="#D1D1D1">used storage</th>
        <th bgcolor="#D1D1D1">total storage</th>
    </tr>

<#list recentBundles as recentBundle>
    <tr>
        <td>
        ${(recentBundle.accountName)!}
        </td>
        <td>
        ${(recentBundle.clusterName)!}
        </td>
        <td>
            <a href=https://datalake.smartsense.hortonworks.com/viewer/#/bundles/support/detail?bundle=${(recentBundle.bundleName)!}>${(recentBundle.bundleName)!}</a>
        </td>
        <td>
        ${(recentBundle.bundleDate?string('yyyy-MM-dd HH:mm:ss'))!}
        </td>
        <td>
        ${(recentBundle.numMasters)!}
        </td>
        <td>
        ${(recentBundle.numSlaves)!}
        </td>
        <td>
        ${(recentBundle.usedStorage)!}
        </td>
        <td>
        ${(recentBundle.totalStorage)!}
        </td>
    </tr>
</#list>

</table>

</body>
</html>
