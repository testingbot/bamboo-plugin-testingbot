<html>
<head>
    <title>Configure TestingBot Plugin</title>
    <meta name="decorator" content="adminpage">
</head>
<body>
<img src="${req.contextPath}/download/resources/com.testingbot.bamboo.bamboo-testingbot-plugin:tbImages/tb_logo.png" border="0"/>
<h1>TestingBot Configuration</h1>

<div class="paddedClearer"></div>
    [@ww.form action="/admin/testingbot/configureTestingBotSave.action"
        id="testingbotConfigurationForm"
        submitLabelKey='global.buttons.update'
        cancelUri='/admin/administer.action']

        [@ui.bambooSection title="Credentials"]
            [@ww.textfield name='key' label='Key' /]
            [@ww.textfield name='secret' label='Secret' /]
        [/@ui.bambooSection]
    [/@ww.form]
</body>
</html>