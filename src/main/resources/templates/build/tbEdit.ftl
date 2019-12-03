${webResourceManager.requireResource("com.testingbot.bamboo.bamboo-testingbot-plugin:tbCSS")}
${webResourceManager.requireResource("com.testingbot.bamboo.bamboo-testingbot-plugin:tbJS")}

<!--[if IE 7]>
<style type="text/css">
    #tbTabs > ul > li.selected{
        border-right: 1px solid #fff !important;
    }
    #tbTabs > ul > li {
        border-right: 1px solid #ddd !important;
    }
    #tbTabs > div {
        z-index: -1 !important;
        left:1px;
    }
</style>
<![endif]-->

[@ui.bambooSection title='<img src="${req.contextPath}/download/resources/com.testingbot.bamboo.bamboo-testingbot-plugin:tbImages/tb_icon.png" />&nbsp;&nbsp;TestingBot' ]
    [#assign createMode=req.servletPath.contains('/build/admin/create/') /]

    [#if hasValidTestingBotConfig]
        [@ww.checkbox label='Enable TestingBot' name='custom.testingbot.enabled' toggle='true' description='Enable Selenium testing with TestingBot' /]

        [@ui.bambooSection dependsOn='custom.testingbot.enabled' showOn='true']

        <div>
            [#include "tbGeneralPanel.ftl"]
            [#include "tbVariablePanel.ftl"]
        </div>


        [/@ui.bambooSection]
    [#else]
        <div class="warningBox">You must configure your <a href="${req.contextPath}/admin/testingbot/configureTestingBot.action">TestingBot settings</a> in the Bamboo Administration area to use this plugin.</div>
    [/#if]
[/@ui.bambooSection]
