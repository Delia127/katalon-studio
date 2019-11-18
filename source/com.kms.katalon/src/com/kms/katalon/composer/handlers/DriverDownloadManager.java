package com.kms.katalon.composer.handlers;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.core.webui.util.WebDriverCleanerUtil;
import com.kms.katalon.execution.webui.configuration.WebDriverManagerRunConfiguration;
import com.kms.katalon.execution.webui.driver.SeleniumWebDriverProvider;

public class DriverDownloadManager {

    public static void downloadDriver(WebUIDriverType typeDriver, File logFile, File errorLogFile) throws InterruptedException, IOException {

        switch (typeDriver) {
        case CHROME_DRIVER: {
                WebDriverCleanerUtil.cleanup();
                
                String chromeDriverPath = SeleniumWebDriverProvider.getChromeDriverPath();
    
                WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
                webDriverManagerRunConfiguration.setLogFile(logFile);
                webDriverManagerRunConfiguration.setErrorLogFile(errorLogFile);
                webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.CHROME_DRIVER,
                        new File(chromeDriverPath).getParentFile());
            }
            break;
        case FIREFOX_DRIVER:{
                WebDriverCleanerUtil.cleanup();
           
                String firefoxDriverPath = SeleniumWebDriverProvider.getGeckoDriverPath();

                WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
                webDriverManagerRunConfiguration.setLogFile(logFile);
                webDriverManagerRunConfiguration.setErrorLogFile(errorLogFile);
                webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.FIREFOX_DRIVER,
                        new File(firefoxDriverPath).getParentFile());
            }
            break;
        case IE_DRIVER: {
                WebDriverCleanerUtil.cleanup();
            
                String ieDriverPath = SeleniumWebDriverProvider.getIEDriverPath();

                WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
                webDriverManagerRunConfiguration.setLogFile(logFile);
                webDriverManagerRunConfiguration.setErrorLogFile(errorLogFile);
                webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.IE_DRIVER,
                        new File(ieDriverPath).getParentFile());
            }
            break;
        case EDGE_DRIVER: {
                WebDriverCleanerUtil.cleanup();
           
                String edgeDriverPath = SeleniumWebDriverProvider.getEdgeDriverPath();

                WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
                webDriverManagerRunConfiguration.setLogFile(logFile);
                webDriverManagerRunConfiguration.setErrorLogFile(errorLogFile);
                webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.EDGE_DRIVER,
                        new File(edgeDriverPath).getParentFile());
            }
            break;
        default:
            break;
        }
    }
}
