package com.testingbot.bamboo.admin.action;

import com.atlassian.bamboo.configuration.AdministrationConfiguration;
import com.atlassian.bamboo.configuration.AdministrationConfigurationAccessor;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.configuration.AdministrationConfigurationPersister;
import com.atlassian.bamboo.ww2.BambooActionSupport;
import com.atlassian.bamboo.ww2.aware.permissions.GlobalAdminSecurityAware;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.component.ComponentLocator;
import com.opensymphony.xwork2.ActionContext;
import com.testingbot.bamboo.config.Keys;
import org.apache.commons.lang.StringUtils;

public class ConfigureAction extends BambooActionSupport implements GlobalAdminSecurityAware
{
    private String tbKey;
    private String tbSecret;

    public ConfigureAction(PluginAccessor pluginAccessor
    ) {
        super();
        setAdministrationConfigurationAccessor(ComponentLocator.getComponent(AdministrationConfigurationAccessor.class));
        setAdministrationConfigurationManager(ComponentLocator.getComponent(AdministrationConfigurationManager.class));
        setAdministrationConfigurationPersister(ComponentLocator.getComponent(AdministrationConfigurationPersister.class));
    }


    public String doEdit() {
        final AdministrationConfiguration adminConfig = this.getAdministrationConfiguration();
        setKey(adminConfig.getSystemProperty(Keys.TB_KEY));
        setSecret(adminConfig.getSystemProperty(Keys.TB_SECRET));
        return INPUT;
    }

    public String doSave()
    {
        final AdministrationConfiguration adminConfig = this.getAdministrationConfiguration();
        adminConfig.setSystemProperty(Keys.TB_KEY, getKey());
        adminConfig.setSystemProperty(Keys.TB_SECRET, getSecret());
        
        administrationConfigurationManager.saveAdministrationConfiguration(adminConfig);

        if (ActionContext.getContext() != null && ActionContext.getContext().getApplication() != null) {
            getBamboo().restartComponentsFollowingConfigurationChange();
        }

        addActionMessage(getText("config.updated"));
        return SUCCESS;
    }

    @Override
    public void validate()
    {
        if (StringUtils.isBlank(tbKey))
        {
            addFieldError("tbKey", "TestingBot Key is required.");
        }

        if (StringUtils.isBlank(tbSecret))
        {
            addFieldError("tbSecret", "TestingBot Secret is required.");
        }
    }

    public String getKey()
    {
        return tbKey;
    }

    public void setKey(String key)
    {
        this.tbKey = key;
    }

    public String getSecret()
    {
        return tbSecret;
    }

    public void setSecret(String secret)
    {
        this.tbSecret = secret;
    }
}