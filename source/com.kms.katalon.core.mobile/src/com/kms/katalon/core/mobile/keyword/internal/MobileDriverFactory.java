package com.kms.katalon.core.mobile.keyword.internal;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;

import com.kms.katalon.core.appium.driver.AppiumDriverManager;
import com.kms.katalon.core.appium.exception.AppiumStartException;
import com.kms.katalon.core.appium.exception.IOSWebkitStartException;
import com.kms.katalon.core.appium.exception.MobileDriverInitializeException;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.driver.ExistingDriverType;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;

public class MobileDriverFactory {
    private static final String WAIT_FOR_APP_SCRIPT_TRUE = "true;";

    private static final String WAIT_FOR_APP_SCRIPT = "waitForAppScript";

    private static final String NO_RESET = "noReset";

    private static final String FULL_RESET = "fullReset";

    public static final String MOBILE_DRIVER_PROPERTY = StringConstants.CONF_PROPERTY_MOBILE_DRIVER;

    public static final String EXISTING_DRIVER_PROPERTY = StringConstants.CONF_PROPERTY_EXISTING_DRIVER;

    public static void cleanup() throws InterruptedException, IOException {
        AppiumDriverManager.cleanup();
    }

    public static MobileDriverType getMobileDriverType() {
        return MobileDriverType.valueOf(RunConfiguration.getDriverSystemProperty(MOBILE_DRIVER_PROPERTY,
                AppiumDriverManager.EXECUTED_PLATFORM));
    }

    public static String getDevicePlatform() {
        return getMobileDriverType().toString();
    }

    public static String getDeviceId() {
        return AppiumDriverManager.getDeviceId(MOBILE_DRIVER_PROPERTY);
    }

    public static String getDeviceName() {
        return AppiumDriverManager.getDeviceName(MOBILE_DRIVER_PROPERTY);
    }

    public static String getDeviceModel() {
        return AppiumDriverManager.getDeviceModel(MOBILE_DRIVER_PROPERTY);
    }

    public static String getDeviceManufacturer() {
        return AppiumDriverManager.getDeviceManufacturer(MOBILE_DRIVER_PROPERTY);
    }

    public static String getDeviceOSVersion() {
        return AppiumDriverManager.getDeviceOSVersion(MOBILE_DRIVER_PROPERTY);
    }

    public static String getDeviceOS() {
        return AppiumDriverManager.getDeviceOS(MOBILE_DRIVER_PROPERTY);
    }

    public static AppiumDriver<?> getDriver() throws StepFailedException {
        try {
            AppiumDriver<?> driver = AppiumDriverManager.getDriver();
            return driver;
        } catch (StepFailedException e) {
            if (isUsingExistingDriver()) {
                try {
                    return startExistingBrowser();
                } catch (MalformedURLException | MobileDriverInitializeException exception) {
                    // Ignore this
                }
            }
            throw e;
        }
    }

    public static void closeDriver() {
        AppiumDriverManager.closeDriver();
    }

    private static DesiredCapabilities convertPropertiesMaptoDesireCapabilities(Map<String, Object> propertyMap,
            MobileDriverType mobileDriverType) {
        DesiredCapabilities desireCapabilities = new DesiredCapabilities();
        for (Entry<String, Object> property : propertyMap.entrySet()) {
            KeywordLogger.getInstance().logInfo(MessageFormat.format(StringConstants.KW_LOG_MOBILE_PROPERTY_SETTING,
                    property.getKey(), property.getValue()));
            desireCapabilities.setCapability(property.getKey(), property.getValue());
        }
        return desireCapabilities;
    }

