<html>
<head>
    <title>TestingBot Results</title>
    <meta name="decorator" content="result">
    <meta name="tab" content="testingbot"/>
</head>
<body>

<h3>TestingBot Results</h3>
[#if jobInformation?exists ]
<p span="viewTestingBotBuildResult">
    The following TestingBot tests were executed as part of this build:
</p>
<br />
<table style="width: 100%">
    <tr>
        <th align="left">Test Name</th>
        <th align="left">Test ID</th>
        <th align="left">OS/Browser</th>
        <th align="left">Pass/Fail</th>
    </tr>
    [#list jobInformation as job]
        <tr>
            <td>
                <a href="build/result/viewTestingBotJobResult.action?jobId=${job.getSession_id()}&buildKey=${buildKey}&buildNumber=${buildNumber}">${job.name}</a>
            </td>
            <td>
                <a href="build/result/viewTestingBotJobResult.action?jobId=${job.getSession_id()}&buildKey=${buildKey}&buildNumber=${buildNumber}">${job.getSession_id()}</a>
            </td>
            <td>${job.getOs()} ${job.getBrowser()} ${job.getBrowser_version()}</td>
            <td>[#if job.isSuccess() == true]
                    success
                [#else]
                    fail
                [/#if]
            </td>
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