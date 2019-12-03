package com.testingbot.bamboo.config;

public final class Keys
{
    public static final String TESTINGBOT_KEY = "TB_KEY";
    public static final String TESTINGBOT_SECRET = "TB_SECRET";

    private Keys() {}
    public static final String CUSTOM_PREFIX = "custom.testingbot.";
    public static final String SSH_PREFIX = "custom.testingbot.ssh.";

    public static final String ENABLED_KEY = CUSTOM_PREFIX + "enabled";
    public static final String TESTINGBOT_USER_KEY = CUSTOM_PREFIX + "key";
    public static final String TESTINGBOT_USER_SECRET = CUSTOM_PREFIX + "secret";
    public static final String OVERRIDE_AUTHENTICATION_KEY = CUSTOM_PREFIX + "auth.enabled";

    public static final String SELENIUM_HOST_KEY = CUSTOM_PREFIX + "selenium.host";
    public static final String SELENIUM_PORT_KEY = CUSTOM_PREFIX + "selenium.port";

    public static final String SSH_ENABLED_KEY = SSH_PREFIX + "enabled";
    public static final String SSH_VERBOSE_KEY = SSH_PREFIX + "verbose";
    public static final String SSH_USE_DEFAULTS_KEY = SSH_PREFIX + "defaults";
    public static final String SSH_LOCAL_HOST_KEY = SSH_PREFIX + "local.host";

    public static final String SSH_LOCAL_PORTS_KEY = SSH_PREFIX + "local.ports";
    public static final String SSH_USE_GENERATED_TUNNEL_ID = SSH_PREFIX + "useGeneratedTunnelIdentifier";

    public static final String TB_KEY = "tb.key";
    public static final String TB_SECRET = "tb.secret";

    /* ENV Vars */
    public static final String SELENIUM_HOST_ENV = "SELENIUM_HOST";
    public static final String SELENIUM_PORT_ENV = "SELENIUM_PORT";
    public static final String TB_CUSTOM_DATA_ENV = "TB_BAMBOO_BUILDNUMBER";
    public static final String BAMBOO_BUILD_NUMBER_ENV = "TB_BAMBOO_BUILDNUMBER";
    public static final String TB_TESTINGBOT_KEY_ENV = "TESTINGBOT_KEY";
    public static final String TB_TESTINGBOT_SECRET_ENV = "TESTINGBOT_SECRET";

    public static final String TEMP_KEY = CUSTOM_PREFIX + "temp.key";
    public static final String TEMP_SECRET = CUSTOM_PREFIX + "temp.secret";

    public static final String TUNNEL_OPTIONS = CUSTOM_PREFIX + "tunnelOptions";
    public static final String TUNNEL_IDENTIFIER = "TUNNEL_IDENTIFIER";
}
