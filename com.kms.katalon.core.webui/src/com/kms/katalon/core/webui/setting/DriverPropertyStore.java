package com.kms.katalon.core.webui.setting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.setting.PropertySettingStore;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class DriverPropertyStore {
	
	public static List<DriverProperty> getProperties(DriverType driverType, String projectDir) {
		Map<String, String> rawProperties = getRawProperties(driverType, projectDir);
		List<DriverProperty> driverProperties = new ArrayList<DriverProperty>();
		
		for (Entry<String, String> entryRawProperty : rawProperties.entrySet()) {
			driverProperties.add(new DriverProperty(entryRawProperty.getKey(), WebUiSettingStore.getValue(entryRawProperty
					.getValue()), driverType));
			
		}
		return driverProperties;
	}
	
	public static List<String> getPropertyKeys(DriverType driverType, String projectDir) {
		List<String> propertyKeys = new ArrayList<String>();
		for (String rawEntryKey : getRawProperties(driverType, projectDir).keySet()) {
			propertyKeys.add( getParentKey(driverType) + "." + rawEntryKey);
		}
		return propertyKeys;
	}
	
	public static void saveProperties(DriverType driverType, List<DriverProperty> driverProperties, String projectDir)
			throws IOException {
		File propertyFile = WebUiSettingStore.getPropertyFile(projectDir);
		PropertySettingStore.removeAll(getPropertyKeys(driverType, projectDir), propertyFile);
		for (DriverProperty driverProperty : driverProperties) {			
			PropertySettingStore
					.addNewProperty(driverProperty.getRawName(), driverProperty.getRawValue(), propertyFile);
		}
	}
	
	public static String getParentKey(DriverType driverType) {
		return (driverType.getName()).toLowerCase();
	}

	public static Map<String, String> getRawProperties(DriverType driverType, String projectDir) {		
		try {
			return PropertySettingStore.getPropertyValues(getParentKey(driverType), WebUiSettingStore.getPropertyFile(projectDir));
		} catch (IOException e) {
			return Collections.emptyMap();
		}
	}
	
	public static String getPropertyValue(DriverType driverType, String projectDir, String rawKey) {
		String key = getParentKey(driverType) + "." + rawKey;
		try {
			String value = PropertySettingStore.getPropertyValue(key,
					WebUiSettingStore.getPropertyFile(projectDir));
			return value;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static FirefoxProfile getFirefoxProfile() {
		FirefoxProfile profile = new FirefoxProfile();
		for (DriverProperty firefoxPerference : DriverPropertyStore.getProperties(WebUIDriverType.FIREFOX_DRIVER,
				RunConfiguration.getProjectDir())) {

			KeywordLogger.getInstance().logInfo(
					"User set: [" + firefoxPerference.getName() + ", " + firefoxPerference.getValue() + "]");

			if (firefoxPerference.getValue() instanceof Integer) {
				profile.setPreference(firefoxPerference.getName(), (Integer) firefoxPerference.getValue());
			} else if (firefoxPerference.getValue() instanceof Boolean) {
				profile.setPreference(firefoxPerference.getName(), (Boolean) firefoxPerference.getValue());
			} else if (firefoxPerference.getValue() instanceof String) {
				profile.setPreference(firefoxPerference.getName(), (String) firefoxPerference.getValue());
			}
		}
		return profile;
	}
	
	public static ChromeOptions getChromeOptions() {
		ChromeOptions options = new ChromeOptions();
		Map<String, Object> prefs = new LinkedHashMap<String, Object>();
		
		for (DriverProperty chromePerference : DriverPropertyStore.getProperties(WebUIDriverType.CHROME_DRIVER,
				RunConfiguration.getProjectDir())) {

			KeywordLogger.getInstance().logInfo(
					"User set: [" + chromePerference.getName() + ", " + chromePerference.getValue() + "]");

			if (chromePerference.getValue() instanceof Integer) {
				prefs.put(chromePerference.getName(), (Integer) chromePerference.getValue());
			} else if (chromePerference.getValue() instanceof Boolean) {
				prefs.put(chromePerference.getName(), (Boolean) chromePerference.getValue());
			} else if (chromePerference.getValue() instanceof String) {
				prefs.put(chromePerference.getName(), (String) chromePerference.getValue());
			}
		}
		options.setExperimentalOption("prefs", prefs);
		return options;
	}
	
	public static DesiredCapabilities getRemoteWebDriverOptions() {
		DesiredCapabilities caps = new DesiredCapabilities();
		
		for (DriverProperty driverProperty : DriverPropertyStore.getProperties(WebUIDriverType.REMOTE_WEB_DRIVER,
				RunConfiguration.getProjectDir())) {

			KeywordLogger.getInstance().logInfo(
					"User set: [" + driverProperty.getName() + ", " + driverProperty.getValue() + "]");

			caps.setCapability(driverProperty.getName(), driverProperty.getValue());
		}
		return caps;
	}
}
