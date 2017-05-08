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
        <th bgcolor="#D1D1D1">case #</th>
        <th bgcolor="#D1D1D1">severity</th>
        <th bgcolor="#D1D1D1">current status</th>
        <th bgcolor="#D1D1D1">product component</th>
        <th bgcolor="#D1D1D1">problem statement/question</th>
        <th bgcolor="#D1D1D1">contact</th>
        <th bgcolor="#D1D1D1">problem type</th>
        <th bgcolor="#D1D1D1">problem sub-type</th>
    </tr>

    <#list openTickets as openTicket>
        <tr>
            <td>
                ${(openTicket.accountName)!}
            </td>
            <td>
                <a href="https://hortonworks.my.salesforce.com/${(openTicket.id)!}?nooverride=1">${(openTicket.caseNumber)!}</a>
            </td>
            <td bgcolor=${sev_color(openTicket.severity)}>
                ${(openTicket.severity)!}
            </td>
            <td>
                ${(openTicket.currentStatusResolution)!}
            </td>
            <td>
                ${(openTicket.productComponent)!}
            </td>
            <td>
                ${(openTicket.problemStatementQuestion)!}
            </td>
            <td>
                ${(openTicket.contactName)!}
            </td>
            <td>
                ${(openTicket.problemType)!}
            </td>
            <td>
                ${(openTicket.problemSubType)!}
            </td>
        </tr>
    </#list>

</table>

</body>
</html>