    private static DesiredCapabilities createCapabilities(MobileDriverType osType, String deviceId, String deviceName,
            String appFile, boolean uninstallAfterCloseApp) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        Map<String, Object> driverPreferences = RunConfiguration.getDriverPreferencesProperties(MOBILE_DRIVER_PROPERTY);
        if (driverPreferences != null && osType == MobileDriverType.IOS_DRIVER) {
            capabilities
                    .merge(convertPropertiesMaptoDesireCapabilities(driverPreferences, MobileDriverType.IOS_DRIVER));
            capabilities.setCapability(WAIT_FOR_APP_SCRIPT, WAIT_FOR_APP_SCRIPT_TRUE);
            if (deviceId == null) {
                capabilities.setCapability(MobileCapabilityType.PLATFORM, getDeviceOS());
                capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, getDeviceOSVersion());
            }
        } else if (driverPreferences != null && osType == MobileDriverType.ANDROID_DRIVER) {
            capabilities.merge(
                    convertPropertiesMaptoDesireCapabilities(driverPreferences, MobileDriverType.ANDROID_DRIVER));
            capabilities.setPlatform(Platform.ANDROID);
        }
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
        capabilities.setCapability(MobileCapabilityType.APP, appFile);
        if (deviceId != null) {
            capabilities.setCapability(MobileCapabilityType.UDID, deviceId);
        }
        capabilities.setCapability(FULL_RESET, uninstallAfterCloseApp);
        capabilities.setCapability(NO_RESET, !uninstallAfterCloseApp);
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 1800);
        return capabilities;
    }

    private static boolean isUsingExistingDriver() {
        return getExecutedDriver() instanceof ExistingDriverType;
    }

    public static AppiumDriver<?> startMobileDriver(String appFile, boolean uninstallAfterCloseApp)
            throws AppiumStartException, IOException, InterruptedException, MobileDriverInitializeException,
            IOSWebkitStartException {
        AppiumDriver<?> driver = null;
        if (isUsingExistingDriver()) {
            driver = startExistingBrowser();
        } else {
            driver = startMobileDriver(getMobileDriverType(), getDeviceId(), getDeviceName(), appFile,
                    uninstallAfterCloseApp);
            saveWebDriverSessionData(driver);
        }
        return driver;
    }

    private static void saveWebDriverSessionData(AppiumDriver<?> remoteWebDriver) {
        try (Socket myClient = new Socket(RunConfiguration.getSessionServerHost(),
                RunConfiguration.getSessionServerPort());
                PrintStream output = new PrintStream(myClient.getOutputStream())) {
            output.println(remoteWebDriver.getSessionId());
            output.println(getWebDriverServerUrl(remoteWebDriver));
            output.println(getMobileDriverType().toString());
            output.println(RunConfiguration.getLogFolderPath());
            DriverType executedDriver = getExecutedDriver();
            if (executedDriver == MobileDriverType.ANDROID_DRIVER) {
                output.println(getDeviceManufacturer() + " " + getDeviceModel() + " " + getDeviceOSVersion());
            } else if (executedDriver == MobileDriverType.IOS_DRIVER) {
                output.println(getDeviceName() + " " + getDeviceOSVersion());
            }
            output.flush();
        } catch (Exception e) {
            // Ignore for this exception
        }
    }

    public static DriverType getExecutedDriver() {
        if (RunConfiguration.getDriverSystemProperties(EXISTING_DRIVER_PROPERTY) != null) {
            return new ExistingDriverType(null);
        }
        return getMobileDriverType();
    }

    private static String getWebDriverServerUrl(AppiumDriver<?> remoteWebDriver) {
        return ((HttpCommandExecutor) remoteWebDriver.getCommandExecutor()).getAddressOfRemoteServer().toString();
    }

    protected static AppiumDriver<?> startExistingBrowser()
            throws MalformedURLException, MobileDriverInitializeException {
        return AppiumDriverManager.startExisitingMobileDriver(
                MobileDriverType.fromStringValue(RunConfiguration.getExisingSessionDriverType()),
                RunConfiguration.getExisingSessionSessionId(), RunConfiguration.getExisingSessionServerUrl());
    }

    public static AppiumDriver<?> startMobileDriver(MobileDriverType osType, String deviceId, String deviceName,
            String appFile, boolean uninstallAfterCloseApp)
            throws MobileDriverInitializeException, IOException, InterruptedException, AppiumStartException {
        return AppiumDriverManager.createMobileDriver(osType, deviceId,
                createCapabilities(osType, deviceId, deviceName, appFile, uninstallAfterCloseApp));
    }
}
