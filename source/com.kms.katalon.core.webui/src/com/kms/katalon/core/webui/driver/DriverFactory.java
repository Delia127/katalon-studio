package com.kms.katalon.core.webui.driver;

import io.appium.java_client.ios.IOSDriver;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.exception.BrowserNotOpenedException;
import com.kms.katalon.core.webui.util.WebDriverPropertyUtil;

public class DriverFactory {
    private static final String APPIUM_CAPABILITY_PLATFORM_NAME_ADROID = "android";
    private static final String APPIUM_CAPABILITY_PLATFORM_NAME_IOS = "ios";
    private static final String APPIUM_CAPABILITY_PLATFORM_NAME = "platformName";
    private static final String REMOTE_WEB_DRIVER_TYPE_APPIUM = "Appium";
    private static final String REMOTE_WEB_DRIVER_TYPE_SELENIUM = "Selenium";
    private static final String CHROME_DRIVER_PATH_PROPERTY_KEY = "webdriver.chrome.driver";
    private static final String IE_DRIVER_PATH_PROPERTY_KEY = "webdriver.ie.driver";
    // Temp error constant message for issues
    // https://code.google.com/p/selenium/issues/detail?id=7977
    private static final String JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE = "[JavaScript Error: \"h is null\"";
    public static final String IE_DRIVER_PATH_PROPERTY = StringConstants.CONF_PROPERTY_IE_DRIVER_PATH;
    public static final String CHROME_DRIVER_PATH_PROPERTY = StringConstants.CONF_PROPERTY_CHROME_DRIVER_PATH;
    public static final String WAIT_FOR_IE_HANGING_PROPERTY = StringConstants.CONF_PROPERTY_WAIT_FOR_IE_HANGING;
    public static final String EXECUTED_BROWSER_PROPERTY = StringConstants.CONF_PROPERTY_EXECUTED_BROWSER;
    public static final String REMOTE_WEB_DRIVER_URL = StringConstants.CONF_PROPERTY_REMOTE_WEB_DRIVER_URL;
    public static final String REMOTE_WEB_DRIVER_TYPE = StringConstants.CONF_PROPERTY_REMOTE_WEB_DRIVER_TYPE;
    public static final String EXECUTED_MOBILE_PLATFORM = StringConstants.CONF_EXECUTED_PLATFORM;
    public static final String EXECUTED_MOBILE_DEVICE_NAME = StringConstants.CONF_EXECUTED_DEVICE_NAME;

    private static final ThreadLocal<WebDriver> localWebServerStorage = new ThreadLocal<WebDriver>() {
        @Override
        protected WebDriver initialValue() {
            return null;
        }
    };

