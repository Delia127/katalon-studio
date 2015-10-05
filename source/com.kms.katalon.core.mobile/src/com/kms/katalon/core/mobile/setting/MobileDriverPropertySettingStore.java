package com.kms.katalon.core.mobile.setting;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map.Entry;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.setting.DriverPropertySettingStore;

public abstract class MobileDriverPropertySettingStore extends DriverPropertySettingStore {
    private static final String SETTINGS_FILE_NAME = StringConstants.MOBILE_PROPERTY_FILE_NAME;

    public MobileDriverPropertySettingStore(String projectDir, DriverType driverType) throws IOException {
        super(projectDir, driverType);
    }

    public MobileDriverPropertySettingStore(String projectDir, DriverType driverType, String customProfileName)
            throws IOException {
        super(projectDir, driverType, customProfileName);
    }

    @Override
    protected String getSettingFileName() {
        return SETTINGS_FILE_NAME;
    }

    public DesiredCapabilities toDesiredCapabilities() {
        DesiredCapabilities desireCapabilities = new DesiredCapabilities();
        for (Entry<String, Object> property : getDriverProperties().entrySet()) {
            KeywordLogger.getInstance().logInfo(
                    MessageFormat.format(StringConstants.KW_LOG_MOBILE_PROPERTY_SETTING, property.getKey(),
                            property.getValue()));
            desireCapabilities.setCapability(property.getKey(), property.getValue());
        }
        return desireCapabilities;
    }
}
