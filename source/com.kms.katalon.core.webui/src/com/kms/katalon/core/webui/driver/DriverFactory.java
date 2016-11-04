package com.kms.katalon.core.webui.driver;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverLogLevel;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.internal.BuildInfo;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.webui.common.WebUiCommonHelper;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.ie.InternetExploreDriverServiceBuilder;
import com.kms.katalon.core.webui.exception.BrowserNotOpenedException;
import com.kms.katalon.core.webui.util.FirefoxExecutable;
import com.kms.katalon.core.webui.util.WebDriverPropertyUtil;
import com.kms.katalon.selenium.FirefoxDriver47;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import io.appium.java_client.ios.IOSDriver;

public class DriverFactory {

    private static final String IE_DRIVER_SERVER_LOG_FILE_NAME = "IEDriverServer.log";

    public static final String WEB_UI_DRIVER_PROPERTY = StringConstants.CONF_PROPERTY_WEBUI_DRIVER;

    public static final String MOBILE_DRIVER_PROPERTY = StringConstants.CONF_PROPERTY_MOBILE_DRIVER;

    public static final String APPIUM_LOG_PROPERTY = StringConstants.CONF_APPIUM_LOG_FILE;

    private static final String APPIUM_CAPABILITY_PLATFORM_NAME_ADROID = "android";

    private static final String APPIUM_CAPABILITY_PLATFORM_NAME_IOS = "ios";

    private static final String APPIUM_CAPABILITY_PLATFORM_NAME = "platformName";

    private static final String REMOTE_WEB_DRIVER_TYPE_APPIUM = "Appium";

    private static final String REMOTE_WEB_DRIVER_TYPE_SELENIUM = "Selenium";

    public static final String CHROME_DRIVER_PATH_PROPERTY_KEY = "webdriver.chrome.driver";

    private static final String IE_DRIVER_PATH_PROPERTY_KEY = "webdriver.ie.driver";

    // Temp error constant message for issues
    // https://code.google.com/p/selenium/issues/detail?id=7977
    private static final String JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE = "[JavaScript Error: \"h is null\"";

    public static final String IE_DRIVER_PATH_PROPERTY = StringConstants.CONF_PROPERTY_IE_DRIVER_PATH;

    public static final String EDGE_DRIVER_PATH_PROPERTY = StringConstants.CONF_PROPERTY_EDGE_DRIVER_PATH;

    public static final String CHROME_DRIVER_PATH_PROPERTY = StringConstants.CONF_PROPERTY_CHROME_DRIVER_PATH;

    public static final String WAIT_FOR_IE_HANGING_PROPERTY = StringConstants.CONF_PROPERTY_WAIT_FOR_IE_HANGING;
    
    public static final String ENABLE_PAGE_LOAD_TIMEOUT = StringConstants.CONF_PROPERTY_ENABLE_PAGE_LOAD_TIMEOUT;
    
    public static final String DEFAULT_PAGE_LOAD_TIMEOUT = StringConstants.CONF_PROPERTY_DEFAULT_PAGE_LOAD_TIMEOUT;
    
    public static final String IGNORE_PAGE_LOAD_TIMEOUT_EXCEPTION = StringConstants.CONF_PROPERTY_IGNORE_PAGE_LOAD_TIMEOUT_EXCEPTION;

    public static final String EXECUTED_BROWSER_PROPERTY = StringConstants.CONF_PROPERTY_EXECUTED_BROWSER;

    public static final String REMOTE_WEB_DRIVER_URL = StringConstants.CONF_PROPERTY_REMOTE_WEB_DRIVER_URL;

    public static final String REMOTE_WEB_DRIVER_TYPE = StringConstants.CONF_PROPERTY_REMOTE_WEB_DRIVER_TYPE;

    public static final String DEBUG_PORT = "debugPort";

    public static final String DEBUG_HOST = "debugHost";

    public static final String DEFAULT_FIREFOX_DEBUG_PORT = "10001";

    public static final String DEFAULT_CHROME_DEBUG_PORT = "10002";

    public static final String DEFAULT_DEBUG_HOST = "localhost";

    private static final int REMOTE_BROWSER_CONNECT_TIMEOUT = 60000;

    private static final String HEADLESS_BROWSER_NAME = "JBrowser (headless)";

