package com.testingbot.bamboo;

import com.atlassian.bamboo.build.BuildDefinition;
import com.atlassian.bamboo.process.EnvironmentVariableAccessor;
import com.atlassian.bamboo.v2.build.BaseConfigurableBuildPlugin;
import com.testingbot.bamboo.config.MappedBuildConfiguration;
import com.testingbot.bamboo.variables.Bamboo3Modifier;
import com.testingbot.bamboo.variables.VariableModifier;
import com.atlassian.bamboo.variable.CustomVariableContext;

public abstract class AbstractTestingBotBuildPlugin extends BaseConfigurableBuildPlugin {

    protected VariableModifier getVariableModifier(
        MappedBuildConfiguration config,
        BuildDefinition definition,
        EnvironmentVariableAccessor environmentVariableAccessor,
        CustomVariableContext customVariableContext
    ) {
        return new Bamboo3Modifier(config, definition, buildContext, environmentVariableAccessor, customVariableContext);
    }
}
