package com.kms.katalon.core.appium.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.AndroidServerFlag;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.net.UrlChecker.TimeoutException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import com.kms.katalon.core.appium.constants.AppiumStringConstants;
import com.kms.katalon.core.appium.exception.AppiumStartException;
import com.kms.katalon.core.appium.exception.IOSWebkitStartException;
import com.kms.katalon.core.appium.exception.MobileDriverInitializeException;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.KeywordLogger;

public class AppiumDriverManager {
    public static final String EXECUTED_PLATFORM = AppiumStringConstants.CONF_EXECUTED_PLATFORM;

    public static final String EXECUTED_DEVICE_ID = AppiumStringConstants.CONF_EXECUTED_DEVICE_ID;

    public static final String EXECUTED_DEVICE_MANUFACTURER = AppiumStringConstants.CONF_EXECUTED_DEVICE_MANUFACTURER;

    public static final String EXECUTED_DEVICE_MODEL = AppiumStringConstants.CONF_EXECUTED_DEVICE_MODEL;

    public static final String EXECUTED_DEVICE_NAME = AppiumStringConstants.CONF_EXECUTED_DEVICE_NAME;

    public static final String EXECUTED_DEVICE_OS = AppiumStringConstants.CONF_EXECUTED_DEVICE_OS;

    public static final String EXECUTED_DEVICE_OS_VERSON = AppiumStringConstants.CONF_EXECUTED_DEVICE_OS_VERSON;

    private static String APPIUM_RELATIVE_PATH_FROM_APPIUM_FOLDER_OLD = "bin" + File.separator + "appium.js";

    private static String APPIUM_RELATIVE_PATH_FROM_APPIUM_FOLDER_NEW = "build" + File.separator + "lib"
            + File.separator + "main.js";

    private static String APPIUM_RELATIVE_PATH_FROM_APPIUM_GUI = "node_modules" + File.separator + "appium";

    private static final String APPIUM_TEMP_RELATIVE_PATH = System.getProperty("java.io.tmpdir") + File.separator
            + "Katalon" + File.separator + "Appium" + File.separator + "Temp";

    private static final String C_FLAG = "-c";

    private static final String DEFAULT_APPIUM_SERVER_ADDRESS = "127.0.0.1";

    private static final String IOS_WEBKIT_DEBUG_PROXY_EXECUTABLE = "ios_webkit_debug_proxy";

    private static final int DEFAULT_WEB_PROXY_PORT = 27753;

    private static final String IOS_WEBKIT_LOG_FILE_NAME = "appium-proxy-server.log";

    private static final String MSG_START_IOS_WEBKIT_SUCCESS = "ios_webkit_debug_proxy server started on port "
            + DEFAULT_WEB_PROXY_PORT;

    private static final String LOCALHOST_PREFIX = "http://localhost:";

    private static final ThreadLocal<Process> localStorageWebProxyProcess = new ThreadLocal<Process>() {
        @Override
        protected Process initialValue() {
            return null;
        }
    };

    private static final ThreadLocal<AppiumDriverLocalService> localStorageAppiumServer = new ThreadLocal<AppiumDriverLocalService>() {
        @Override
        protected AppiumDriverLocalService initialValue() {
            return null;
        }
    };

    private static final ThreadLocal<AppiumDriver<?>> localStorageAppiumDriver = new ThreadLocal<AppiumDriver<?>>() {
        @Override
        protected AppiumDriver<?> initialValue() {
            return null;
        }
    };

    private static void ensureWebProxyServerStarted(String deviceId) throws IOException, InterruptedException,
            IOSWebkitStartException {
        if (!isWebProxyServerStarted(1)) {
            startWebProxyServer(deviceId);
        }
    }

    /**
     * Start proxy server, this server is optional
     * 
     * @param deviceId
     * @throws Exception
     */
    private static void startWebProxyServer(String deviceId) throws IOException, InterruptedException,
            IOSWebkitStartException {
        String[] webProxyServerCmd = { IOS_WEBKIT_DEBUG_PROXY_EXECUTABLE, C_FLAG,
                deviceId + ":" + DEFAULT_WEB_PROXY_PORT };
        ProcessBuilder webProxyServerProcessBuilder = new ProcessBuilder(webProxyServerCmd);
        webProxyServerProcessBuilder.redirectOutput(new File(
                new File(RunConfiguration.getAppiumLogFilePath()).getParent() + File.separator
                        + IOS_WEBKIT_LOG_FILE_NAME));

        Process webProxyProcess = webProxyServerProcessBuilder.start();

        // Check again if proxy server started
        if (!isServerStarted(10, new URL(LOCALHOST_PREFIX + DEFAULT_WEB_PROXY_PORT))) {
            throw new IOSWebkitStartException();
        }
        localStorageWebProxyProcess.set(webProxyProcess);
        KeywordLogger.getInstance().logInfo(MSG_START_IOS_WEBKIT_SUCCESS);
    }

