package org.lodder.subtools.sublibrary;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigProperties.class);

    private static final ConfigProperties instance = new ConfigProperties();
    private final Properties prop = new Properties();

    private ConfigProperties() {
        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
            prop.load(input);
        } catch (IOException ex) {
            LOGGER.error("Error loading config properties", ex);
        }
    }

    public static String getProperty(Property property) {
        return instance.prop.getProperty(property.value);
    }

    @AllArgsConstructor
    public enum Property {
        NAME("name"),
        VERSION("version");

        @val String value;
    }
}
