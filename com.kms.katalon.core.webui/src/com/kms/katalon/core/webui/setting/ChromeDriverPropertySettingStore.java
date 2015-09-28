package com.kms.katalon.core.webui.setting;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class ChromeDriverPropertySettingStore extends WebUiDriverPropertySettingStore {
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

    public ChromeDriverPropertySettingStore(String projectDir) throws IOException {
        super(projectDir, WebUIDriverType.CHROME_DRIVER);
    }

    public ChromeDriverPropertySettingStore(String projectDir, String customProfileName) throws IOException {
        super(projectDir, WebUIDriverType.CHROME_DRIVER, customProfileName);
    }

    @Override
    public DesiredCapabilities toDesiredCapabilities() {
        if (driverType != WebUIDriverType.CHROME_DRIVER) {
            return null;
        }
        DesiredCapabilities desireCapabilities = DesiredCapabilities.chrome();
        Map<String, Object> chromeOptions = new HashMap<String, Object>();
        for (Entry<String, Object> driverProperty : getProperties().entrySet()) {
            if (Arrays.asList(CHROME_CAPABILITIES).contains(driverProperty.getKey())) {
                chromeOptions.put(driverProperty.getKey(), driverProperty.getValue());
            } else {
                desireCapabilities.setCapability(driverProperty.getKey(), driverProperty.getValue());
            }
            KeywordLogger.getInstance().logInfo(
                    MessageFormat.format(StringConstants.KW_LOG_WEB_UI_PROPERTY_SETTING, driverProperty.getKey(),
                            driverProperty.getValue()));
        }
        desireCapabilities.setCapability(ChromeOptions.CAPABILITY, getProperties());
        return desireCapabilities;
    }
}
