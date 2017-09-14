package ar.fiuba.taller.common;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigLoader {

	private Map<String, String> propertiesMap;

	public ConfigLoader(String configFile) throws IOException {
		propertiesMap = new HashMap<String, String>();
		Properties properties = new Properties();
		try {
			properties.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(Constants.CONF_FILE));
		} catch (IOException e) {
			System.err.println(
					"No ha sido posible cargar el archivo de propiedades");
			throw new IOException();
		}
		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);
			propertiesMap.put(key, value);
		}

		propertiesMap = Collections.unmodifiableMap(propertiesMap);
	}

	public Map<String, String> getProperties() {
		return propertiesMap;
	}
}