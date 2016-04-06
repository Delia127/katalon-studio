package com.kms.katalon.execution.webui.util;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.ChromeDriverConnector;
import com.kms.katalon.execution.webui.driver.FirefoxDriverConnector;
import com.kms.katalon.execution.webui.driver.IEDriverConnector;
import com.kms.katalon.execution.webui.driver.SafariDriverConnector;

public class WebUIExecutionUtil {
    public static int getWaitForIEHanging() {
        return getPreferenceStore(PreferenceConstants.WEBUI_QUALIFIER).getInt(
                PreferenceConstants.WEBUI_EXECUTION_WAIT_FOR_IE_HANGING);
    }

    public static IDriverConnector getBrowserDriverConnector(WebUIDriverType webDriverType, String projectDirectory)
            throws IOException {
        switch (webDriverType) {
            case CHROME_DRIVER:
                return new ChromeDriverConnector(projectDirectory + File.separator
                        + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME);
            case FIREFOX_DRIVER:
                return new FirefoxDriverConnector(projectDirectory + File.separator
                        + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME);
            case IE_DRIVER:
                return new IEDriverConnector(projectDirectory + File.separator
                        + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME);
            case SAFARI_DRIVER:
                return new SafariDriverConnector(projectDirectory + File.separator
                        + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME);
            default:
                return null;
        }
    }
}