    private static boolean isWebProxyServerStarted(int timeOut) {
        if (localStorageWebProxyProcess.get() == null) {
            return false;
        }
        try {
            localStorageWebProxyProcess.get().exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            // Process is running
        }
        try {
            return isServerStarted(timeOut, new URL(LOCALHOST_PREFIX + DEFAULT_WEB_PROXY_PORT));
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private static boolean isServerStarted(int timeToWait, URL url) {
        try {
            new UrlChecker().waitUntilAvailable(timeToWait, TimeUnit.SECONDS, url);
            return true;
        } catch (TimeoutException ex1) {}
        return false;
    }

    private static void ensureServicesStarted(DriverType driverType, String deviceId) throws IOException,
            InterruptedException, AppiumStartException {
        if (isIOSDriverType(driverType)) {
            // Proxy server is optional
            try {
                ensureWebProxyServerStarted(deviceId);
            } catch (IOException | InterruptedException | IOSWebkitStartException e) {
                KeywordLogger.getInstance().logInfo(e.getMessage());
            }
        }
        startAppiumServerJS(RunConfiguration.getTimeOut());
    }

    private static boolean isAndroidDriverType(DriverType driverType) {
        return StringUtils.equals(AppiumStringConstants.ANDROID, driverType.toString());
    }

    private static boolean isIOSDriverType(DriverType driverType) {
        return StringUtils.equals(AppiumStringConstants.IOS, driverType.toString());
    }

    public static void startAppiumServerJS(int timeout, Map<String, String> environmentVariables)
            throws AppiumStartException, IOException {
        if (localStorageAppiumServer.get() != null && localStorageAppiumServer.get().isRunning()) {
            return;
        }
        String appium = findAppiumJS();
        int freePort = getFreePort();
        AppiumDriverLocalService service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder().withArgument(
                GeneralServerFlag.LOG_LEVEL, getAppiumLogLevel())
                .withArgument(GeneralServerFlag.TEMP_DIRECTORY, createAppiumTempFile())
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withAppiumJS(new File(appium))
                .withArgument(AndroidServerFlag.CHROME_DRIVER_PORT, Integer.toString(getFreePort()))
                .withArgument(AndroidServerFlag.BOOTSTRAP_PORT_NUMBER, Integer.toString(getFreePort()))
                .withIPAddress(DEFAULT_APPIUM_SERVER_ADDRESS)
                .usingPort(freePort)
                .withEnvironment(environmentVariables)
                .withLogFile(new File(RunConfiguration.getAppiumLogFilePath()))
                .withStartUpTimeOut(new Long(timeout), TimeUnit.SECONDS));
        service.start();
        localStorageAppiumServer.set(service);
        KeywordLogger.getInstance().logInfo(
                MessageFormat.format(AppiumStringConstants.APPIUM_STARTED_ON_PORT, freePort));
    }

    private static String getAppiumLogLevel() {
        return RunConfiguration.getDriverSystemProperty(StringConstants.CONF_PROPERTY_MOBILE_DRIVER,
                StringConstants.CONF_APPIUM_LOG_LEVEL);
    }

    private static String createAppiumTempFile() {
        return APPIUM_TEMP_RELATIVE_PATH + System.currentTimeMillis();
    }

    private static String findAppiumJS() throws AppiumStartException {
        String appiumHome = RunConfiguration.getAppiumDirectory();
        if (StringUtils.isEmpty(appiumHome)) {
            throw new AppiumStartException(AppiumStringConstants.APPIUM_START_EXCEPTION_APPIUM_DIRECTORY_NOT_SET);
        }
        String appium = getAppiumJSPathFromNPMBuild(appiumHome);
        if (!new File(appium).exists()) {
            appium = getAppiumJSPathFromAppiumGUI(appiumHome);
        }
        if (!new File(appium).exists()) {
            throw new AppiumStartException(
                    AppiumStringConstants.APPIUM_START_EXCEPTION_APPIUM_DIRECTORY_INVALID_CANNOT_FIND_APPIUM_JS);
        }
        return appium;
    }

    private static String getAppiumJSPathFromAppiumGUI(String appiumHome) {
        String appiumFolderFromGUIAppium = appiumHome + File.separator + APPIUM_RELATIVE_PATH_FROM_APPIUM_GUI;
        return getAppiumJSPathFromNPMBuild(appiumFolderFromGUIAppium);
    }

    private static String getAppiumJSPathFromNPMBuild(String appiumHome) {
        String oldAppiumJSPath = appiumHome + File.separator + APPIUM_RELATIVE_PATH_FROM_APPIUM_FOLDER_OLD;
        if (!new File(oldAppiumJSPath).exists()) {
            return appiumHome + File.separator + APPIUM_RELATIVE_PATH_FROM_APPIUM_FOLDER_NEW;
        }
        return oldAppiumJSPath;
    }

    public static void startAppiumServerJS(int timeout) throws AppiumStartException, IOException {
        startAppiumServerJS(timeout, new HashMap<String, String>());
    }

    private static synchronized int getFreePort() {
        try (ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        } catch (IOException e) {
            // do nothing
        }
        return -1;
    }

    @SuppressWarnings("rawtypes")
    public static AppiumDriver<?> createMobileDriver(DriverType driverType, String deviceId,
            DesiredCapabilities capabilities) throws IOException, InterruptedException, AppiumStartException,
            MobileDriverInitializeException, MalformedURLException {
        ensureServicesStarted(driverType, deviceId);
        AppiumDriverLocalService appiumService = localStorageAppiumServer.get();
        if (appiumService == null) {
            throw new MobileDriverInitializeException(AppiumStringConstants.APPIUM_NOT_STARTED);
        }
        URL appiumServerUrl = new URL(appiumService.getUrl().toString());
        int time = 0;
        long currentMilis = System.currentTimeMillis();
        int timeOut = RunConfiguration.getTimeOut();
        while (time < timeOut) {
            try {
                AppiumDriver<?> driver = null;
                if (isIOSDriverType(driverType)) {
                    driver = new IOSDriver(appiumServerUrl, capabilities);
                } else if (isAndroidDriverType(driverType)) {
                    driver = new SwipeableAndroidDriver(appiumServerUrl, capabilities);
                }
                if (driver == null) {
                    throw new MobileDriverInitializeException(MessageFormat.format(
                            AppiumStringConstants.CANNOT_START_MOBILE_DRIVER_INVALID_TYPE, driver));
                }
                localStorageAppiumDriver.set(driver);
                new AppiumRequestService(appiumService).logAppiumInfo();
                return driver;
            } catch (UnreachableBrowserException e) {
                long newMilis = System.currentTimeMillis();
                time += ((newMilis - currentMilis) / 1000);
                currentMilis = newMilis;
                continue;
            }
        }
        throw new MobileDriverInitializeException(MessageFormat.format(
                AppiumStringConstants.CANNOT_CONNECT_TO_APPIUM_AFTER_X, timeOut));
    }

    public static void closeDriver() {
        AppiumDriver<?> webDriver = localStorageAppiumDriver.get();
        if (null != webDriver && null != ((RemoteWebDriver) webDriver).getSessionId()) {
            webDriver.quit();
        }
        RunConfiguration.removeDriver(webDriver);
        localStorageAppiumDriver.set(null);
        quitServer();
    }

    public static void quitServer() {
        if (localStorageAppiumServer.get() != null && localStorageAppiumServer.get().isRunning()) {
            localStorageAppiumServer.get().stop();
            localStorageAppiumServer.set(null);
        }
        if (localStorageWebProxyProcess.get() != null) {
            localStorageWebProxyProcess.get().destroy();
            localStorageWebProxyProcess.set(null);
        }
    }

    public static AppiumDriver<?> getDriver() throws StepFailedException {
        verifyWebDriverIsOpen();
        return localStorageAppiumDriver.get();
    }

    private static void verifyWebDriverIsOpen() throws StepFailedException {
        if (localStorageAppiumDriver.get() == null) {
            throw new StepFailedException("No application is started yet.");
        }
    }

    public static void cleanup() throws InterruptedException, IOException {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("win")) {
            killProcessOnWin("adb.exe");
            killProcessOnWin("node.exe");
        } else {
            killProcessOnMac("adb");
            killProcessOnMac("node");
            killProcessOnMac("instruments");
            killProcessOnMac("deviceconsole");
            killProcessOnMac("ios_webkit_debug_proxy");
        }
    }

