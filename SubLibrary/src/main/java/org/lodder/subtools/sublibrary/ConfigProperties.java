package org.lodder.subtools.sublibrary;

import java.io.IOException;
import java.io.InputStream;

public final class ConfigProperties {

    private static ConfigProperties configProps = null;
    private final java.util.Properties prop = new java.util.Properties();

    private ConfigProperties() {
        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized static ConfigProperties getInstance() {
        if (configProps == null) {
            configProps = new ConfigProperties();
        }
        return configProps;
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}
