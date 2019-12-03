package com.testingbot.bamboo.config;

import java.net.ServerSocket;

import java.util.Map;

import static com.testingbot.bamboo.config.Keys.*;

public class MappedBuildConfiguration {
    private final Map<String, String> map;

    public MappedBuildConfiguration(Map<String, String> map) {
        this.map = map;
    }

    public boolean shouldOverrideAuthentication() {
        return map.get(OVERRIDE_AUTHENTICATION_KEY) != null && map.get(OVERRIDE_AUTHENTICATION_KEY).equals("true");
    }

    public String getKey() {
        return map.get(TESTINGBOT_USER_KEY);
    }

    public String getSecret() {
        return map.get(TESTINGBOT_USER_SECRET);
    }

    public static String[] fromString(String string) {
        if (string == null)
            return new String[]{};
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        String result[] = new String[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = strings[i];
        }
        return result;
    }

    public boolean isEnabled() {
        return Boolean.parseBoolean(map.get(ENABLED_KEY));
    }

    public boolean isTunnelEnabled() {
        return Boolean.parseBoolean(map.get(SSH_ENABLED_KEY));
    }

    public boolean isVerboseSSHLogging() {
        return Boolean.parseBoolean(map.get(SSH_VERBOSE_KEY));
    }

    public boolean useGeneratedTunnelIdentifier() {
        return Boolean.parseBoolean(map.get(SSH_USE_GENERATED_TUNNEL_ID));
    }

    public String getSshPorts() {
        String port = map.get(SELENIUM_PORT_ENV);

        if (port == null) {
            port = map.get(SELENIUM_PORT_KEY);
        }

        if (port == null || port.equals("")) {
            if (isTunnelEnabled()) {
                port = "4445";
            } else {
                port = "80";
            }
        } else if (port == "0") {
            try {
                ServerSocket s = new ServerSocket(0);
                System.out.println("Port was 0, listening on port: " + s.getLocalPort());
                port = Integer.toString(s.getLocalPort());
            } catch (java.io.IOException e) {
                if (isTunnelEnabled()) {
                    port = "4445";
                } else {
                    port = "80";
                }
            }
        }

        map.put(SELENIUM_PORT_ENV,port);

        return port;
    }

    public String getSshHost() {
        String host = map.get(SELENIUM_HOST_KEY);
        if (host == null || host.equals("")) {
            if (isTunnelEnabled()) {
                host = "localhost";
            } else {
                host = "hub.testingbot.com";
            }
        }
        return host;
    }

    public String getTempKey() {
        return map.get(TEMP_KEY);
    }

    public void setTempKey(String user) {
        map.put(TEMP_KEY, user);
    }

    public String getTempSecret() {
        return map.get(TEMP_SECRET);
    }

    public void setTempSecret(String key) {
        map.put(TEMP_SECRET, key);
    }

    public Map<String, String> getMap() {
        return map;
    }

    public String getTunnelOptions() {
        return map.get(TUNNEL_OPTIONS);
    }
}