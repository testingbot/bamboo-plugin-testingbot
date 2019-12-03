package com.testingbot.bamboo.plan;

import com.atlassian.bamboo.build.ViewBuildResults;
import com.atlassian.bamboo.configuration.AdministrationConfiguration;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;

import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.testingbot.bamboo.config.Keys;
import com.testingbot.bamboo.models.TestingbotTest;
import com.testingbot.testingbotrest.TestingbotREST;
import org.jetbrains.annotations.NotNull;

public class ViewTestingBotTestAction extends ViewBuildResults {

    private AdministrationConfigurationManager administrationConfigurationManager;

    private TestingbotTest jobInformation;
    
    private String hmac;

    private String jobId;

    @Override
    public String doDefault() throws Exception {
        jobInformation = new TestingbotTest();
        jobInformation.setSession_id(jobId);
        AdministrationConfiguration adminConfig = administrationConfigurationManager.getAdministrationConfiguration();
        String key = adminConfig.getSystemProperty(Keys.TB_KEY);
        String secret = adminConfig.getSystemProperty(Keys.TB_SECRET);
        TestingbotREST tbClient = new TestingbotREST(key, secret);
        hmac = tbClient.getAuthenticationHash(jobId);
        
        return super.doDefault();
    }

    @Override
    public void setAdministrationConfigurationManager(AdministrationConfigurationManager administrationConfigurationManager) {
        this.administrationConfigurationManager = administrationConfigurationManager;
    }

    public TestingbotTest getJobInformation() {
        return jobInformation;
    }
    
    public String getHmac() {
        return hmac;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public boolean isRestartable(@NotNull ResultsSummary resultsSummary) {
        return false;
    }
}
