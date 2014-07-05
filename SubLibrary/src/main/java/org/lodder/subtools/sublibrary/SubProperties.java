package org.lodder.subtools.sublibrary;

import java.io.IOException;
import java.io.InputStream;

public class SubProperties {

	private static SubProperties subProps = null;
	private java.util.Properties prop = new java.util.Properties();
	private InputStream input = null;

	private SubProperties() {
		try {
			input = getClass().getResourceAsStream("/config.properties");
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static SubProperties getSubProperties() {
		if (subProps == null)
			subProps = new SubProperties();
		return subProps;
	}

	public String getProperty(String key){
		return prop.getProperty(key);
	}
}
