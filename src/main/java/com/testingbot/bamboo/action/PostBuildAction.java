package com.testingbot.bamboo.action;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.CustomBuildProcessor;
import com.atlassian.bamboo.build.LogEntry;
import com.atlassian.bamboo.storage.StorageLocationService;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.resultsummary.tests.TestState;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.CurrentBuildResult;
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.atlassian.spring.container.ContainerManager;
import com.testingbot.bamboo.AbstractTestingBotBuildPlugin;
import com.testingbot.bamboo.config.MappedBuildConfiguration;
import com.testingbot.bamboo.tunnel.TestingBotTunnel;
import com.testingbot.models.TestingbotTest;
import com.testingbot.testingbotrest.TestingbotApiException;
import com.testingbot.testingbotrest.TestingbotREST;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostBuildAction extends AbstractTestingBotBuildPlugin implements CustomBuildProcessor {

    private static final Logger logger = Logger.getLogger(PostBuildAction.class);
    public static final String TESTINGBOT_SESSION_ID = "TestingBotSessionID";
    private static final Pattern SESSION_ID_PATTERN = Pattern.compile("TestingBotSessionID=([0-9A-z\\-]+)(?:.test-name=(.*))?");
    private static final String JOB_NAME_PATTERN = "\\b({0})\\b";

    private PlanManager planManager;
    private BuildLoggerManager buildLoggerManager;
    private StorageLocationService storageLocationService;

    private CustomVariableContext customVariableContext;

    protected MappedBuildConfiguration getBuildConfiguration(BuildContext buildContext) {
        return new MappedBuildConfiguration(buildContext.getBuildDefinition().getCustomConfiguration());
    }

    @NotNull
    @Override
    public BuildContext call() {
        logger.info("TB PostBuildAction");
        final MappedBuildConfiguration config = getBuildConfiguration(buildContext);
        if (config.isEnabled()) {
            try {
                recordJobResult(config);
            } catch (IOException e) {
                logger.error(e);
            }
            if (config.isTunnelEnabled()) {
                final BuildLogger buildLogger = getBuildLoggerManager().getLogger(buildContext.getResultKey());
                PrintStream printLogger = new PrintStream(new NullOutputStream()) {
                    @Override
                    public void println(String x) {
                        buildLogger.addBuildLogEntry(x);
                    }
                };
                if (TestingBotTunnel.tunnel != null) {
                    TestingBotTunnel.tunnel.stop();
                    TestingBotTunnel.tunnel = null;
                }
            }
        }
        return buildContext;
    }

    @Override
    public void init(@NotNull BuildContext context) {
        this.buildContext = context;
    }

    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }

    protected void recordJobResult(MappedBuildConfiguration config) throws IOException {
        boolean foundLogEntry = false;
        logger.info("Checking log interceptor entries");
        CurrentBuildResult buildResult = buildContext.getBuildResult();
        for (Map.Entry<String, String> entry : buildResult.getCustomBuildData().entrySet()) {
            if (entry.getKey().contains("TB_JOB_ID")) {
                if (processLine(config, entry.getValue())) {
                    foundLogEntry = true;
                }

            }
        }

        logger.info("Reading from log file");
        // try reading from the log file directly
        final StorageLocationService storageLocationService = getStorageLocationService();
        File logFile = storageLocationService.getLogFile(buildContext.getPlanResultKey());
        List lines = FileUtils.readLines(logFile);
        for (Object object : lines) {
            String line = (String) object;
            if (logger.isDebugEnabled()) {
                logger.debug("Processing line: " + line);
            }
            if (processLine(config, line)) {
                foundLogEntry = true;
            }
        }

        logger.info("Reading from build logger output");
        BuildLogger buildLogger = buildLoggerManager.getLogger(buildContext.getResultKey());
        for (LogEntry logEntry : buildLogger.getBuildLog()) {
            if (processLine(config, logEntry.getLog())) {
                foundLogEntry = true;
            }
        }

        if (!foundLogEntry) {
            logger.warn("No TestingBot Session IDs found in the build output");
        }
    }

    protected boolean processLine(MappedBuildConfiguration config, String line) {
        String sessionId = null;
        String jobName = null;
        Matcher m = SESSION_ID_PATTERN.matcher(line);
        while (m.find()) {
            sessionId = m.group(1);
            if (m.groupCount() == 2) {
                jobName = m.group(2);
            }
        }

        if (sessionId == null) {
            sessionId = StringUtils.substringBetween(line, TESTINGBOT_SESSION_ID + "=", " ");
        }
        if (sessionId == null) {
            sessionId = StringUtils.substringAfter(line, TESTINGBOT_SESSION_ID + "=");
        }
        if (sessionId != null && !sessionId.equalsIgnoreCase("null")) {
            if (sessionId.trim().equals("")) {
                logger.error("Session id for line" + line + " was blank");
                return false;
            } else {
                storeBuildNumber(config, sessionId, jobName);
                return true;
            }
        }
        return false;
    }

    /**
     * Receives a sessionId, check TB API and update there
     **/
    protected void storeBuildNumber(MappedBuildConfiguration config, String sessionId, String jobName) {
        TestingbotREST tbClient = getRESTClient(config);

        logger.info("Updating Test " + sessionId + " via TestingBot API");
        try {
            TestingbotTest testInformation = tbClient.getTest(sessionId);
            if (!testInformation.getName().isEmpty()) {
                Boolean testPassed = hasTestPassed(testInformation.getName());
                if (testPassed != null) {
                    testInformation.setSuccess(testPassed);
                }
            } else {
                testInformation.setName(jobName);
            }

            if (testInformation.getBuild() == null || testInformation.getBuild().isEmpty()) {
                testInformation.setBuild(getBuildNumber());
            }

            tbClient.updateTest(testInformation);
        } catch (TestingbotApiException ex) {
            
        }
    }

    protected TestingbotREST getRESTClient(MappedBuildConfiguration config) {
        return new TestingbotREST(config.getTempKey(), config.getTempSecret());
    }

    private Boolean hasTestPassed(String name) {
        TestResults testResults = findTestResult(name);
        if (testResults != null) {
            return testResults.getState().equals(TestState.SUCCESS);
        }

        return (buildContext.getBuildResult().getBuildState().equals(BuildState.SUCCESS));
    }

    private TestResults findTestResult(String name) {
        if (name == null) {
            return null;
        }
        TestResults testResult = findTestResult(name, buildContext.getBuildResult().getFailedTestResults());
        if (testResult == null) {
            testResult = findTestResult(name, buildContext.getBuildResult().getSuccessfulTestResults());
        }
        return testResult;
    }

    private TestResults findTestResult(String name, Collection<TestResults> testResults) {
        for (TestResults testResult : testResults) {
            Pattern jobNamePattern = Pattern.compile(MessageFormat.format(JOB_NAME_PATTERN, name));
            Matcher matcher = jobNamePattern.matcher(testResult.getActualMethodName());
            if (name.equals(testResult.getActualMethodName()) //if job name equals full name of test
                    || name.contains(testResult.getActualMethodName()) //or if job name contains the test name
                    || matcher.find()) { //or if the full name of the test contains the job name (matching whole words only)
                //then we have a match
                return testResult;
            }
        }
        return null;
    }

    protected String getBuildNumber() {
        return getBuildContextToUse().getBuildResultKey();
    }

    private BuildContext getBuildContextToUse() {
        return buildContext.getParentBuildContext() == null ? buildContext : buildContext.getParentBuildContext();
    }

    public BuildLoggerManager getBuildLoggerManager() {
        if (buildLoggerManager == null) {
            buildLoggerManager = (BuildLoggerManager) ContainerManager.getComponent("buildLoggerManager");
        }
        return buildLoggerManager;
    }

    public StorageLocationService getStorageLocationService() {
        if (storageLocationService == null) {
            storageLocationService = (StorageLocationService) ContainerManager.getComponent("storageLocationService");
        }
        return storageLocationService;
    }

    public void setBuildLoggerManager(BuildLoggerManager buildLoggerManager) {
        this.buildLoggerManager = buildLoggerManager;
    }

    public void setCustomVariableContext(CustomVariableContext customVariableContext) {
        this.customVariableContext = customVariableContext;
    }
}
