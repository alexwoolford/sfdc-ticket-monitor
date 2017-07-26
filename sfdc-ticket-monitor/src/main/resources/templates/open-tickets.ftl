<#function sev_color sev_string="">
    <#if sev_string?contains("S1")>
        <#return "#e34a33" />
    <#elseif sev_string?contains("S2")>
        <#return "#fdbb84" />
    <#elseif sev_string?contains("S3")>
        <#return "#fee8c8" />
    <#elseif sev_string?contains("S4")>
        <#return "#a1d99b" />
    <#else>
        <#return "#ffffff" />
    </#if>
</#function>


<!DOCTYPE html>
<html lang="en">
<head>
    <title>open Salesforce.com tickets</title>
    <style>

        .wrap {
            width: 1200px;
        }

        .wrap table {
            width: 1200px;
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
            <td width="70px">case #</td>
            <td width="100px">severity</td>
            <td width="250px">current status</td>
            <td width="100px">product component</td>
            <td width="250px">problem statement/question</td>
            <td width="100px">contact</td>
            <td width="100px">problem type</td>
        </tr>
    </table>

    <div class="inner_table">
        <table>

        <#list openTickets as openTicket>
            <tr>
                <td width="100px">
                    ${(openTicket.accountName)!}
                </td>
                <td width="70px">
                    <a href="https://hortonworks.my.salesforce.com/${(openTicket.id)!}?nooverride=1">${(openTicket.caseNumber)!}</a>
                </td>
                <td bgcolor=${sev_color(openTicket.severity)} width="100px">
                    ${(openTicket.severity)!}
                </td>
                <td width="250px">
                    ${(openTicket.currentStatusResolution)!}
                </td>
                <td width="100px">
                    ${(openTicket.productComponent)!}
                </td>
                <td width="250px">
                    ${(openTicket.problemStatementQuestion)!}
                </td>
                <td width="100px">
                    ${(openTicket.contactName)!}
                </td>
                <td width="100px">
                    ${(openTicket.problemType)!}
                </td>
            </tr>
        </#list>

        </table>

    </div>

</div>

</body>
</html>