    @SuppressWarnings("rawtypes")
    public static WebDriver openWebDriver() throws Exception {
        try {
            if (null != localWebServerStorage.get()
                    && null != ((RemoteWebDriver) localWebServerStorage.get()).getSessionId()) {
                KeywordLogger.getInstance().logWarning(StringConstants.DRI_LOG_WARNING_BROWSER_ALREADY_OPENED);
                closeWebDriver();
            }
            WebUIDriverType driver = (WebUIDriverType) getExecutedBrowser();
            if (driver == null) {
                throw new StepFailedException(StringConstants.DRI_ERROR_MSG_NO_BROWSER_SET);
            }
            KeywordLogger.getInstance().logInfo(
                    MessageFormat.format(StringConstants.XML_LOG_STARTING_DRIVER_X, driver.toString()));
            DesiredCapabilities desireCapibilities = WebDriverPropertyUtil.toDesireCapabilities(
                    RunConfiguration.getExecutionDriverProperty(), driver);
            WebDriver webDriver = null;
            switch (driver) {
            case FIREFOX_DRIVER:
                webDriver = new FirefoxDriver(desireCapibilities);
                break;
            case IE_DRIVER:
                System.setProperty(IE_DRIVER_PATH_PROPERTY_KEY, getIEDriverPath());
                webDriver = new InternetExplorerDriver(desireCapibilities);
                break;
            case SAFARI_DRIVER:
                webDriver = new SafariDriver(desireCapibilities);
                break;
            case CHROME_DRIVER:
                System.setProperty(CHROME_DRIVER_PATH_PROPERTY_KEY, getChromeDriverPath());
                webDriver = new ChromeDriver(desireCapibilities);
                break;
            case REMOTE_WEB_DRIVER:
                String remoteWebServerUrl = getRemoteWebDriverServerUrl();
                String remoteWebServerType = getRemoteWebDriverServerType();
                if (remoteWebServerType == null) {
                    remoteWebServerType = REMOTE_WEB_DRIVER_TYPE_SELENIUM;
                }
                KeywordLogger.getInstance().logInfo(
                        MessageFormat.format(StringConstants.XML_LOG_CONNECTING_TO_REMOTE_WEB_SERVER_X_WITH_TYPE_Y,
                                remoteWebServerUrl, remoteWebServerType));
                if (remoteWebServerType.equals(REMOTE_WEB_DRIVER_TYPE_APPIUM)) {
                    Object platformName = desireCapibilities.getCapability(APPIUM_CAPABILITY_PLATFORM_NAME);
                    if (platformName == null) {
                        throw new StepFailedException(MessageFormat.format(
                                StringConstants.DRI_MISSING_PROPERTY_X_FOR_APPIUM_REMOTE_WEB_DRIVER,
                                APPIUM_CAPABILITY_PLATFORM_NAME));
                    }
                    if (platformName instanceof String) {
                        if (APPIUM_CAPABILITY_PLATFORM_NAME_ADROID.equalsIgnoreCase((String) platformName)) {
                            webDriver = new SwipeableAndroidDriver(new URL(remoteWebServerUrl),
                                    WebDriverPropertyUtil.toDesireCapabilities(
                                            RunConfiguration.getExecutionDriverProperty(),
                                            DesiredCapabilities.android(), false));
                        } else if (APPIUM_CAPABILITY_PLATFORM_NAME_IOS.equalsIgnoreCase((String) platformName)) {
                            webDriver = new IOSDriver(new URL(remoteWebServerUrl),
                                    WebDriverPropertyUtil.toDesireCapabilities(
                                            RunConfiguration.getExecutionDriverProperty(),
                                            DesiredCapabilities.iphone(), false));
                        } else {
                            throw new StepFailedException(MessageFormat.format(
                                    StringConstants.DRI_PLATFORM_NAME_X_IS_NOT_SUPPORTED_FOR_APPIUM_REMOTE_WEB_DRIVER,
                                    platformName));
                        }
                    }
                    break;
                }
                webDriver = new RemoteWebDriver(new URL(remoteWebServerUrl), desireCapibilities);
                break;
            case ANDROID_DRIVER:
                webDriver = WebMobileDriverFactory.getInstance().getAndroidDriver(getMobileDeviceName());
                break;
            case IOS_DRIVER:
                webDriver = WebMobileDriverFactory.getInstance().getIosDriver(getMobileDeviceName());
                break;
            default:
                throw new StepFailedException(MessageFormat.format(StringConstants.DRI_ERROR_DRIVER_X_NOT_IMPLEMENTED,
                        driver.getName()));
            }
            localWebServerStorage.set(webDriver);
            setTimeout();
            KeywordLogger.getInstance()
                    .logRunData("sessionId", ((RemoteWebDriver) webDriver).getSessionId().toString());
            return webDriver;
        } catch (Error e) {
            KeywordLogger.getInstance().logMessage(LogLevel.WARNING, e.getMessage());
            throw new StepFailedException(e);
        }
    }

