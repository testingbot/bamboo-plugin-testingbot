package com.testingbot.bamboo.action;

import com.atlassian.bamboo.buildqueue.manager.CustomPreBuildQueuedAction;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.process.EnvironmentVariableAccessor;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.testingbot.bamboo.AbstractTestingBotBuildPlugin;
import com.testingbot.bamboo.config.Keys;
import com.testingbot.bamboo.config.MappedBuildConfiguration;
import com.testingbot.bamboo.variables.VariableModifier;
import com.atlassian.bamboo.variable.CustomVariableContext;
import org.jetbrains.annotations.NotNull;

public class EnvironmentConfigurator extends AbstractTestingBotBuildPlugin implements CustomPreBuildQueuedAction {

    private AdministrationConfigurationManager administrationConfigurationManager;

    private EnvironmentVariableAccessor environmentVariableAccessor;
    private CustomVariableContext customVariableContext;

    public EnvironmentConfigurator() {
        super();
    }

    public CustomVariableContext getCustomVariableContext() {
        return customVariableContext;
    }

    public void setCustomVariableContext(CustomVariableContext customVariableContext) {
        this.customVariableContext = customVariableContext;
    }

    @NotNull
    @Override
    public BuildContext call() {
        final MappedBuildConfiguration config = new MappedBuildConfiguration(buildContext.getBuildDefinition().getCustomConfiguration());
        // setting unique tunnel identifier
        if (config.isEnabled() && config.useGeneratedTunnelIdentifier()) {
            String tunnelIdentifier = generateTunnelIdentifier(buildContext.getPlanName());
            customVariableContext.addCustomData(Keys.TUNNEL_IDENTIFIER, tunnelIdentifier);
        }

        if (config.isEnabled()) {
            setSeleniumEnvironmentVars(config);
        }
        return buildContext;
    }

    private void setSeleniumEnvironmentVars(MappedBuildConfiguration config){
        VariableModifier variableModifier = getVariableModifier(config, buildContext.getBuildDefinition(), environmentVariableAccessor, customVariableContext);
        variableModifier.setAdministrationConfigurationManager(administrationConfigurationManager);
        variableModifier.storeVariables();
        variableModifier.populateVariables(buildContext.getVariableContext());
    }

    public void setAdministrationConfigurationManager(AdministrationConfigurationManager administrationConfigurationManager) {
        this.administrationConfigurationManager = administrationConfigurationManager;
    }

    public EnvironmentVariableAccessor getEnvironmentVariableAccessor() {
        return environmentVariableAccessor;
    }

    public void setEnvironmentVariableAccessor(EnvironmentVariableAccessor environmentVariableAccessor) {
        this.environmentVariableAccessor = environmentVariableAccessor;
    }
    //only allow word, digit, and hyphen characters
    private final String PATTERN_DISALLOWED_TUNNEL_ID_CHARS = "[^\\w\\d-]+";

    private String generateTunnelIdentifier(final String projectName) {
        String sanitizedName = projectName.replaceAll(PATTERN_DISALLOWED_TUNNEL_ID_CHARS, "_");
        return sanitizedName + "-" + System.currentTimeMillis();
    }

}
