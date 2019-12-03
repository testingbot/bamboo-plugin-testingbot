package com.testingbot.bamboo.variables;

import com.atlassian.bamboo.build.BuildDefinition;
import com.atlassian.bamboo.configuration.AdministrationConfiguration;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.variable.VariableContext;
import com.atlassian.bamboo.variable.VariableDefinitionContext;
import com.atlassian.bamboo.variable.VariableType;
import com.testingbot.bamboo.config.Keys;
import com.testingbot.bamboo.config.MappedBuildConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.atlassian.bamboo.variable.CustomVariableContext;

import java.util.HashMap;
import java.util.Map;

public abstract class DefaultVariableModifier implements VariableModifier {

    private static final Logger logger = Logger.getLogger(DefaultVariableModifier.class);

    public CustomVariableContext getCustomVariableContext() {
        return customVariableContext;
    }

    public void setCustomVariableContext(CustomVariableContext customVariableContext) {
        this.customVariableContext = customVariableContext;
    }

    private CustomVariableContext customVariableContext;

    protected static final String EQUALS = "=\"";

    protected MappedBuildConfiguration config;
    protected AdministrationConfigurationManager administrationConfigurationManager;
    protected BuildDefinition definition;
    protected BuildContext buildContext;

    public DefaultVariableModifier(MappedBuildConfiguration config, BuildDefinition definition, BuildContext buildContext, CustomVariableContext customVariableContext) {
        this.config = config;
        this.definition = definition;
        this.buildContext = buildContext;
        this.customVariableContext = customVariableContext;
    }

    protected Map<String, VariableDefinitionContext> createSeleniumVariableContext(VariableContext variableContext) {
        Map<String, VariableDefinitionContext> variables = new HashMap<>();

        AdministrationConfiguration adminConfig = administrationConfigurationManager.getAdministrationConfiguration();
        createCommonEnvironmentVariables(variableContext, adminConfig);
        return variables;
    }

    private void addVariable(VariableContext variables, String key, String value) {
        variables.addLocalVariable(key, value);
        VariableDefinitionContext variableDefinitionContext = variables.getEffectiveVariables().get(key);
        if (variableDefinitionContext != null)
        {
            variableDefinitionContext.setVariableType(VariableType.ENVIRONMENT);
        }
    }

    private void createCommonEnvironmentVariables(VariableContext variables, AdministrationConfiguration adminConfig) {

        if (config.shouldOverrideAuthentication() && StringUtils.isNotEmpty(config.getKey())) {
            config.setTempKey(config.getKey());
        } else {
            config.setTempKey(adminConfig.getSystemProperty(Keys.TB_KEY));
        }

        if (config.shouldOverrideAuthentication() && StringUtils.isNotEmpty(config.getSecret())) {
            config.setTempSecret(config.getSecret());
        } else {
            config.setTempSecret(adminConfig.getSystemProperty(Keys.TB_SECRET));
        }

        addVariable(variables, Keys.SELENIUM_HOST_ENV, config.getSshHost());
        addVariable(variables, Keys.SELENIUM_PORT_ENV, config.getSshPorts());
        addVariable(variables, Keys.TESTINGBOT_KEY, config.getTempKey());
        addVariable(variables, Keys.TB_TESTINGBOT_KEY_ENV, config.getTempKey());
        addVariable(variables, Keys.TESTINGBOT_SECRET, config.getTempSecret());
        addVariable(variables, Keys.TB_TESTINGBOT_SECRET_ENV, config.getTempSecret());
        if (buildContext.getParentBuildContext() == null) {
            addVariable(variables, Keys.BAMBOO_BUILD_NUMBER_ENV, buildContext.getBuildResultKey());
        } else {
            addVariable(variables, Keys.BAMBOO_BUILD_NUMBER_ENV, buildContext.getParentBuildContext().getBuildResultKey());

        }
        if (config.useGeneratedTunnelIdentifier()) {
            addVariable(variables, Keys.TUNNEL_IDENTIFIER, customVariableContext.getVariables(buildContext).get(Keys.TUNNEL_IDENTIFIER));
        }
    }

    public void setAdministrationConfigurationManager(AdministrationConfigurationManager administrationConfigurationManager) {
        this.administrationConfigurationManager = administrationConfigurationManager;
    }


    /**
     * @return String representing the set of environment variables to apply
     */
    protected String createSeleniumEnvironmentVariables() {
        return createSeleniumEnvironmentVariables("");
    }

    /**
     * @param prefix Prefix for each environment variable (eg '-D'), can be null
     * @return String representing the set of environment variables to apply
     * @deprecated
     */
    protected String createSeleniumEnvironmentVariables(String prefix) {
        AdministrationConfiguration adminConfig = administrationConfigurationManager.getAdministrationConfiguration();
        StringBuilder stringBuilder = new StringBuilder();
        createCommonEnvironmentVariables(prefix, stringBuilder, adminConfig);
       
        return stringBuilder.toString();
    }

    private void createCommonEnvironmentVariables(String prefix, StringBuilder stringBuilder, AdministrationConfiguration adminConfig) {
        if (config.shouldOverrideAuthentication() && StringUtils.isNotEmpty(config.getKey())) {
            config.setTempKey(config.getKey());
        } else {
            config.setTempKey(adminConfig.getSystemProperty(Keys.TB_KEY));
        }

        if (config.shouldOverrideAuthentication() && StringUtils.isNotEmpty(config.getSecret())) {
            config.setTempSecret(config.getSecret());
        } else {
            config.setTempSecret(adminConfig.getSystemProperty(Keys.TB_SECRET));
        }

        stringBuilder.append(' ').append(prefix).append(Keys.SELENIUM_HOST_ENV).append(EQUALS).append(config.getSshHost()).append('"');
        stringBuilder.append(' ').append(prefix).append(Keys.SELENIUM_PORT_ENV).append(EQUALS).append(config.getSshPorts()).append('"');
        stringBuilder.append(' ').append(prefix).append(Keys.TB_TESTINGBOT_KEY_ENV).append(EQUALS).append(config.getTempKey()).append('"');
        stringBuilder.append(' ').append(prefix).append(Keys.TESTINGBOT_KEY).append(EQUALS).append(config.getTempKey()).append('"');
        stringBuilder.append(' ').append(prefix).append(Keys.TESTINGBOT_SECRET).append(EQUALS).append(config.getTempSecret()).append('"');
        stringBuilder.append(' ').append(prefix).append(Keys.TB_TESTINGBOT_SECRET_ENV).append(EQUALS).append(config.getTempSecret()).append('"');
        if (buildContext.getParentBuildContext() == null) {
            stringBuilder.append(' ').append(prefix).append(Keys.BAMBOO_BUILD_NUMBER_ENV).append(EQUALS).append(buildContext.getBuildResultKey()).append('"');
        } else {
            stringBuilder.append(' ').append(prefix).append(Keys.BAMBOO_BUILD_NUMBER_ENV).append(EQUALS).append(buildContext.getParentBuildContext().getBuildResultKey()).append('"');
        }
        if (config.useGeneratedTunnelIdentifier()) {
            String tunnelIdentifier = customVariableContext.getVariables(buildContext).get(Keys.TUNNEL_IDENTIFIER);
            stringBuilder.append(' ').append(prefix).append(Keys.TUNNEL_IDENTIFIER).append(EQUALS).append(tunnelIdentifier).append('"');
        }
    }
}