    public static WebDriver openWebDriver(DriverType driver, String projectDir, Object options) throws Exception {
        try {
            if (!(driver instanceof WebUIDriverType)) {
                return null;
            }
            closeWebDriver();
            WebDriver webDriver = null;
            WebUIDriverType webUIDriver = (WebUIDriverType) driver;
            switch (webUIDriver) {
            case FIREFOX_DRIVER:
                if (options instanceof FirefoxProfile) {
                    webDriver = new FirefoxDriver((FirefoxProfile) options);
                } else {
                    webDriver = new FirefoxDriver();
                }
                break;
            case IE_DRIVER:
                System.setProperty(IE_DRIVER_PATH_PROPERTY_KEY, getIEDriverPath());
                webDriver = new InternetExplorerDriver();
                break;
            case SAFARI_DRIVER:
                webDriver = new SafariDriver();
                break;
            case CHROME_DRIVER:
                System.setProperty(CHROME_DRIVER_PATH_PROPERTY_KEY, getChromeDriverPath());
                if (options instanceof DesiredCapabilities) {
                    ChromeDriver chromeDriver = new ChromeDriver((DesiredCapabilities) options);
                    return chromeDriver;
                }
                break;
            default:
                throw new StepFailedException(MessageFormat.format(StringConstants.DRI_ERROR_DRIVER_X_NOT_IMPLEMENTED,
                        driver.getName()));
            }
            localWebServerStorage.set(webDriver);
            setTimeout();
            return webDriver;
        } catch (Error e) {
            KeywordLogger.getInstance().logMessage(LogLevel.WARNING, e.getMessage());
            throw new StepFailedException(e);
        }
    }

    private static void setTimeout() {
        localWebServerStorage.get().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    }

    public static WebDriver getWebDriver() throws StepFailedException, WebDriverException {
        verifyWebDriver();
        return localWebServerStorage.get();
    }

    private static void verifyWebDriver() throws StepFailedException, WebDriverException {
        verifyWebDriverIsOpen();
        try {
            if (null == ((RemoteWebDriver) localWebServerStorage.get()).getSessionId()) {
                switchToAvailableWindow();
            } else {
                checkIfWebDriverIsBlock();
            }
        } catch (WebDriverException e) {
            if (!(e instanceof NoSuchWindowException) && e.getMessage() != null
                    && !e.getMessage().startsWith(JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE)) {
                throw e;
            }
        }
    }

    private static void verifyWebDriverIsOpen() throws WebDriverException {
        if (localWebServerStorage.get() == null) {
            throw new BrowserNotOpenedException();
        }
    }