    private static void killProcessOnWin(String processName) throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder("taskkill", "/f", "/im", processName, "/t");
        pb.start().waitFor();
    }

    private static void killProcessOnMac(String processName) throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder("killall", processName);
        pb.start().waitFor();
    }

    public static String getDeviceId(String parentProperty) {
        return RunConfiguration.getDriverSystemProperty(parentProperty, EXECUTED_DEVICE_ID);
    }

    public static String getDeviceName(String parentProperty) {
        return RunConfiguration.getDriverSystemProperty(parentProperty, EXECUTED_DEVICE_NAME);
    }

    public static String getDeviceModel(String parentProperty) {
        return RunConfiguration.getDriverSystemProperty(parentProperty, EXECUTED_DEVICE_MODEL);
    }

    public static String getDeviceManufacturer(String parentProperty) {
        return RunConfiguration.getDriverSystemProperty(parentProperty, EXECUTED_DEVICE_MANUFACTURER);
    }

    public static String getDeviceOSVersion(String parentProperty) {
        return RunConfiguration.getDriverSystemProperty(parentProperty, EXECUTED_DEVICE_OS_VERSON);
    }

    public static String getDeviceOS(String parentProperty) {
        return RunConfiguration.getDriverSystemProperty(parentProperty, EXECUTED_DEVICE_OS);
    }

}
