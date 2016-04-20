package com.kms.katalon.core.mobile.keyword;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.net.UrlChecker.TimeoutException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.exception.AppiumStartException;
import com.kms.katalon.core.mobile.exception.IOSWebkitStartException;
import com.kms.katalon.core.mobile.exception.MobileDriverInitializeException;
import com.kms.katalon.core.mobile.util.MobileDriverPropertyUtil;

public class MobileDriverFactory {

    private static final String APPIUM_SERVER_URL_SUFFIX = "/wd/hub";

    private static final String APPIUM_SERVER_URL_PREFIX = "http://127.0.0.1:";

    public static final String MOBILE_DRIVER_PROPERTY = StringConstants.CONF_PROPERTY_MOBILE_DRIVER;

    public static final String EXECUTED_PLATFORM = StringConstants.CONF_EXECUTED_PLATFORM;

    public static final String EXECUTED_DEVICE_ID = StringConstants.CONF_EXECUTED_DEVICE_ID;

    public static final String APPIUM_LOG_PROPERTY = StringConstants.CONF_APPIUM_LOG_FILE;

    private static Process webProxyServer;

    private static final int WEB_PROXY_PORT = 27753;

    private static final String APPIUM_LOG_FILE_NAME = "appium-proxy-server.log";

    private static final String MSG_START_IOS_WEBKIT_SUCCESS = "ios_webkit_debug_proxy server started on port " + WEB_PROXY_PORT;

    private static final String LOCALHOST_PREFIX = "http://localhost:";

    public enum OsType {
        IOS, ANDROID
    }

    private static final ThreadLocal<Integer> localStorageAppiumPort = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    private static final ThreadLocal<Process> localStorageAppiumServer = new ThreadLocal<Process>() {
        @Override
        protected Process initialValue() {
            return null;
        }
    };

    private static final ThreadLocal<AppiumDriver<?>> localStorageAppiumDriver = new ThreadLocal<AppiumDriver<?>>() {
        @Override
        protected AppiumDriver<?> initialValue() {
            return null;
        }
    };