    // Only check for IE
    private static void checkIfWebDriverIsBlock() throws StepFailedException {
        if (getExecutedBrowser() == WebUIDriverType.IE_DRIVER) {
            // return if there is an alert blocking
            // if (isAlertPresent()) {
            // return;
            // }
            final WebDriver ieDriver = localWebServerStorage.get();
            Thread ieSafeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ieDriver.getWindowHandle();
                    } catch (WebDriverException e) {
                        // Ignore since we only check for hanging thread
                    }
                }
            });
            ieSafeThread.start();
            float count = 0;
            while (ieSafeThread.isAlive()) {
                if (count > getWaitForIEHanging()) {
                    ieSafeThread.interrupt();
                    throw new StepFailedException(MessageFormat.format(
                            StringConstants.DRI_MSG_UNABLE_REACH_WEB_DRI_TIMEOUT, RunConfiguration.getTimeOut()));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Thread is interrupted, do nothing
                }
                count += 0.1;
            }
        }
    }

    public static Alert getAlert() throws WebDriverException {
        verifyWebDriverIsOpen();
        Alert alert = null;
        if (getExecutedBrowser() == WebUIDriverType.IE_DRIVER) {
            final WebDriver ieDriver = localWebServerStorage.get();
            Thread ieSafeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        try {
                            ieDriver.switchTo().alert();
                        } catch (Exception e) {
                            if (!(e instanceof NoSuchWindowException) && e.getMessage() != null
                                    && !e.getMessage().startsWith(JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE)) {
                                throw e;
                            }
                            switchToAvailableWindow();
                            ieDriver.switchTo().alert();
                        }
                    } catch (WebDriverException e) {
                        // Ignore since we only check for hanging thread
                    }
                }
            });
            ieSafeThread.start();
            float count = 0;
            while (ieSafeThread.isAlive()) {
                if (count > getWaitForIEHanging()) {
                    ieSafeThread.interrupt();
                    throw new StepFailedException(MessageFormat.format(
                            StringConstants.DRI_MSG_UNABLE_REACH_WEB_DRI_TIMEOUT, RunConfiguration.getTimeOut()));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Thread is interrupted, do nothing
                }
                count += 0.1;
            }
        }
        try {
            try {
                alert = localWebServerStorage.get().switchTo().alert();
            } catch (Exception e) {
                if (!(e instanceof NoSuchWindowException) && e.getMessage() != null
                        && !e.getMessage().startsWith(JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE)) {
                    throw e;
                }
                switchToAvailableWindow();
                alert = localWebServerStorage.get().switchTo().alert();
            }
            return alert;
        } catch (NoAlertPresentException ex) {
            return null;
        }
    }

    public static boolean waitForAlert(int timeOut) {
        verifyWebDriverIsOpen();
        float count = 0;
        while (count < timeOut) {
            Alert alert = getAlert();
            if (alert != null) {
                return true;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Thread is interrupted, do nothing
            }
            count += 0.5;
        }
        return false;
    }

    public static void switchToAvailableWindow() {
        verifyWebDriverIsOpen();
        try {
            localWebServerStorage.get().switchTo().window("");
        } catch (WebDriverException e) {
            if (!(e instanceof NoSuchWindowException) && e.getMessage() != null
                    && !e.getMessage().startsWith(JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE)) {
                throw e;
            }
            // default window is closed, try to switch to available window
            Set<String> availableWindows = localWebServerStorage.get().getWindowHandles();
            for (String windowId : availableWindows) {
                try {
                    localWebServerStorage.get().switchTo().window(windowId);
                    return;
                } catch (WebDriverException exception) {
                    if (!(exception instanceof NoSuchWindowException) && e.getMessage() != null
                            && !exception.getMessage().startsWith(JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE)) {
                        throw exception;
                    }
                    continue;
                }
            }
        }
    }

    private static String getIEDriverPath() {
        return RunConfiguration.getStringProperty(IE_DRIVER_PATH_PROPERTY);
    }

    private static String getChromeDriverPath() {
        return RunConfiguration.getStringProperty(CHROME_DRIVER_PATH_PROPERTY);
    }

    private static int getWaitForIEHanging() {
        if (getExecutedBrowser() != WebUIDriverType.IE_DRIVER) {
            throw new IllegalArgumentException(StringConstants.XML_LOG_ERROR_BROWSER_NOT_IE);
        }
        return Integer.parseInt(RunConfiguration.getStringProperty(WAIT_FOR_IE_HANGING_PROPERTY));
    }

    public static DriverType getExecutedBrowser() {
        DriverType webDriverType = null;
        if (RunConfiguration.getProperty(EXECUTED_BROWSER_PROPERTY) != null) {
            webDriverType = WebUIDriverType.valueOf(RunConfiguration.getStringProperty(EXECUTED_BROWSER_PROPERTY));
        }
        if (webDriverType == null && RunConfiguration.getProperty(EXECUTED_MOBILE_PLATFORM) != null) {
            webDriverType = WebUIDriverType.valueOf(RunConfiguration.getStringProperty(EXECUTED_MOBILE_PLATFORM));
        }
        return webDriverType;
    }

    public static String getRemoteWebDriverServerUrl() {
        return RunConfiguration.getStringProperty(REMOTE_WEB_DRIVER_URL);
    }

    public static String getRemoteWebDriverServerType() {
        return RunConfiguration.getStringProperty(REMOTE_WEB_DRIVER_TYPE);
    }

    public static String getMobilePlatform() {
        return RunConfiguration.getStringProperty(EXECUTED_MOBILE_PLATFORM);
    }

    public static String getMobileDeviceName() {
        return RunConfiguration.getStringProperty(EXECUTED_MOBILE_DEVICE_NAME);
    }

    public static void closeWebDriver() {
        WebDriver webDriver = localWebServerStorage.get();
        if (null != webDriver && null != ((RemoteWebDriver) webDriver).getSessionId()) {
            try {
                webDriver.quit();
                WebUIDriverType driver = (WebUIDriverType) getExecutedBrowser();
                switch (driver) {
                case ANDROID_DRIVER:
                case IOS_DRIVER:
                    WebMobileDriverFactory.getInstance().quitServer();
                    break;
                default:
                    break;

                }
            } catch (UnreachableBrowserException e) {
                KeywordLogger.getInstance().logWarning(StringConstants.DRI_LOG_WARNING_BROWSER_NOT_REACHABLE);
            }
        }
        localWebServerStorage.set(null);
    }
}
