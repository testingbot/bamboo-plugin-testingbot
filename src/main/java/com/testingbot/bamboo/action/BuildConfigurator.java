package com.testingbot.bamboo.action;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.CustomPreBuildAction;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.configuration.AdministrationConfiguration;
import com.atlassian.bamboo.configuration.AdministrationConfigurationAccessor;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.v2.build.BaseConfigurableBuildPlugin;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import com.atlassian.spring.container.ContainerManager;
import com.testingbot.bamboo.config.Keys;
import com.testingbot.bamboo.config.MappedBuildConfiguration;
import com.testingbot.bamboo.tunnel.TestingBotTunnel;
import com.testingbot.bamboo.util.TestingBotLogInterceptor;
import com.testingbot.testingbotrest.TestingbotREST;
import com.testingbot.tunnel.Api;
import com.testingbot.tunnel.App;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import net.sf.json.JSONObject;

public class BuildConfigurator extends BaseConfigurableBuildPlugin implements CustomPreBuildAction {

    private static final Logger logger = Logger.getLogger(BuildConfigurator.class);

    public CustomVariableContext getCustomVariableContext() {
        return customVariableContext;
    }

    private CustomVariableContext customVariableContext;

    private AdministrationConfigurationAccessor administrationConfigurationAccessor;

    private PlanManager planManager;

    private static final String DEFAULT_SSH_LOCAL_HOST = "localhost";
    private static final String DEFAULT_SSH_LOCAL_PORT = "8080";

    @NotNull
    @Override
    public BuildContext call() {
        BuildLoggerManager buildLoggerManager = (BuildLoggerManager) ContainerManager.getComponent("buildLoggerManager");
        BuildLogger buildLogger = buildLoggerManager.getLogger(buildContext.getResultKey());
        try {
            final MappedBuildConfiguration config = new MappedBuildConfiguration(buildContext.getBuildDefinition().getCustomConfiguration());
            TestingBotLogInterceptor logInterceptor = new TestingBotLogInterceptor(buildContext);
            buildLogger.getInterceptorStack().add(logInterceptor);
            if (config.isEnabled() && config.isTunnelEnabled()) {
                startTunnel(config);
            }
        } catch (IOException e) {
            logger.error("Error running TestingBot BuildConfigurator", e);
        }
        return buildContext;
    }

    protected TestingbotREST getRESTClient(MappedBuildConfiguration config) {
        return new TestingbotREST(config.getTempKey(), config.getTempSecret());
    }

    public void startTunnel(MappedBuildConfiguration config) throws IOException {
        BuildLoggerManager buildLoggerManager = (BuildLoggerManager) ContainerManager.getComponent("buildLoggerManager");
        final BuildLogger buildLogger = buildLoggerManager.getLogger(buildContext.getResultKey());
        PrintStream printLogger = new PrintStream(new NullOutputStream()) {
            @Override
            public void println(String x) {
                buildLogger.addBuildLogEntry(x);
            }
        };

        App tunnel = new App();
        TestingBotTunnel.tunnel = tunnel;
        AdministrationConfiguration adminConfig = administrationConfigurationAccessor.getAdministrationConfiguration();
        String apiKey = adminConfig.getSystemProperty(Keys.TB_KEY);
        String apiSecret = adminConfig.getSystemProperty(Keys.TB_SECRET);
        tunnel.setClientKey(apiKey);
        tunnel.setClientSecret(apiSecret);
        try {
            tunnel.init();
            tunnel.setFreeJettyPort();
            tunnel.boot();
            Api api = tunnel.getApi();
            JSONObject response;
            boolean ready = false;
            String tunnelID = Integer.toString(tunnel.getTunnelID());
            while (!ready) {
                try {
                    response = api.pollTunnel(tunnelID);
                    ready = response.getString("state").equals("READY");
                } catch (Exception ex) {
                    printLogger.println(ex.getMessage());
                    logger.error(ex);
                    break;
                }
                Thread.sleep(3000);
            }
        } catch (Exception ex) {
            printLogger.println(ex.getMessage());
            logger.error(ex);
        }
    }

    private String getResolvedOptions(String tunnelOptions) {
        String options = tunnelOptions;
        if (options != null) {
            return customVariableContext.substituteString(options, buildContext, null);
        }
        return "";
    }

    @Override
    protected void populateContextForEdit(final Map<String, Object> context, final BuildConfiguration buildConfiguration, final Plan build) {
        populateCommonContext(context);
    }

    @Override
    public void addDefaultValues(@NotNull BuildConfiguration buildConfiguration) {
        super.addDefaultValues(buildConfiguration);

        //only set SSH enabled if we don't have any properties set
        if (!buildConfiguration.getKeys(Keys.CUSTOM_PREFIX).hasNext()) {
            addDefaultStringValue(buildConfiguration, Keys.SSH_ENABLED_KEY, Boolean.TRUE.toString());
            addDefaultStringValue(buildConfiguration, Keys.SSH_VERBOSE_KEY, Boolean.FALSE.toString());

        }
        addDefaultStringValue(buildConfiguration, Keys.SSH_LOCAL_HOST_KEY, DEFAULT_SSH_LOCAL_HOST);
        addDefaultStringValue(buildConfiguration, Keys.SSH_LOCAL_PORTS_KEY, DEFAULT_SSH_LOCAL_PORT);

    }

    private void addDefaultStringValue(BuildConfiguration buildConfiguration, String configurationKey, String defaultValue) {
        if (StringUtils.isBlank(buildConfiguration.getString(configurationKey))) {
            buildConfiguration.setProperty(configurationKey, defaultValue);
        }
    }

    private void populateCommonContext(final Map<String, Object> context) {
        context.put("hasValidTestingBotConfig", hasValidTestingBotConfig());
    }

    public boolean hasValidTestingBotConfig() {
        AdministrationConfiguration adminConfig = administrationConfigurationAccessor.getAdministrationConfiguration();
        return (StringUtils.isNotBlank(adminConfig.getSystemProperty(Keys.TB_KEY))
                && StringUtils.isNotBlank(adminConfig.getSystemProperty(Keys.TB_SECRET)));
    }

    public AdministrationConfigurationAccessor getAdministrationConfigurationAccessor() {
        return administrationConfigurationAccessor;
    }

    public void setAdministrationConfigurationAccessor(AdministrationConfigurationAccessor administrationConfigurationAccessor) {
        this.administrationConfigurationAccessor = administrationConfigurationAccessor;
    }

    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }

    public void setCustomVariableContext(CustomVariableContext customVariableContext) {
        this.customVariableContext = customVariableContext;
    }
}