    private static final ThreadLocal<Map<String, String>> localStorageIosDevices = new ThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            try {
                return getIosDevices();
            } catch (Exception e) {
                KeywordLogger.getInstance().logWarning(e.getMessage());
            }
            return new LinkedHashMap<String, String>();
        }
    };

    private static final ThreadLocal<Map<String, String>> localStorageAndroidDevices = new ThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            try {
                return getAndroidDevices();
            } catch (Exception e) {
                KeywordLogger.getInstance().logWarning(e.getMessage());
            }
            return new LinkedHashMap<String, String>();
        }
    };

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

    private static void ensureWebProxyServerStarted(String deviceId) throws IOException, InterruptedException, IOSWebkitStartException {
        if (!isWebProxyServerStarted(1)) {
            startWebProxyServer(deviceId);
        }
    }

	public static void startMobileDriver(OsType osType, String deviceId,
			String appFile, boolean uninstallAfterCloseApp)
			throws AppiumStartException, IOException, InterruptedException, MobileDriverInitializeException, IOSWebkitStartException
             {
        ensureServicesStarted(osType, deviceId);
        createMobileDriver(osType, deviceId, appFile, uninstallAfterCloseApp);
    }

    private static void ensureServicesStarted(OsType osType, String deviceId) throws IOException, InterruptedException, IOSWebkitStartException, AppiumStartException {
        if (osType == OsType.IOS) {
            // Proxy server is optional
            ensureWebProxyServerStarted(deviceId);
        }
        // Appium server started already?
        if (isAppiumServerStarted(1)) {
            return;
        }
        // If not, start it
        startAppiumServer();
        if (isAppiumServerStarted(RunConfiguration.getTimeOut())) {
            KeywordLogger.getInstance().logInfo("Appium server started on port " + localStorageAppiumPort.get());
            return;
        }
        throw new AppiumStartException("Fail to start Appium server in " + RunConfiguration.getTimeOut() + " seconds");
    }

    private static boolean isAppiumServerStarted(int timeToWait) {
        if (localStorageAppiumServer.get() == null) {
            return false;
        }
        try {
            // Detect if the process still alive?
            localStorageAppiumServer.get().exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            // The process is still alive, continue to ping it's HTTP end-point
        }
        try {
            return isServerStarted(timeToWait, new URL(APPIUM_SERVER_URL_PREFIX + localStorageAppiumPort.get()
                    + APPIUM_SERVER_URL_SUFFIX + "/status"));
        } catch (MalformedURLException mex) {
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

    private static boolean isWebProxyServerStarted(int timeOut) {
        if (webProxyServer == null) {
            return false;
        }
        try {
            webProxyServer.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {}
        try {
            return isServerStarted(timeOut, new URL("http://localhost:" + WEB_PROXY_PORT));
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private static synchronized int getFreePort() {
        ServerSocket s = null;
        try {
            s = new ServerSocket(0);
            return s.getLocalPort();
        } catch (IOException e) {
            // do nothing
        } finally {
            try {
                s.close();
            } catch (IOException e) {
                // do nothing
            }
        }
        return -1;
    }

    private static void startAppiumServer() throws AppiumStartException, IOException {
        String appiumHome = System.getenv("APPIUM_HOME");
        if (appiumHome == null) {
            throw new AppiumStartException("APPIUM_HOME environment variable is not set");
        }
        String appium = appiumHome + "/bin/appium.js";
        String appiumTemp = System.getProperty("java.io.tmpdir") + File.separator + "Katalon" + File.separator
                + "Appium" + File.separator + "Temp" + System.currentTimeMillis();
        localStorageAppiumPort.set(getFreePort());
        String[] cmd = { "node", appium, "--command-timeout", "3600", "--tmp", appiumTemp, "-p",
                String.valueOf(localStorageAppiumPort.get()), "--chromedriver-port", String.valueOf(getFreePort()) };
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectOutput(new File(RunConfiguration.getAppiumLogFilePath()));
        localStorageAppiumServer.set(pb.start());
    }

    private static String getIosWebkitPath() throws IOException, InterruptedException {
        String iOSWebkitPath = System.getenv("IOS_WEBKIT_HOME");
        if (iOSWebkitPath != null) {
            return iOSWebkitPath += "/bin/ios_webkit_debug_proxy";
        }
        String[] cmd = new String[] { "which", "ios_webkit_debug_proxy" };
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();
        process.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        iOSWebkitPath = reader.readLine();
        reader.close();
        return iOSWebkitPath;
    }

    /**
     * Start proxy server, this server is optional
     * 
     * @param deviceId
     * @throws Exception
     */
    private static void startWebProxyServer(String deviceId) throws IOException, InterruptedException, IOSWebkitStartException {
        String webProxyServerLocation = getIosWebkitPath();
        if (StringUtils.isBlank(webProxyServerLocation)) {
            return;
        }
        String[] webProxyServerCmd = { webProxyServerLocation, "-c", deviceId + ":" + WEB_PROXY_PORT };
        ProcessBuilder webProxyServerProcessBuilder = new ProcessBuilder(webProxyServerCmd);
        webProxyServerProcessBuilder.redirectOutput(new File(
                new File(RunConfiguration.getAppiumLogFilePath()).getParent() + File.separator + APPIUM_LOG_FILE_NAME));

        webProxyServerProcessBuilder.start();

        // Check again if proxy server started
        if (!isServerStarted(10, new URL(LOCALHOST_PREFIX + WEB_PROXY_PORT))) {
            throw new IOSWebkitStartException();
        }
        KeywordLogger.getInstance().logInfo(MSG_START_IOS_WEBKIT_SUCCESS);
    }

    public static void quitServer() {
        if (localStorageAppiumServer.get() != null) {
            localStorageAppiumServer.get().destroy();
            localStorageAppiumServer.set(null);
        }
        if (webProxyServer != null) {
            webProxyServer.destroy();
            webProxyServer = null;
        }
    }

    public static List<String> getDevices() throws Exception {
        List<String> devices = new ArrayList<String>();
        devices.addAll(getAndroidDevices().values());
        devices.addAll(getIosDevices().values());
        return devices;
    }

    public static Map<String, String> getIosDevices() throws Exception {
        Map<String, String> iosDevices = new LinkedHashMap<String, String>();
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            List<String> deviceIds = new ArrayList<String>();
            String[] cmd = { "idevice_id", "-l" };
            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process p = pb.start();
            p.waitFor();
            String line = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = br.readLine()) != null) {
                deviceIds.add(line);
            }

            for (String deviceId : deviceIds) {
                cmd = new String[] { "ideviceinfo", "-u", deviceId };
                pb.command(cmd);
                p = pb.start();
                p.waitFor();
                br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String deviceInfo = "";
                while ((line = br.readLine()) != null) {
                    if (line.contains("DeviceClass:")) {
                        deviceInfo = line.substring("DeviceClass:".length(), line.length()).trim();
                        continue;
                    }
                    if (line.contains("DeviceName:")) {
                        deviceInfo += " " + line.substring("DeviceName:".length(), line.length()).trim();
                        continue;
                    }
                    if (line.contains("ProductVersion:")) {
                        deviceInfo += " " + line.substring("ProductVersion:".length(), line.length()).trim();
                        continue;
                    }
                }

                iosDevices.put(deviceId, deviceInfo);
            }
        }
        return iosDevices;
    }

    public static Map<String, String> getAndroidDevices() throws Exception {
        Map<String, String> androidDevices = new LinkedHashMap<String, String>();

        String adbPath = System.getenv("ANDROID_HOME");
        if (adbPath != null) {
            List<String> deviceIds = new ArrayList<String>();
            adbPath += File.separator + "platform-tools" + File.separator + "adb";
            String[] cmd = new String[] { adbPath, "devices" };
            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process process = pb.start();
            process.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line, deviceId, deviceName;
            while ((line = br.readLine()) != null) {
                if (!line.toLowerCase().trim().contains("list of devices")) {
                    if (line.toLowerCase().trim().contains("device")) {
                        deviceId = line.split("\\s")[0];
                        deviceIds.add(deviceId);
                    }
                }
            }
            br.close();

            for (String id : deviceIds) {
                cmd = new String[] { adbPath, "-s", id, "shell", "getprop", "ro.product.manufacturer" };
                pb.command(cmd);
                process = pb.start();
                process.waitFor();
                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                deviceName = br.readLine();
                br.close();

                cmd = new String[] { adbPath, "-s", id, "shell", "getprop", "ro.product.model" };
                pb.command(cmd);
                process = pb.start();
                process.waitFor();
                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                deviceName += " " + br.readLine();
                br.close();

                cmd = new String[] { adbPath, "-s", id, "shell", "getprop", "ro.build.version.release" };
                pb.command(cmd);
                process = pb.start();
                process.waitFor();
                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                deviceName += " " + br.readLine();
                br.close();

                androidDevices.put(id, deviceName);
            }
        }
        return androidDevices;
    }

    public static String getDeviceId(String deviceName) {
        for (Map.Entry<String, String> entry : localStorageAndroidDevices.get().entrySet()) {
            if (entry.getValue().equalsIgnoreCase(deviceName) || entry.getKey().equalsIgnoreCase(deviceName)) {
                return entry.getKey();
            }
        }

        for (Map.Entry<String, String> entry : localStorageIosDevices.get().entrySet()) {
            if (entry.getValue().equalsIgnoreCase(deviceName) || entry.getKey().equalsIgnoreCase(deviceName)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public static OsType getDeviceOs(String deviceId) throws Exception {
        if (localStorageAndroidDevices.get().containsKey(deviceId)) {
            return OsType.ANDROID;
        } else if (localStorageIosDevices.get().containsKey(deviceId)) {
            return OsType.IOS;
        } else {
            return null;
        }
    }

    public static String getDevicePlatform() {
        return RunConfiguration.getDriverSystemProperty(StringConstants.CONF_PROPERTY_MOBILE_DRIVER, EXECUTED_PLATFORM);
    }

    public static String getDeviceName() {
        return RunConfiguration.getStringProperty(EXECUTED_DEVICE_ID);
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

    public static void closeDriver() {
        AppiumDriver<?> webDriver = localStorageAppiumDriver.get();
        if (null != webDriver && null != ((RemoteWebDriver) webDriver).getSessionId()) {
            webDriver.quit();
        }
        RunConfiguration.removeDriver(webDriver);
        localStorageAppiumDriver.set(null);
    }

    private static DesiredCapabilities createCapabilities(OsType osType, String deviceId, String appFile,
            boolean uninstallAfterCloseApp) {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        Map<String, Object> driverPreferences = RunConfiguration.getDriverPreferencesProperties(MOBILE_DRIVER_PROPERTY);

        if (driverPreferences != null && osType == OsType.IOS) {
            capabilities.merge(MobileDriverPropertyUtil.toDesireCapabilities(driverPreferences,
                    MobileDriverType.IOS_DRIVER));
            capabilities.setCapability("waitForAppScript", true);
        } else if (driverPreferences != null && osType == OsType.ANDROID) {
            capabilities.merge(MobileDriverPropertyUtil.toDesireCapabilities(driverPreferences,
                    MobileDriverType.ANDROID_DRIVER));
            capabilities.setPlatform(Platform.ANDROID);
        }

        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceId);
        capabilities.setCapability(MobileCapabilityType.APP, appFile);
        capabilities.setCapability("udid", deviceId);
        capabilities.setCapability("fullReset", uninstallAfterCloseApp);
        capabilities.setCapability("noReset", !uninstallAfterCloseApp);
        capabilities.setCapability("newCommandTimeout", 1800);
        // capabilities.setCapability("waitForAppScript", true);
        return capabilities;
    }

    @SuppressWarnings("rawtypes")
    private static void createMobileDriver(OsType osType, String deviceId, String appFile,
            boolean uninstallAfterCloseApp) throws MobileDriverInitializeException, MalformedURLException {
        DesiredCapabilities capabilities = createCapabilities(osType, deviceId, appFile, uninstallAfterCloseApp);
        int time = 0;
        long currentMilis = System.currentTimeMillis();
        AppiumDriver<?> appiumDriver = null;
        URL url = new URL(APPIUM_SERVER_URL_PREFIX + localStorageAppiumPort.get() + APPIUM_SERVER_URL_SUFFIX);
        while (time < RunConfiguration.getTimeOut()) {
            try {
                if (osType == OsType.IOS) {
                    appiumDriver = new IOSDriver(url, capabilities);
                } else if (osType == OsType.ANDROID) {
                    appiumDriver = new SwipeableAndroidDriver(url, capabilities);
                }
                localStorageAppiumDriver.set(appiumDriver);
                return;
            } catch (UnreachableBrowserException e) {
                long newMilis = System.currentTimeMillis();
                time += ((newMilis - currentMilis) / 1000);
                currentMilis = newMilis;
                continue;
            }
        }
        throw new MobileDriverInitializeException("Could not connect to appium server after "
                + RunConfiguration.getTimeOut() + " seconds");
    }
}
