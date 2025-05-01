package org.lodder.subtools.multisubdownloader.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.val;

public class PropertiesReader {

    private static PropertiesReader propertiesReaderInstance;
    private final Properties properties;

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

    public static String getProperty(PomProperty property) {
        return PropertiesReader.getPropertiesReader().properties.getProperty(property.value);
    }

    @AllArgsConstructor
    public enum PomProperty {
        BUILD_TIMESTAMP("build.timestamp");

        @val String value;
    }
}
