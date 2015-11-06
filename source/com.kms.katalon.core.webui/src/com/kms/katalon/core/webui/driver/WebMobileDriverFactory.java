package com.kms.katalon.core.webui.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.UnreachableBrowserException;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.KeywordLogger;

class WebMobileDriverFactory {
    private static final String APPIUM_SERVER_URL_SUFFIX = "/wd/hub";
    private static final String APPIUM_SERVER_URL_PREFIX = "http://127.0.0.1:";
    private int appiumPort;
    private Process appiumServer;
    private Process webProxyServer;

    private static final ThreadLocal<WebMobileDriverFactory> localWebMobileDriverFactoryStorage = new ThreadLocal<WebMobileDriverFactory>() {
        @Override
        protected WebMobileDriverFactory initialValue() {
            return new WebMobileDriverFactory();
        }
    };

    private WebMobileDriverFactory() {
    }

    static WebMobileDriverFactory getInstance() {
        return localWebMobileDriverFactoryStorage.get();
    }
    
    private void cleanup() {
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

    private void killProcessOnWin(String processName) {
        ProcessBuilder pb = new ProcessBuilder("taskkill", "/f", "/im", processName, "/t");
        try {
            pb.start().waitFor();
        } catch (Exception e) {
            // LOGGER.error(e.getMessage(), e);
        }
    }

    private void killProcessOnMac(String processName) {
        ProcessBuilder pb = new ProcessBuilder("killall", processName);
        try {
            pb.start().waitFor();
        } catch (Exception e) {
            // LOGGER.error(e.getMessage(), e);
        }
    }

    AppiumDriver<?> getAndroidDriver(String deviceName) throws Exception {
        if (!isServerStarted()) {
            startAppiumServer();
        }
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setPlatform(Platform.ANDROID);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
        capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
        capabilities.setCapability(MobileCapabilityType.UDID, deviceName);
        int time = 0;
        long currentMilis = System.currentTimeMillis();
        AppiumDriver<?> appiumDriver = null;
        while (time < RunConfiguration.getTimeOut()) {
            try {
                appiumDriver = new SwipeableAndroidDriver(new URL(APPIUM_SERVER_URL_PREFIX + appiumPort
                        + APPIUM_SERVER_URL_SUFFIX), capabilities);
                return appiumDriver;
            } catch (UnreachableBrowserException e) {
                long newMilis = System.currentTimeMillis();
                time += ((newMilis - currentMilis) / 1000);
                currentMilis = newMilis;
                continue;
            }
        }
        throw new StepFailedException("Could not connect to appium server after " + RunConfiguration.getTimeOut()
                + " seconds");
    }

    @SuppressWarnings("rawtypes")
    AppiumDriver<?> getIosDriver(String deviceName) throws Exception {
        cleanup();
        if (!isWebProxyServerStarted()) {
            startWebProxyServer(deviceName);
        }
        if (!isServerStarted()) {
            startAppiumServer();
        }
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
        capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Safari");
        capabilities.setCapability(MobileCapabilityType.UDID, deviceName);
        capabilities.setCapability("autoAcceptAlerts", true);
        capabilities.setCapability("waitForAppScript", true);
        int time = 0;
        long currentMilis = System.currentTimeMillis();
        AppiumDriver<?> appiumDriver = null;
        while (time < RunConfiguration.getTimeOut()) {
            try {
                appiumDriver = new IOSDriver(new URL(APPIUM_SERVER_URL_PREFIX + appiumPort + APPIUM_SERVER_URL_SUFFIX),
                        capabilities);
                return appiumDriver;
            } catch (UnreachableBrowserException e) {
                long newMilis = System.currentTimeMillis();
                time += ((newMilis - currentMilis) / 1000);
                currentMilis = newMilis;
                continue;
            }
        }
        throw new StepFailedException("Could not connect to appium server after " + RunConfiguration.getTimeOut()
                + " seconds");
    }

    private boolean isServerStarted() {
        if (appiumServer == null) {
            return false;
        } else {
            try {
                appiumServer.exitValue();
                return false;
            } catch (Exception e) {
                // LOGGER.warn(e.getMessage(), e);
                try {
                    String logContent = FileUtils.readFileToString(new File(new File(RunConfiguration.getLogFilePath())
                            .getParent() + File.separator + "appium.log"));
                    if (logContent.contains("Console LogLevel: debug")) {
                        return true;
                    }
                } catch (Exception e1) {
                    // LOGGER.warn(e1.getMessage(), e1);
                }
            }
        }
        return false;
    }

    private boolean isWebProxyServerStarted() {
        if (webProxyServer == null) {
            return false;
        } else {
            try {
                webProxyServer.exitValue();
                return false;
            } catch (IllegalThreadStateException e) {
                return true;
            }
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

    private void startAppiumServer() throws Exception {
        String appium = System.getenv("APPIUM_HOME") + "/bin/appium.js";
        String appiumTemp = System.getProperty("user.home") + File.separator + "Appium_Temp"
                + System.currentTimeMillis();
        appiumPort = getFreePort();
        String[] cmd = { "node", appium, "--command-timeout", "3600", "--tmp", appiumTemp, "-p",
                String.valueOf(appiumPort), "--chromedriver-port", String.valueOf(getFreePort())};
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectOutput(new File(new File(RunConfiguration.getLogFilePath()).getParent() + File.separator
                + "appium.log"));
        appiumServer = pb.start();
        while (!isServerStarted()) {
        }
        KeywordLogger.getInstance().logInfo("Appium server started on port " + appiumPort);
    }

    private void startWebProxyServer(String deviceId) throws Exception {
        String webProxyServerLocation = "ios_webkit_debug_proxy";
        int webProxyPort = 27753;
        String[] webProxyServerCmd = { webProxyServerLocation, "-c", deviceId + ":" + webProxyPort };
        ProcessBuilder webProxyServerProcessBuilder = new ProcessBuilder(webProxyServerCmd);
        webProxyServerProcessBuilder.redirectOutput(new File(new File(RunConfiguration.getLogFilePath()).getParent()
                + File.separator + "appium-proxy-server.log"));
        webProxyServer = webProxyServerProcessBuilder.start();
        while (!isWebProxyServerStarted()) {
        }
        KeywordLogger.getInstance().logInfo("ios_webkit_debug_proxy server started on port " + webProxyPort);
    }

    void quitServer() {
        if (appiumServer != null) {
            appiumServer.destroy();
            appiumServer = null;
        }
        if (webProxyServer != null) {
            webProxyServer.destroy();
            webProxyServer = null;
        }
    }
}
