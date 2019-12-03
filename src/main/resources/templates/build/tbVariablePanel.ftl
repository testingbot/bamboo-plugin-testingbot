[#assign createMode=req.servletPath.contains('/build/admin/create/') /]
<div id="tbVariablePanel" class="tb_panel">
    <div class="tb_panel_header">Environment Variables</div>
    <div class="tb_panel_box">
        <div class="helpTextArea">
            <span>
                The TestingBot plugin sets several environment variables, which can be used by your tests. An example on how to use these variables in your (Java) tests:
                <pre>System.getenv("bamboo_TESTINGBOT_KEY")</pre>
            </span>
        </div>
        <br />
    [@ui.bambooSection title='Available Environment Variables']
        <div class="helpTextArea">
            <strong>bamboo_SELENIUM_HOST</strong> - The hostname of the selenium server<br/><br/>
            <strong>bamboo_SELENIUM_PORT</strong> - The port of the selenium server<br/><br/>
            <strong>bamboo_TESTINGBOT_KEY</strong> - The TestingBot API Key configured for this Bamboo build.<br/><br/>
            <strong>bamboo_TESTINGBOT_SECRET</strong> - The TestingBot API Secret configured for this Bamboo build.<br/><br/>
        </div>
    [/@ui.bambooSection]

        <div class="clearer"></div>
    </div>
</div>