    private static final ThreadLocal<WebDriver> localWebServerStorage = new ThreadLocal<WebDriver>() {
        @Override
        protected WebDriver initialValue() {
            return null;
        }
    };

    private static final ThreadLocal<EdgeDriverService> localEdgeDriverServiceStorage = new ThreadLocal<EdgeDriverService>() {
        @Override
        protected EdgeDriverService initialValue() {
            return new EdgeDriverService.Builder().usingDriverExecutable(new File(getEdgeDriverPath()))
                    .usingAnyFreePort()
                    .build();
        }
    };

    private static InternetExplorerDriverService ieDriverService;

    /**
     * Open a new web driver based on the execution configuration
     * 
     * @return the created WebDriver
     * @throws Exception
     */
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

            Map<String, Object> driverPreferenceProps = RunConfiguration.getDriverPreferencesProperties(WEB_UI_DRIVER_PROPERTY);
            DesiredCapabilities desireCapibilities = null;
            if (driverPreferenceProps != null) {
                desireCapibilities = WebDriverPropertyUtil.toDesireCapabilities(driverPreferenceProps, driver);
            }
            WebDriver webDriver = null;
            switch (driver) {
                case FIREFOX_DRIVER:
                    if (FirefoxExecutable.isUsingFirefox47AndAbove(desireCapibilities)) {
                        //webDriver = FirefoxExecutable.startGeckoDriver(desireCapibilities);
                        webDriver = new FirefoxDriver47(desireCapibilities);
                    } else {
                        webDriver = new FirefoxDriver(desireCapibilities);
                    }
                    break;
                case IE_DRIVER:
                    ieDriverService = new InternetExploreDriverServiceBuilder().withLogLevel(
                            InternetExplorerDriverLogLevel.TRACE)
                            .usingDriverExecutable(new File(getIEDriverPath()))
                            .withLogFile(
                                    new File(RunConfiguration.getLogFolderPath() + File.separator + IE_DRIVER_SERVER_LOG_FILE_NAME))
                            .build();
                    webDriver = new InternetExplorerDriver(ieDriverService, desireCapibilities);
                    break;
                case SAFARI_DRIVER:
                    webDriver = new SafariDriver(desireCapibilities);
                    break;
                case CHROME_DRIVER:
                    System.setProperty(CHROME_DRIVER_PATH_PROPERTY_KEY, getChromeDriverPath());
                    webDriver = new ChromeDriver(desireCapibilities);
                    break;
                case REMOTE_WEB_DRIVER:
                case KOBITON_WEB_DRIVER:
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
                                        WebDriverPropertyUtil.toDesireCapabilities(driverPreferenceProps,
                                                DesiredCapabilities.android(), false));
                            } else if (APPIUM_CAPABILITY_PLATFORM_NAME_IOS.equalsIgnoreCase((String) platformName)) {
                                webDriver = new IOSDriver(new URL(remoteWebServerUrl),
                                        WebDriverPropertyUtil.toDesireCapabilities(driverPreferenceProps,
                                                DesiredCapabilities.iphone(), false));
                            } else {
                                throw new StepFailedException(
                                        MessageFormat.format(
                                                StringConstants.DRI_PLATFORM_NAME_X_IS_NOT_SUPPORTED_FOR_APPIUM_REMOTE_WEB_DRIVER,
                                                platformName));
                            }
                        }
                        break;
                    }
                    webDriver = new RemoteWebDriver(new URL(remoteWebServerUrl), desireCapibilities);
                    break;
                case ANDROID_DRIVER:
                case IOS_DRIVER:
                    webDriver = WebMobileDriverFactory.createMobileDriver(driver);
                    break;
                case EDGE_DRIVER:
                    EdgeDriverService edgeService = localEdgeDriverServiceStorage.get();
                    if (!edgeService.isRunning()) {
                        edgeService.start();
                    }
                    webDriver = new EdgeDriver(edgeService, WebDriverPropertyUtil.toDesireCapabilities(
                            driverPreferenceProps, DesiredCapabilities.edge(), false));
                    break;
                case REMOTE_FIREFOX_DRIVER:
                    String debugHost = RunConfiguration.getDriverSystemProperty(WEB_UI_DRIVER_PROPERTY, DEBUG_HOST,
                            DriverFactory.DEFAULT_DEBUG_HOST);
                    String debugPort = RunConfiguration.getDriverSystemProperty(WEB_UI_DRIVER_PROPERTY, DEBUG_PORT,
                            DriverFactory.DEFAULT_FIREFOX_DEBUG_PORT);
                    desireCapibilities.merge(DesiredCapabilities.firefox());
                    URL firefoxDriverUrl = new URL("http", debugHost, Integer.parseInt(debugPort), "/hub");
                    webDriver = new RemoteWebDriver(firefoxDriverUrl, desireCapibilities);
                    waitForRemoteBrowserReady(firefoxDriverUrl);
                    break;
                case REMOTE_CHROME_DRIVER:
                    debugHost = RunConfiguration.getDriverSystemProperty(WEB_UI_DRIVER_PROPERTY, DEBUG_HOST,
                            DriverFactory.DEFAULT_DEBUG_HOST);
                    debugPort = RunConfiguration.getDriverSystemProperty(WEB_UI_DRIVER_PROPERTY, DEBUG_PORT,
                            DriverFactory.DEFAULT_CHROME_DEBUG_PORT);
                    System.setProperty(CHROME_DRIVER_PATH_PROPERTY_KEY, getChromeDriverPath());
                    desireCapibilities.merge(DesiredCapabilities.chrome());
                    Map<String, Object> chromeOptions = new HashMap<String, Object>();
                    chromeOptions.put("debuggerAddress", debugHost + ":" + debugPort);
                    desireCapibilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                    // Start Chrome-driver in background
                    int chromeDriverPort = determineNextFreePort(9515);
                    URL chromeDriverUrl = new URL("http", debugHost, chromeDriverPort, "/");
                    Runtime.getRuntime().exec(
                            new String[] { System.getProperty(CHROME_DRIVER_PATH_PROPERTY_KEY),
                                    "--port=" + chromeDriverPort });
                    waitForRemoteBrowserReady(chromeDriverUrl);
                    webDriver = new RemoteWebDriver(chromeDriverUrl, desireCapibilities);
                    break;
                case HEADLESS_DRIVER:
                    webDriver = new JBrowserDriver(desireCapibilities);
                    break;
                default:
                    throw new StepFailedException(MessageFormat.format(
                            StringConstants.DRI_ERROR_DRIVER_X_NOT_IMPLEMENTED, driver.getName()));
            }
            localWebServerStorage.set(webDriver);
            RunConfiguration.storeDriver(webDriver);
            setTimeout();
            logBrowserRunData(webDriver);
            return webDriver;
        } catch (Error e) {
            KeywordLogger.getInstance().logMessage(LogLevel.WARNING, e.getMessage());
            throw new StepFailedException(e);
        }
    }

    private static void logBrowserRunData(WebDriver webDriver) {
        if (webDriver == null) {
            return;
        }

        KeywordLogger logger = KeywordLogger.getInstance();
        logger.logRunData("sessionId", ((RemoteWebDriver) webDriver).getSessionId().toString());
        logger.logRunData("browser", getBrowserVersion(webDriver));
        logger.logRunData("platform",
                webDriver.getClass() == RemoteWebDriver.class ? ((RemoteWebDriver) webDriver).getCapabilities()
                        .getPlatform()
                        .toString() : System.getProperty("os.name"));
        logger.logRunData(StringConstants.XML_LOG_SELENIUM_VERSION, new BuildInfo().getReleaseLabel());
    }

    private static String getBrowserVersion(WebDriver webDriver) {
        if (webDriver instanceof JBrowserDriver) {
            return HEADLESS_BROWSER_NAME;
        }
        return WebUiCommonHelper.getBrowserAndVersion(webDriver);
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
                        DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
                        desiredCapabilities.setCapability(FirefoxDriver.PROFILE, (FirefoxProfile) options);
                        if (FirefoxExecutable.isUsingFirefox47AndAbove(desiredCapabilities)) {
                            //webDriver = FirefoxExecutable.startGeckoDriver(desiredCapabilities);
                            webDriver = new FirefoxDriver47(desiredCapabilities);
                        } else {
                            webDriver = new FirefoxDriver(desiredCapabilities);
                        }
                    } else {
                        webDriver = new FirefoxDriver();
                    }
                    break;
                case IE_DRIVER:
                    System.setProperty(IE_DRIVER_PATH_PROPERTY_KEY, getIEDriverPath());
                    if (options instanceof DesiredCapabilities) {
                        webDriver = new InternetExplorerDriver((DesiredCapabilities) options);
                        break;
                    }
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
                    throw new StepFailedException(MessageFormat.format(
                            StringConstants.DRI_ERROR_DRIVER_X_NOT_IMPLEMENTED, driver.getName()));
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
        WebDriver webDriver = localWebServerStorage.get();
        if (webDriver instanceof EdgeDriver) {
            return;
        }
        Timeouts timeouts = webDriver.manage().timeouts();
        timeouts.implicitlyWait(0, TimeUnit.SECONDS);
        if (isEnablePageLoadTimeout()) {
            timeouts.pageLoadTimeout(getDefaultPageLoadTimeout(), TimeUnit.SECONDS);
        }
    }

    /**
     * Get the current active web driver
     * 
     * @return the current active WebDriver
     * @throws StepFailedException
     * @throws WebDriverException
     */
    public static WebDriver getWebDriver() throws StepFailedException, WebDriverException {
        try {
            verifyWebDriver();
        } catch (BrowserNotOpenedException e) {
            for (Object driverObject : RunConfiguration.getStoredDrivers()) {
                if (driverObject instanceof RemoteWebDriver) {
                    return (RemoteWebDriver) driverObject;
                }
            }
            throw e;
        }
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
        long miliseconds = System.currentTimeMillis();
        while (count < timeOut) {
            Alert alert = getAlert();
            if (alert != null) {
                return true;
            }
            count += ((System.currentTimeMillis() - miliseconds) / 1000);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Thread is interrupted, do nothing
            }
            count += 0.5;
            miliseconds = System.currentTimeMillis();
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

    public static int getCurrentWindowIndex() {
        verifyWebDriverIsOpen();
        String currentWindowHandle = localWebServerStorage.get().getWindowHandle();
        Set<String> availableWindowHandles = localWebServerStorage.get().getWindowHandles();
        int count = 0;
        for (String windowHandle : availableWindowHandles) {
            if (windowHandle.equals(currentWindowHandle)) {
                return count;
            }
            count++;
        }
        throw new StepFailedException(StringConstants.XML_LOG_ERROR_CANNOT_FOUND_WINDOW_HANDLE);
    }

    private static String getIEDriverPath() {
        return RunConfiguration.getDriverSystemProperty(WEB_UI_DRIVER_PROPERTY, IE_DRIVER_PATH_PROPERTY);
    }

    private static String getEdgeDriverPath() {
        return RunConfiguration.getDriverSystemProperty(WEB_UI_DRIVER_PROPERTY, EDGE_DRIVER_PATH_PROPERTY);
    }

    public static String getChromeDriverPath() {
        return RunConfiguration.getDriverSystemProperty(WEB_UI_DRIVER_PROPERTY, CHROME_DRIVER_PATH_PROPERTY);
    }

    private static int getWaitForIEHanging() {
        if (getExecutedBrowser() != WebUIDriverType.IE_DRIVER) {
            throw new IllegalArgumentException(StringConstants.XML_LOG_ERROR_BROWSER_NOT_IE);
        }
        return Integer.parseInt(RunConfiguration.getDriverSystemProperty(WEB_UI_DRIVER_PROPERTY,
                WAIT_FOR_IE_HANGING_PROPERTY));
    }
    
    public static boolean isEnablePageLoadTimeout() {
        return RunConfiguration.getBooleanProperty(ENABLE_PAGE_LOAD_TIMEOUT, RunConfiguration.getExecutionGeneralProperties());
    }
    
    public static int getDefaultPageLoadTimeout() {
        return RunConfiguration.getIntProperty(DEFAULT_PAGE_LOAD_TIMEOUT, RunConfiguration.getExecutionGeneralProperties());
    }
    
    public static boolean isIgnorePageLoadTimeoutException() {
        return RunConfiguration.getBooleanProperty(IGNORE_PAGE_LOAD_TIMEOUT_EXCEPTION, RunConfiguration.getExecutionGeneralProperties());
    }

    public static DriverType getExecutedBrowser() {
        DriverType webDriverType = null;
        String driverTypeString = RunConfiguration.getDriverSystemProperty(WEB_UI_DRIVER_PROPERTY,
                EXECUTED_BROWSER_PROPERTY);
        if (driverTypeString != null) {
            webDriverType = WebUIDriverType.valueOf(driverTypeString);
        }

        if (webDriverType == null
                && RunConfiguration.getDriverSystemProperty(MOBILE_DRIVER_PROPERTY,
                        WebMobileDriverFactory.EXECUTED_MOBILE_PLATFORM) != null) {
            webDriverType = WebUIDriverType.valueOf(RunConfiguration.getDriverSystemProperty(MOBILE_DRIVER_PROPERTY,
                    WebMobileDriverFactory.EXECUTED_MOBILE_PLATFORM));
        }
        return webDriverType;
    }

    public static String getRemoteWebDriverServerUrl() {
        return RunConfiguration.getDriverSystemProperty(WEB_UI_DRIVER_PROPERTY, REMOTE_WEB_DRIVER_URL);
    }

    public static String getRemoteWebDriverServerType() {
        return RunConfiguration.getDriverSystemProperty(WEB_UI_DRIVER_PROPERTY, REMOTE_WEB_DRIVER_TYPE);
    }

    public static void closeWebDriver() {
        WebUIDriverType driverType = (WebUIDriverType) getExecutedBrowser();
        if (driverType == WebUIDriverType.IE_DRIVER) {
            quitIE();
            return;
        }
        WebDriver webDriver = localWebServerStorage.get();
        if (null != webDriver && null != ((RemoteWebDriver) webDriver).getSessionId()) {
            try {
                webDriver.quit();
                switch (driverType) {
                    case ANDROID_DRIVER:
                    case IOS_DRIVER:
                        WebMobileDriverFactory.closeDriver();
                        break;
                    case EDGE_DRIVER:
                        EdgeDriverService edgeDriverService = localEdgeDriverServiceStorage.get();
                        if (edgeDriverService.isRunning()) {
                            edgeDriverService.stop();
                        }
                        break;
                    default:
                        break;

                }
            } catch (UnreachableBrowserException e) {
                KeywordLogger.getInstance().logWarning(StringConstants.DRI_LOG_WARNING_BROWSER_NOT_REACHABLE);
            }
        }
        localWebServerStorage.set(null);
        RunConfiguration.removeDriver(webDriver);
    }

    private static void quitIE() {
        try {
            WebDriver webDriver = localWebServerStorage.get();
            if (null != webDriver && null != ((RemoteWebDriver) webDriver).getSessionId()) {
                webDriver.quit();
            }
        } catch (UnreachableBrowserException e) {
            // browser may have been already closed, ignored
        }
        quitIEDriverServer();
    }

    private static void quitIEDriverServer() {
        if (ieDriverService == null) {
            return;
        }
        ieDriverService.stop();
    }

    private static void waitForRemoteBrowserReady(URL url) throws Exception {
        long waitUntil = System.currentTimeMillis() + REMOTE_BROWSER_CONNECT_TIMEOUT;
        boolean connectable = false;
        while (!connectable) {
            try {
                url.openConnection().connect();
                connectable = true;
            } catch (IOException e) {
                // Cannot connect yet.
            }

            if (waitUntil < System.currentTimeMillis()) {
                throw new Exception(String.format(
                        "Unable to connect to browser on host %s and port %s after %s seconds.", url.getHost(),
                        url.getPort(), REMOTE_BROWSER_CONNECT_TIMEOUT));
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }

    private static int determineNextFreePort(int port) {
        int newport;
        NetworkUtils networkUtils = new NetworkUtils();
        for (newport = port; newport < port + 2000; newport++) {
            InetSocketAddress address = new InetSocketAddress(networkUtils.obtainLoopbackIp4Address(), newport);
            try (Socket socket = new Socket()) {
                socket.bind(address);
                return newport;
            } catch (IOException e) {
                // Port is already bound. Skip it and continue
            }
        }
        throw new WebDriverException(String.format("Cannot find free port in the range %d to %d ", port, newport));
    }
}