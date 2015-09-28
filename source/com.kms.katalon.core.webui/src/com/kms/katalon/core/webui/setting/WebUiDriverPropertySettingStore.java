package com.kms.katalon.core.webui.setting;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map.Entry;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.setting.DriverPropertySettingStore;
import com.kms.katalon.core.webui.constants.StringConstants;

public abstract class WebUiDriverPropertySettingStore extends DriverPropertySettingStore {
    private static final String SETTINGS_FILE_NAME = StringConstants.WEB_UI_PROPERTY_FILE_NAME;

    public WebUiDriverPropertySettingStore(String projectDir, DriverType driverType) throws IOException {
        super(projectDir, driverType);
    }

    public WebUiDriverPropertySettingStore(String projectDir, DriverType driverType, String customProfileName)
            throws IOException {
        super(projectDir, driverType, customProfileName);
    }

    @Override
    protected String getSettingFileName() {
        return SETTINGS_FILE_NAME;
    }

    public DesiredCapabilities toDesiredCapabilities() {
        DesiredCapabilities desireCapabilities = new DesiredCapabilities();
        for (Entry<String, Object> property : getProperties().entrySet()) {
            KeywordLogger.getInstance().logInfo(
                    MessageFormat.format(StringConstants.KW_LOG_WEB_UI_PROPERTY_SETTING, property.getKey(),
                            property.getValue()));
            desireCapabilities.setCapability(property.getKey(), property.getValue());
        }
        return desireCapabilities;
    }
}
