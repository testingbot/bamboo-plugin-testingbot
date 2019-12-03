package com.testingbot.bamboo.variables;

import com.atlassian.bamboo.build.BuildDefinition;
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.atlassian.bamboo.process.EnvironmentVariableAccessor;
import com.atlassian.bamboo.process.EnvironmentVariableAccessorImpl;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.variable.VariableContext;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.testingbot.bamboo.config.Keys;
import com.testingbot.bamboo.config.MappedBuildConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class Bamboo3Modifier extends DefaultVariableModifier {
    private static final Logger logger = Logger.getLogger(Bamboo3Modifier.class);

    private final EnvironmentVariableAccessor environmentVariableAccessor;

    private static enum CreateEnvironmentAssignment implements Function<Map.Entry<String, String>, String> {
        INSTANCE;
        private CreateEnvironmentAssignment() {
        }

        @Override
        public String apply(@Nullable Map.Entry<String, String> input) {
            return String.format("%s=\"%s\"", new Object[]{
                EnvironmentVariableAccessorImpl.forceLegalIdentifier((String)((Map.Entry) Preconditions.checkNotNull(input)).getKey()),
                ((Map.Entry)Preconditions.checkNotNull(input)).getValue()
            });
        }
    }

    public Bamboo3Modifier(
        MappedBuildConfiguration config,
        BuildDefinition definition,
        BuildContext buildContext,
        EnvironmentVariableAccessor environmentVariableAccessor,
        CustomVariableContext customVariableContext
    ) {
        super(config, definition, buildContext, customVariableContext);
        this.environmentVariableAccessor = environmentVariableAccessor;
    }


    @Override
    public void populateVariables(VariableContext variableContext) {
        createSeleniumVariableContext(variableContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeVariables() {
        String envBuffer = createSeleniumEnvironmentVariables();
        Map<String, String> envMap = environmentVariableAccessor.splitEnvironmentAssignments(envBuffer, false);

        try {
            Class taskDefinitionClass = TaskDefinition.class;
            if (taskDefinitionClass != null) {
                List<TaskDefinition> taskDefinitions = definition.getTaskDefinitions();
                for (TaskDefinition taskDefinition : taskDefinitions) {
                    Map<String, String> configuration = taskDefinition.getConfiguration();
                    String originalEnv = StringUtils.defaultString((String) configuration.get("environmentVariables"));

                    Map<String, String> origMap = environmentVariableAccessor.splitEnvironmentAssignments(originalEnv, false);
                    for (Map.Entry<String, String> entry : envMap.entrySet())
                    {
                        if (entry.getKey().startsWith("SELENIUM_") || entry.getKey().startsWith("TB_") || entry.getKey().startsWith("TESTINGBOT_") || entry.getKey().equals(Keys.TUNNEL_IDENTIFIER)) {
                            origMap.put(entry.getKey(), "${bamboo." + entry.getKey() + "}");

                        }
                    }
                    
                    configuration.put(
                        "environmentVariables",
                        joinEnvMap(origMap)
                    );
                }
            }
        } catch (Exception e) {
            //ignore and attempt to continue
            logger.warn("Unable to process environment variables", e);
        }
    }

    @Nullable
    private static String joinEnvMap(Map<String, String> origMap) {
        return org.apache.commons.lang3.StringUtils.join(Iterables.transform(
            origMap.entrySet(),
            CreateEnvironmentAssignment.INSTANCE).iterator(),
            " "
        );
    }
}
