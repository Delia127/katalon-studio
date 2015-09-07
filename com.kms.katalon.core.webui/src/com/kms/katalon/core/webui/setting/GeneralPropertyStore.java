package com.kms.katalon.core.webui.setting;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import com.kms.katalon.core.setting.PropertySettingStore;

public class GeneralPropertyStore {
	private static final String PARENT_KEY = "general";
	
	public static Map<String, String> getMapProperties(String projectDir) {
		try {
			return PropertySettingStore.getPropertyValues(PARENT_KEY, WebUiSettingStore.getPropertyFile(projectDir));
		} catch (IOException e) {
			return Collections.emptyMap();
		}
	}
	
	public static String getPropertyValue(String projectDir, String rawKey) {
		String key = PARENT_KEY + "." + rawKey;
		try {
			String value = PropertySettingStore.getPropertyValue(key,
					WebUiSettingStore.getPropertyFile(projectDir));
			return value;
		} catch (IOException e) {
			return null;
		}
	}
}
