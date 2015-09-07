package com.kms.katalon.core.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class PropertySettingStore {
	public static final String ROOT_FOLDER_NAME = "settings" + File.separator + "internal";
	public static final String PROPERTY_FILE_EXENSION = ".properties";
	
	public static void addNewProperty(String key, String value, File propertyFile) throws IOException {
		FileInputStream fileInput = null;
		FileOutputStream fileOutput = null;
		try {
			fileInput = new FileInputStream(propertyFile);
			LinkedProperties properties = new LinkedProperties();
			properties.load(fileInput);
			fileInput.close();
			fileInput = null;
			
			properties.put(key, value);
			fileOutput = new FileOutputStream(propertyFile);
			properties.store(fileOutput, null);
			fileOutput.close();
			fileOutput = null;
		} finally {
			if (fileInput != null) {
				fileInput.close();
			}

			if (fileOutput != null) {
				fileOutput.close();
			}
		}
	}
	
	public static void clearAll(File propertyFile) throws IOException{
		FileInputStream fileInput = null;
		FileOutputStream fileOutput = null;
		try {
			fileInput = new FileInputStream(propertyFile);
			Properties properties = new LinkedProperties();
			properties.load(fileInput);
			fileInput.close();
			fileInput = null;

			properties.clear();	
			fileOutput = new FileOutputStream(propertyFile);
			properties.store(fileOutput, null);
			fileOutput.close();
			fileOutput = null;
		} finally {
			if (fileInput != null) {
				fileInput.close();
			}

			if (fileOutput != null) {
				fileOutput.close();
			}
		}
	}
	
	public static void removeAll(Collection<String> keys, File propertyFile) throws IOException{
		FileInputStream fileInput = null;
		FileOutputStream fileOutput = null;
		try {
			fileInput = new FileInputStream(propertyFile);
			LinkedProperties properties = new LinkedProperties();
			properties.load(fileInput);
			fileInput.close();
			fileInput = null;

			Iterator<Object> orderedKeys = properties.orderedKeys().iterator();
			while (orderedKeys.hasNext()) {
				Object propertyKey = orderedKeys.next();
				String rawEntryKey = propertyKey.toString();
				
				if (keys.contains(rawEntryKey)) {					
					properties.remove(propertyKey);
				}
			}
			
			fileOutput = new FileOutputStream(propertyFile);
			properties.store(fileOutput, null);
			fileOutput.close();
			fileOutput = null;
		} finally {
			if (fileInput != null) {
				fileInput.close();
			}

			if (fileOutput != null) {
				fileOutput.close();
			}
		}
	}
	
	public static String getPropertyValue(String key, File propertyFile) throws IOException {
		if (!propertyFile.exists()) return null;
		
		FileInputStream fileInput = new FileInputStream(propertyFile);
		try {
			Properties properties = new LinkedProperties();
			properties.load(fileInput);
			return properties.getProperty(key);
		} finally {
			fileInput.close();
		}
	}
	
	public static Map<String, String> getPropertyValues(String parentKey, File propertyFile) throws IOException {
		if (!propertyFile.exists()) return Collections.emptyMap();
		
		FileInputStream fileInput = new FileInputStream(propertyFile);
		try {
			LinkedProperties properties = new LinkedProperties();
			properties.load(fileInput);
			Map<String, String> mapProperties = new LinkedHashMap<String, String>();
			
			Iterator<Object> orderedKeys = properties.orderedKeys().iterator();
			while (orderedKeys.hasNext()) {
				Object propertyKey = orderedKeys.next();
				String rawEntryKey = propertyKey.toString();
				
				if (rawEntryKey.startsWith(parentKey + ".")) {
					String entryKey = rawEntryKey.substring(parentKey.length() + 1);
					String entryValue = properties.getProperty(rawEntryKey);
					
					mapProperties.put(entryKey, entryValue);
				}
			}
			
			return mapProperties;
		} finally {
			fileInput.close();
		}
	}
}
