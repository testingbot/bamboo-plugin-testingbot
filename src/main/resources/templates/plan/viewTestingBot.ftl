<html>
<head>
    <title>TestingBot Results</title>
    <meta name="decorator" content="plan">
</head>
<body>
[@cp.resultsSubMenu selectedTab='testingbot' /]

[#if jobInformation?exists ]
<table style="width: 100%">
    <tr>
      <th align="left">Test Name</th>
      <th align="left">OS/Browser</th>
      <th align="left">Pass/Fail</th>
    </tr>
    [#list jobInformation as job]
        <tr>
            <td>
                <a href="build/result/viewTestingBotJobResult.action?job=${job.sessionId}">${job.name}</a>
            </td>
            <td>
                <a href="build/result/viewTestingBotJobResult.action?job=${job.sessionId}">${job.sessionId}</a>
            </td>
            <td>${job.getOs()} ${job.getBrowser()} ${job.getVersion()}</td>
            <td>[#if job.isSuccess()]success [#else] fail[/#if]</td>
        </tr>
    [/#list]
</table>
[#else]

<p>
    Unable to find a TestingBot Test result for ${buildKey}.
</p>


[/#if]
</body>
</html>