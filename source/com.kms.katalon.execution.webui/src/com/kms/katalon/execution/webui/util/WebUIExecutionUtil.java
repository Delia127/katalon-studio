package com.kms.katalon.execution.webui.util;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.ChromeDriverConnector;
import com.kms.katalon.execution.webui.driver.FirefoxDriverConnector;
import com.kms.katalon.execution.webui.driver.IEDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.execution.webui.driver.SafariDriverConnector;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;

public class WebUIExecutionUtil {
    public static int getWaitForIEHanging() throws IOException {
        return WebUiExecutionSettingStore.getStore().getIEHangTimeout();
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
    
    public static IDriverConnector getDriverConnector(WebUIDriverType webDriverType, String customRunConfig)
            throws IOException {
        switch (webDriverType) {
            case REMOTE_WEB_DRIVER:
                return new RemoteWebDriverConnector(customRunConfig);
            default:
                return null;
        }
    }
}
