package com.kms.katalon.core.webui.setting;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStore;


public class WebUiSettingStore {
	private static final String FILE_NAME = "com.kms.katalon.core.webui";
	private static final String BOOLEAN_REGEX = "^(true|false)$";
	private static final String INTEGER_REGEX = "^(-)?\\d+$";
	private static final String STRING_REGEX = "^\".+\"$";
	private static final String PROPERTY_NAME_REGEX = "^[a-zA-Z0-9\\.\\-_@\\*]+$";
	
	public static File getPropertyFile(String projectDir) throws IOException {
		File configFile = new File(projectDir + File.separator + PropertySettingStore.ROOT_FOLDER_NAME
				+ File.separator + FILE_NAME + PropertySettingStore.PROPERTY_FILE_EXENSION);
		if (!configFile.exists()) {
			configFile.createNewFile();
		}
		return configFile;		
	}
	
	public static Object getValue(String rawValue) {
		if (rawValue == null || rawValue.isEmpty()) return null;
		
		if (rawValue.matches(BOOLEAN_REGEX)) {
			return Boolean.valueOf(rawValue);
		} else if (rawValue.matches(INTEGER_REGEX)) {
			return Integer.valueOf(rawValue);
		} else if (rawValue.matches(STRING_REGEX)) {
			return rawValue.substring(1, rawValue.length() - 1);
		} else {
			return null;
		}
	}
	
	public static String getRawValue(Object value) {
		if (value == null) return null;
		if (value instanceof String) {
			return "\"" + value + "\"";
		} else {
			return String.valueOf(value);
		}
	}
	
	public static boolean isValidPropertyName(String name) {
		if (name == null || name.isEmpty()) return false;
		return name.matches(PROPERTY_NAME_REGEX);
	}
}
