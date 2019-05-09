package com.kms.katalon.execution.webui.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.ChromeDriverConnector;
import com.kms.katalon.execution.webui.driver.FirefoxDriverConnector;
import com.kms.katalon.execution.webui.driver.IEDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.execution.webui.driver.SafariDriverConnector;
import com.kms.katalon.execution.webui.configuration.ChromeRunConfiguration;
import com.kms.katalon.execution.webui.configuration.FirefoxRunConfiguration;
import com.kms.katalon.execution.webui.configuration.IERunConfiguration;
import com.kms.katalon.execution.webui.configuration.SafariRunConfiguration;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;

public class WebUIExecutionUtil {
    public static int getWaitForIEHanging() throws IOException {
        return WebUiExecutionSettingStore.getStore().getIEHangTimeout();
    }

    public static IDriverConnector getBrowserDriverConnector(WebUIDriverType webDriverType, String projectDirectory)
            throws IOException {
        Map<String, IDriverConnector> driverConnectors = new HashMap<>();
        switch (webDriverType) {
            case CHROME_DRIVER:
                driverConnectors = new ChromeRunConfiguration(projectDirectory).getDriverConnectors();
                break;
            case FIREFOX_DRIVER:
                driverConnectors = new FirefoxRunConfiguration(projectDirectory).getDriverConnectors();
                break;
            case IE_DRIVER:
                driverConnectors = new IERunConfiguration(projectDirectory).getDriverConnectors();
                break;
            case SAFARI_DRIVER:
                driverConnectors = new SafariRunConfiguration(projectDirectory).getDriverConnectors();
                break;
            default:
                return null;
        }
        return driverConnectors.get(DriverFactory.WEB_UI_DRIVER_PROPERTY);
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
