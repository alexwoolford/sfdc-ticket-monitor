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
    <title>SFDC ticket ${(caseNumber)!} opened by ${(contactName)!}</title>
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
    <tr bgcolor="#D1D1D1">
        <th>attribute</th>
        <th>value</th>
    </tr>
    <tr>
        <td>Account Name</td>
        <td>${(accountName)!}</td>
    </tr>
    <tr>
        <td>Case Number</td>
        <td><a href="https://hortonworks.my.salesforce.com/${(id)!}?nooverride=1">${(caseNumber)!}</a></td>
    </tr>
    <tr>
        <td>Severity</td>
        <td bgcolor=${sev_color(severity)}>${(severity)!}</td>
    </tr>
    <tr>
        <td>Current Status Resolution</td>
        <td>${(currentStatusResolution)!}</td>
    </tr>
    <tr>
        <td>Product Component</td>
        <td>${(productComponent)!}</td>
    </tr>
    <tr>
        <td>Problem Statement/Question</td>
        <td>${(problemStatementQuestion)!}</td>
    </tr>
    <tr>
        <td>Description</td>
        <td>${(description)!}</td>
    </tr>
    <tr>
        <td>Contact Name</td>
        <td>${(contactName)!}</td>
    </tr>
    <tr>
        <td>Problem Type</td>
        <td>${(problemType)!}</td>
    </tr>
    <tr>
        <td>Problem Sub Type</td>
        <td>${(problemSubType)!}</td>
    </tr>
    <tr>
        <td>Status</td>
        <td>${(status)!}</td>
    </tr>
</table>

</body>
</html>
