<!DOCTYPE html>
<html lang="en">
<head>
    <title>most recent SmartSense bundles</title>
    <style>

        .wrap {
            width: 1450px;
        }

        .wrap table {
            width: 1450px;
            table-layout: fixed;
        }

        table tr td {
            padding: 5px;
            border: 1px solid #eee;
            word-wrap: break-word;
        }

        table.head tr td {
            padding: 5px;
            border: 1px solid #eee;
            word-wrap: break-word;
            background: #eee;
        }

        .inner_table {
            height: 600px;
            overflow-y: auto;
        }

    </style>
</head>
<body>

<div class="wrap">

    <table class="head">

        <tr>
            <td width="100px">account</td>
            <td width="160px">cluster</td>
            <td width="500px">bundle</td>
            <td width="145px">bundle date</td>
            <td width="50px"># masters</td>
            <td width="50px"># slaves</td>
            <td width="150px">used storage</td>
            <td width="150px">total storage</td>
        </tr>

    </table>

    <div class="inner_table">
        <table>

        <#list recentBundles as recentBundle>
            <tr>
                <td width="100px">
                ${(recentBundle.accountName)!}
                </td>
                <td width="160px">
                ${(recentBundle.clusterName)!}
                </td>
                <td width="500px">
                    <a href=https://datalake.smartsense.hortonworks.com/viewer/#/bundles/support/detail?bundle=${(recentBundle.bundleName)!}>${(recentBundle.bundleName)!}</a>
                </td>
                <td width="145px">
                ${(recentBundle.bundleDate?string('yyyy-MM-dd HH:mm:ss'))!}
                </td>
                <td width="50px" align="right">
                ${(recentBundle.numMasters)!}
                </td>
                <td width="50px" align="right">
                ${(recentBundle.numSlaves)!}
                </td>
                <td width="150px" align="right">
                ${(recentBundle.usedStorage)!}
                </td>
                <td width="150px" align="right">
                ${(recentBundle.totalStorage)!}
                </td>
            </tr>
        </#list>

        </table>

    </div>

</div>

</body>
</html>
