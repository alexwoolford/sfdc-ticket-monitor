<!DOCTYPE html>
<html lang="en">
<head>
    <title>SFDC ticket</title>
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
        <th>attribute</th>
        <th>value</th>
    </tr>
    <tr>
        <td>Account Name</td>
        <td>${(accountName)!}</td>
    </tr>
    <tr>
        <td>Case Number</td>
        <td>${(caseNumber)!}</td>
    </tr>
    <tr>
        <td>Severity</td>
        <td>${(severity)!}</td>
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
