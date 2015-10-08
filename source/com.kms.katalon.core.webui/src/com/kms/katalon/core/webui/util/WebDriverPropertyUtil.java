package com.kms.katalon.core.webui.util;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class WebDriverPropertyUtil {
    private static final String CHROME_ARGUMENT_PROPERTY_KEY = "args";
    private static final String CHROME_BINARY_PROPERTY_KEY = "binary";
    private static final String CHROME_EXTENSIONS_PROPERTY_KEY = "extensions";
    private static final String CHROME_PREFERENCES_PROPERTY_KEY = "prefs";
    private static final String CHROME_LOCALSTATE_PROPERTY_KEY = "localState";
    private static final String CHROME_DETACH_PROPERTY_KEY = "detach";
    private static final String CHROME_DEBUGGER_ADDRESS_PROPERTY_KEY = "debuggerAddress";
    private static final String CHROME_EXCLUDE_SWITCHES_PROPERTY_KEY = "excludeSwitches";
    private static final String CHROME_MINI_DUMP_PATH_PROPERTY_KEY = "minidumpPath";
    private static final String CHROME_MOBILE_EMULATION_PROPERTY_KEY = "mobileEmulation";
    private static final String CHROME_PREF_LOGGING_PREFS_PROPERTY_KEY = "perfLoggingPrefs";
    private static final String[] CHROME_CAPABILITIES = { CHROME_ARGUMENT_PROPERTY_KEY, CHROME_BINARY_PROPERTY_KEY,
            CHROME_EXTENSIONS_PROPERTY_KEY, CHROME_PREFERENCES_PROPERTY_KEY, CHROME_LOCALSTATE_PROPERTY_KEY,
            CHROME_DETACH_PROPERTY_KEY, CHROME_DEBUGGER_ADDRESS_PROPERTY_KEY, CHROME_EXCLUDE_SWITCHES_PROPERTY_KEY,
            CHROME_MINI_DUMP_PATH_PROPERTY_KEY, CHROME_MOBILE_EMULATION_PROPERTY_KEY,
            CHROME_PREF_LOGGING_PREFS_PROPERTY_KEY };

    public static DesiredCapabilities toDesireCapabilities(Map<String, Object> propertyMap, WebUIDriverType webUIDriverType) {
        switch (webUIDriverType) {
        case CHROME_DRIVER:
            return getDesireCapabilitiesForChrome(propertyMap);
        case FIREFOX_DRIVER:
            return getDesireCapabilitiesForFirefox(propertyMap);
        default:
            DesiredCapabilities desireCapabilities = new DesiredCapabilities();
            for (Entry<String, Object> property : propertyMap.entrySet()) {
                KeywordLogger.getInstance().logInfo(
                        MessageFormat.format(StringConstants.KW_LOG_WEB_UI_PROPERTY_SETTING, property.getKey(),
                                property.getValue()));
                desireCapabilities.setCapability(property.getKey(), property.getValue());
            }
            return desireCapabilities;
        }
    }
    
    public static DesiredCapabilities getDesireCapabilitiesForFirefox(Map<String, Object> propertyMap) {
        DesiredCapabilities desireCapabilities = DesiredCapabilities.firefox();
        for (Entry<String, Object> property : propertyMap.entrySet()) {
            KeywordLogger.getInstance().logInfo("User set: [" + property.getKey() + ", " + property.getValue() + "]");
            if (property.getKey().equals(FirefoxDriver.PROFILE)) {
                if (property.getValue() instanceof Map<?, ?>) {
                    Map<?, ?> firefoxPropertyMap = (Map<?, ?>) property.getValue();
                    FirefoxProfile firefoxProfile = new FirefoxProfile();
                    for (Entry<?, ?> entry : firefoxPropertyMap.entrySet()) {
                        if (entry.getKey() instanceof String) {
                            String entryKey = (String) entry.getKey();
                            boolean isSet = false;
                            if (property.getValue() instanceof Integer) {
                                firefoxProfile.setPreference(entryKey, (Integer) entry.getValue());
                                isSet = true;
                            } else if (property.getValue() instanceof Boolean) {
                                firefoxProfile.setPreference(entryKey, (Boolean) entry.getValue());
                                isSet = true;
                            } else if (property.getValue() instanceof String) {
                                firefoxProfile.setPreference(entryKey, (String) entry.getValue());
                                isSet = true;
                            }
                            if (isSet) {
                                KeywordLogger.getInstance().logInfo(
                                        MessageFormat.format(StringConstants.KW_LOG_WEB_UI_PROPERTY_SETTING, entryKey,
                                                entry.getValue()));
                            }
                        }
                    }
                    desireCapabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
                } else {
                    desireCapabilities.setCapability(property.getKey(), property.getValue());
                    KeywordLogger.getInstance().logInfo(
                            MessageFormat.format(StringConstants.KW_LOG_WEB_UI_PROPERTY_SETTING, property.getKey(),
                                    property.getValue()));
                }
            }

        }
        return desireCapabilities;
    }

    public static DesiredCapabilities getDesireCapabilitiesForChrome(Map<String, Object> propertyMap) {
        DesiredCapabilities desireCapabilities = DesiredCapabilities.chrome();
        Map<String, Object> chromeOptions = new HashMap<String, Object>();
        for (Entry<String, Object> driverProperty : propertyMap.entrySet()) {
            if (Arrays.asList(CHROME_CAPABILITIES).contains(driverProperty.getKey())) {
                chromeOptions.put(driverProperty.getKey(), driverProperty.getValue());
            } else {
                desireCapabilities.setCapability(driverProperty.getKey(), driverProperty.getValue());
            }
            KeywordLogger.getInstance().logInfo(
                    MessageFormat.format(StringConstants.KW_LOG_WEB_UI_PROPERTY_SETTING, driverProperty.getKey(),
                            driverProperty.getValue()));
        }
        desireCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        return desireCapabilities;
    }
}
