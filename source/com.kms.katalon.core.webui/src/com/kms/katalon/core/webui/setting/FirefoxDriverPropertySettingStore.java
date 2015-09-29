package com.kms.katalon.core.webui.setting;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class FirefoxDriverPropertySettingStore extends WebUiDriverPropertySettingStore {

    public FirefoxDriverPropertySettingStore(String projectDir) throws IOException {
        super(projectDir, WebUIDriverType.FIREFOX_DRIVER);
    }

    public FirefoxDriverPropertySettingStore(String projectDir, String customProfileName) throws IOException {
        super(projectDir, WebUIDriverType.FIREFOX_DRIVER, customProfileName);
    }

    @Override
    public DesiredCapabilities toDesiredCapabilities() {
        if (driverType != WebUIDriverType.FIREFOX_DRIVER) {
            return null;
        }
        DesiredCapabilities desireCapabilities = DesiredCapabilities.firefox();
        for (Entry<String, Object> property : getDriverProperties().entrySet()) {
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
}
