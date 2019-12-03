[#assign createMode=req.servletPath.contains('/build/admin/create/') /]
<div id="tbGeneralPanel" class="tb_panel">
    <div class="tb_panel_header">General Settings</div>
    <div class="tb_panel_box">
        <div class="helpTextArea">
            <span>
                Configure environment variables below. Specify whether you'd like to automatically start and stop <a href="https://testingbot.com/support/other/tunnel" target="_blank">TestingBot Tunnel</a> for this build.
            </span>
        </div>
    [@ww.checkbox label='Enable TestingBot Tunnel' name='custom.testingbot.ssh.enabled' toggle='true' description='Enabling TestingBot Tunnel will establish a secure connection between your network and TestingBot.' /]

    [@ww.checkbox label='Override Default Authentication' name='custom.testingbot.auth.enabled' toggle='true' description='Specify a different key/secret for this build' /]

    [@ui.bambooSection dependsOn='custom.testingbot.auth.enabled' showOn='true']
        [@ww.textfield name='custom.testingbot.key' label='Key' description="TestingBot Key"/]
        <br clear="all"/>
        [@ww.textfield name='custom.testingbot.secret' label='Secret' description="TestingBot Secret" /]
        <br clear="all"/>
    [/@ui.bambooSection]


    [@ww.textfield name='custom.testingbot.selenium.host' label='Selenium Host' description="The name of the Selenium host to be used. For TestingBot tests, this should be set to `hub.testingbot.com`."/]
        <br clear="all"/>
    [@ww.textfield name='custom.testingbot.selenium.port' label='Selenium Port' description="The name of the Selenium Port to be used. For tests using TestingBot Tunnel, this should be 4445.  If using `hub.testingbot.com` please use port 80." /]
        <br clear="all"/>
    </div>
</div>
