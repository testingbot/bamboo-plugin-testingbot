package com.testingbot.bamboo.util;

import com.atlassian.bamboo.build.LogEntry;
import com.atlassian.bamboo.build.logger.LogInterceptor;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.CurrentBuildResult;
import com.testingbot.bamboo.action.PostBuildAction;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class TestingBotLogInterceptor implements LogInterceptor {
    private static final Logger logger = Logger.getLogger(TestingBotLogInterceptor.class);
    private final BuildContext buildContext;


    public TestingBotLogInterceptor(BuildContext buildContext) {
        this.buildContext = buildContext;
    }

    @Override
    public void intercept(@NotNull LogEntry logEntry) {
        if (StringUtils.containsIgnoreCase(logEntry.getLog(), PostBuildAction.TESTINGBOT_SESSION_ID)) {
            logger.info("Adding log entry: " + logEntry.getLog());
            CurrentBuildResult buildResult = buildContext.getBuildResult();
            buildResult.getCustomBuildData().put("TB_JOB_ID_" + System.currentTimeMillis(), logEntry.getLog());
        } else {
            logger.info("Skipping line " + logEntry.getLog());
        }
    }

    @Override
    public void interceptError(@NotNull LogEntry logEntry) {
        if (StringUtils.containsIgnoreCase(logEntry.getLog(), PostBuildAction.TESTINGBOT_SESSION_ID)) {
            logger.info("Adding log entry: " + logEntry.getLog());
            CurrentBuildResult buildResult = buildContext.getBuildResult();
            buildResult.getCustomBuildData().put("TB_JOB_ID_" + System.currentTimeMillis(), logEntry.getLog());
        } else {
            logger.info("Skipping line " + logEntry.getLog());
        }
    }
}
