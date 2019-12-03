package com.testingbot.bamboo.variables;

import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.variable.VariableContext;

public interface VariableModifier {
    void storeVariables();
    void setAdministrationConfigurationManager(AdministrationConfigurationManager administrationConfigurationManager);
    void populateVariables(VariableContext variableContext);
}