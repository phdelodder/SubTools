package org.lodder.subtools.multisubdownloader.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private final Properties properties;
    private static PropertiesReader propertiesReaderInstance;

    public PropertiesReader() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("properties-from-pom.properties")) {
            this.properties = new Properties();
            this.properties.load(is);
        }
    }

    private static PropertiesReader getPropertiesReader() {
        if (propertiesReaderInstance == null) {
            try {
                propertiesReaderInstance = new PropertiesReader();
            } catch (IOException e) {
                throw new IllegalStateException("Should not happen", e);
            }
        }
        return propertiesReaderInstance;
    }

    public static String getProperty(String propertyName) {
        return PropertiesReader.getPropertiesReader().properties.getProperty(propertyName);
    }
}
