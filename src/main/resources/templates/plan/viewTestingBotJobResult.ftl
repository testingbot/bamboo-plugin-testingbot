<html>
<head>
    <title>TestingBot Result</title>
    <meta name="decorator" content="result">
    <meta name="tab" content="testingbot"/>
</head>
<body>
[#if jobInformation?exists ]
<h3>Results for ${jobInformation.jobId}</h3>

<iframe src="https://testingbot.com/mini/${jobInformation.getSession_id()}?auth=${hmac}&ref=bamboo" style="width: 100%; height: 1000px; border: 0"></iframe>

[#else]

<p>
Unable to find a TestingBot result for ${buildKey}. Please verify if this URL contains the necessary parameters.
<br />
For more help, please <a href="https://testingbot.com/contact/new">contact TestingBot</a>.
</p>
[/#if]
</body>
</html>