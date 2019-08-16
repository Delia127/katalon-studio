package com.kms.katalon.composer.handlers;

import static org.eclipse.core.runtime.Platform.getOS;
import static org.eclipse.core.runtime.Platform.OS_WIN32;
import static org.eclipse.core.runtime.Platform.OS_LINUX;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Platform;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.util.internal.ProcessUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.webui.configuration.WebDriverManagerRunConfiguration;
import com.kms.katalon.execution.webui.driver.SeleniumWebDriverProvider;

public class DriverDownloadManager {

    public static void downloadDriver(WebUIDriverType typeDriver) throws InterruptedException, IOException {

        switch (typeDriver) {
        case CHROME_DRIVER:
            switch (Platform.getOS()) {
            case OS_WIN32:
                ProcessUtil.killProcessOnWindows("chromedriver.exe");
                break;
            case OS_LINUX:
                ProcessUtil.killProcessOnUnix("chromedriver");
                break;
            default:
                break;
            }
            try {
                String chromeDriverPath = SeleniumWebDriverProvider.getChromeDriverPath();

                WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
                webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.CHROME_DRIVER,
                        new File(chromeDriverPath).getParentFile());
            } catch (IOException e) {
                LoggerSingleton.logError(e);
                throw new IOException();
            }
            break;
        case FIREFOX_DRIVER:
            switch (Platform.getOS()) {
            case OS_WIN32:
                ProcessUtil.killProcessOnWindows("geckodriver.exe");
                break;
            case OS_LINUX:
                ProcessUtil.killProcessOnUnix("geckodriver");
                break;
            default:
                break;
            }
            try {
                String firefoxDriverPath = SeleniumWebDriverProvider.getGeckoDriverPath();

                WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
                webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.FIREFOX_DRIVER,
                        new File(firefoxDriverPath).getParentFile());
            } catch (IOException e) {
                LoggerSingleton.logError(e);
                throw new IOException();
            }
            break;
        case IE_DRIVER:
            switch (Platform.getOS()) {
            case OS_WIN32:
                ProcessUtil.killProcessOnWindows("IEDriverServer.exe");
                break;
            case OS_LINUX:
                ProcessUtil.killProcessOnUnix("IEDriverServer");
                break;
            default:
                break;
            }
            try {
                String ieDriverPath = SeleniumWebDriverProvider.getIEDriverPath();

                WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
                webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.IE_DRIVER,
                        new File(ieDriverPath).getParentFile());
            } catch (IOException e) {
                LoggerSingleton.logError(e);
                throw new IOException();
            }
            break;
        case EDGE_DRIVER:
            switch (Platform.getOS()) {
            case OS_WIN32:
                ProcessUtil.killProcessOnWindows("msedgedriver.exe");
                break;
            case OS_LINUX:
                ProcessUtil.killProcessOnUnix("msedgedriver");
                break;
            default:
                break;
            }
            try {
                String edgeDriverPath = SeleniumWebDriverProvider.getEdgeDriverPath();

                WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
                webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.EDGE_DRIVER,
                        new File(edgeDriverPath).getParentFile());
            } catch (IOException e) {
                LoggerSingleton.logError(e);
                throw new IOException();
            }
            break;
        default:
            break;
        }
    }
}
