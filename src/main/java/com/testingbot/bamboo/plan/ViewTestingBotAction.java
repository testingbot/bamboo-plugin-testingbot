package com.testingbot.bamboo.plan;

import com.atlassian.bamboo.plan.cache.ImmutableChain;
import com.atlassian.bamboo.plan.cache.ImmutableJob;
import com.atlassian.bamboo.plan.cache.ImmutablePlan;

import com.atlassian.bamboo.build.ViewBuildResults;
import com.atlassian.bamboo.configuration.AdministrationConfiguration;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.plan.PlanKeys;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.testingbot.bamboo.config.Keys;
import com.testingbot.bamboo.config.MappedBuildConfiguration;
import com.testingbot.bamboo.models.TestingbotTest;
import com.testingbot.bamboo.models.TestingbotTestBuildCollection;
import com.testingbot.testingbotrest.TestingbotApiException;
import com.testingbot.testingbotrest.TestingbotREST;
import com.testingbot.testingbotrest.TestingbotUnauthorizedException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.iharder.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class ViewTestingBotAction extends ViewBuildResults {

    private static final Logger logger = Logger.getLogger(ViewTestingBotAction.class);

    private AdministrationConfigurationManager administrationConfigurationManager;

    private List<TestingbotTest> jobInformation;

    @Override
    public String doDefault() throws Exception {
        String key, secret;
        logger.info("Trying to get the TestingBot tests in this build");

        jobInformation = new ArrayList<>();

        ImmutablePlan plan = getImmutablePlan();
        if (plan instanceof ImmutableChain) {
            List<ImmutableChain> chains = cachedPlanManager.getPlansByProject(getImmutablePlan().getProject(), ImmutableChain.class);
            for (ImmutableJob job : ((ImmutableChain) plan).getAllJobs()) {
                final MappedBuildConfiguration config = new MappedBuildConfiguration(job.getBuildDefinition().getCustomConfiguration());
                if (StringUtils.isNotEmpty(config.getKey())) {
                    key = config.getKey();
                    secret = config.getSecret();
                    jobInformation.addAll(retrieveJobIds(key, secret));
                }
                if (!jobInformation.isEmpty()) {
                    break;
                }
            }
        }
        if (jobInformation.isEmpty()) {
            AdministrationConfiguration adminConfig = administrationConfigurationManager.getAdministrationConfiguration();

            key = adminConfig.getSystemProperty(Keys.TB_KEY);
            secret = adminConfig.getSystemProperty(Keys.TB_SECRET);
            jobInformation.addAll(retrieveJobIds(key, secret));
        }

        return super.doDefault();
    }

    /**
     * Gets tests for a specific build from TestingBot
     *
     * @param key
     * @param secret
     * @param buildIdentifier
     * @return response The API response
     */
    public TestingbotTestBuildCollection getTestsForBuild(String key, String secret, String buildIdentifier) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = key + ":" + secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/builds/" + buildIdentifier);
            getRequest.setHeader("Authorization", "Basic " + encoding);

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            if (response.getStatusLine().getStatusCode() > 200) {
                throw new TestingbotUnauthorizedException();
            }
            com.google.gson.Gson gson = new com.google.gson.Gson();
            return gson.fromJson(sb.toString(), TestingbotTestBuildCollection.class);
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private ArrayList<TestingbotTest> retrieveJobIds(String key, String secret) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String buildName = PlanKeys.getPlanResultKey(resultsSummary.getPlanKey(), getResultsSummary().getBuildNumber()).getKey();
        try {
            TestingbotTestBuildCollection testCollection = getTestsForBuild(key, secret, buildName);
            return testCollection.getData();
        } catch (TestingbotApiException ex) {
            return new ArrayList<>();
        }
    }

    @Override
    public void setAdministrationConfigurationManager(AdministrationConfigurationManager administrationConfigurationManager) {
        this.administrationConfigurationManager = administrationConfigurationManager;
    }

    public List<TestingbotTest> getJobInformation() {
        return jobInformation;
    }

    @Override
    public boolean isRestartable(@NotNull ResultsSummary resultsSummary) {
        return false;
    }
}