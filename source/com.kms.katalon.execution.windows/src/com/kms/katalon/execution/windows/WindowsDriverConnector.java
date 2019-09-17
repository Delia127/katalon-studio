package com.kms.katalon.execution.windows;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.core.windows.driver.WindowsDriverFactory;
import com.kms.katalon.core.windows.constants.StringConstants;
import com.kms.katalon.core.windows.constants.WindowsDriverConstants;
import com.kms.katalon.core.windows.driver.WindowsDriverType;
import com.kms.katalon.execution.configuration.AbstractDriverConnector;
import com.kms.katalon.logging.LogUtil;

public class WindowsDriverConnector extends AbstractDriverConnector {

    private String configurationFolderPath;

    public WindowsDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
        this.configurationFolderPath = configurationFolderPath;
    }

    @Override
    public DriverType getDriverType() {
        return WindowsDriverType.getInstance();
    }

    @Override
    public String getSettingFileName() {
        return StringConstants.WINDOWS_PROPERTY_FILE_NAME;
    }

    @Override
    public WindowsDriverConnector clone() {
        try {
            WindowsDriverConnector clone = new WindowsDriverConnector(configurationFolderPath);
            return clone;
        } catch (IOException e) {
            LogUtil.logError(e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getDesiredCapabilities() {
        Map<String, Object> userProperties = getUserConfigProperties();
        if (userProperties.containsKey(WindowsDriverFactory.DESIRED_CAPABILITIES_PROPERTY)) {
            return (Map<String, Object>) userProperties.get(WindowsDriverFactory.DESIRED_CAPABILITIES_PROPERTY);
        }
        return Collections.emptyMap();
    }

    public String getWinAppDriverUrl() {
        Map<String, Object> userProperties = getUserConfigProperties();
        return (String) userProperties.getOrDefault(WindowsDriverFactory.WIN_APP_DRIVER_PROPERTY,
                WindowsDriverConstants.DEFAULT_WIN_APP_DRIVER_URL);
    }

    public static WindowsDriverConnector getInstance(String projectDir) throws IOException {
        return new WindowsDriverConnector(
                projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME);
    }
}
