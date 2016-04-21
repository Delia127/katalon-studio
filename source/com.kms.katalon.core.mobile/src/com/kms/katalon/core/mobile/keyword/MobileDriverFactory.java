package com.kms.katalon.core.mobile.keyword;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.core.appium.driver.AppiumDriverManager;
import com.kms.katalon.core.appium.exception.AppiumStartException;
import com.kms.katalon.core.appium.exception.IOSWebkitStartException;
import com.kms.katalon.core.appium.exception.MobileDriverInitializeException;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class MobileDriverFactory {
    private static final String WAIT_FOR_APP_SCRIPT = "waitForAppScript";

    private static final String NO_RESET = "noReset";

    private static final String FULL_RESET = "fullReset";

    public static final String MOBILE_DRIVER_PROPERTY = StringConstants.CONF_PROPERTY_MOBILE_DRIVER;

    public static final String EXECUTED_PLATFORM = StringConstants.CONF_EXECUTED_PLATFORM;

    public static final String EXECUTED_DEVICE_ID = StringConstants.CONF_EXECUTED_DEVICE_ID;

    public static final String EXECUTED_DEVICE_MANUFACTURER = StringConstants.CONF_EXECUTED_DEVICE_MANUFACTURER;

    public static final String EXECUTED_DEVICE_MODEL = StringConstants.CONF_EXECUTED_DEVICE_MODEL;

    public static final String EXECUTED_DEVICE_NAME = StringConstants.CONF_EXECUTED_DEVICE_NAME;

    public static final String EXECUTED_DEVICE_OS = StringConstants.CONF_EXECUTED_DEVICE_OS;

    public static final String EXECUTED_DEVICE_OS_VERSON = StringConstants.CONF_EXECUTED_DEVICE_OS_VERSON;

    public static void cleanup() throws InterruptedException, IOException {
        AppiumDriverManager.cleanup();
    }

    public static MobileDriverType getMobileDriverType() {
        return MobileDriverType.valueOf(RunConfiguration.getDriverSystemProperty(MOBILE_DRIVER_PROPERTY,
                EXECUTED_PLATFORM));
    }

    public static String getDevicePlatform() {
        return getMobileDriverType().toString();
    }

    public static String getDeviceId() {
        return RunConfiguration.getDriverSystemProperty(MOBILE_DRIVER_PROPERTY, EXECUTED_DEVICE_ID);
    }

    public static String getDeviceName() {
        return RunConfiguration.getDriverSystemProperty(MOBILE_DRIVER_PROPERTY, EXECUTED_DEVICE_NAME);
    }

    public static String getDeviceModel() {
        return RunConfiguration.getDriverSystemProperty(MOBILE_DRIVER_PROPERTY, EXECUTED_DEVICE_MODEL);
    }

    public static String getDeviceManufacturer() {
        return RunConfiguration.getDriverSystemProperty(MOBILE_DRIVER_PROPERTY, EXECUTED_DEVICE_MANUFACTURER);
    }

    public static String getDeviceOSVersion() {
        return RunConfiguration.getDriverSystemProperty(MOBILE_DRIVER_PROPERTY, EXECUTED_DEVICE_OS_VERSON);
    }

    public static String getDeviceOS() {
        return RunConfiguration.getDriverSystemProperty(MOBILE_DRIVER_PROPERTY, EXECUTED_DEVICE_OS);
    }

    public static AppiumDriver<?> getDriver() throws StepFailedException {
        return AppiumDriverManager.getDriver();
    }

    public static void closeDriver() {
        AppiumDriverManager.closeDriver();
    }

    private static DesiredCapabilities convertPropertiesMaptoDesireCapabilities(Map<String, Object> propertyMap,
            MobileDriverType mobileDriverType) {
        DesiredCapabilities desireCapabilities = new DesiredCapabilities();
        for (Entry<String, Object> property : propertyMap.entrySet()) {
            KeywordLogger.getInstance().logInfo(
                    MessageFormat.format(StringConstants.KW_LOG_MOBILE_PROPERTY_SETTING, property.getKey(),
                            property.getValue()));
            desireCapabilities.setCapability(property.getKey(), property.getValue());
        }
        return desireCapabilities;
    }

    private static DesiredCapabilities createCapabilities(MobileDriverType osType, String deviceId, String appFile,
            boolean uninstallAfterCloseApp) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        Map<String, Object> driverPreferences = RunConfiguration.getDriverPreferencesProperties(MOBILE_DRIVER_PROPERTY);
        if (driverPreferences != null && osType == MobileDriverType.IOS_DRIVER) {
            capabilities.merge(convertPropertiesMaptoDesireCapabilities(driverPreferences, MobileDriverType.IOS_DRIVER));
            capabilities.setCapability(WAIT_FOR_APP_SCRIPT, true);
        } else if (driverPreferences != null && osType == MobileDriverType.ANDROID_DRIVER) {
            capabilities.merge(convertPropertiesMaptoDesireCapabilities(driverPreferences,
                    MobileDriverType.ANDROID_DRIVER));
            capabilities.setPlatform(Platform.ANDROID);
        }

        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceId);
        capabilities.setCapability(MobileCapabilityType.APP, appFile);
        capabilities.setCapability(MobileCapabilityType.UDID, deviceId);
        capabilities.setCapability(FULL_RESET, uninstallAfterCloseApp);
        capabilities.setCapability(NO_RESET, !uninstallAfterCloseApp);
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 1800);
        return capabilities;
    }

    public static AppiumDriver<?> startMobileDriver(String appFile, boolean uninstallAfterCloseApp)
            throws AppiumStartException, IOException, InterruptedException, MobileDriverInitializeException,
            IOSWebkitStartException {
        return startMobileDriver(getMobileDriverType(), getDeviceId(), appFile, uninstallAfterCloseApp);
    }

    public static AppiumDriver<?> startMobileDriver(MobileDriverType osType, String deviceId, String appFile,
            boolean uninstallAfterCloseApp) throws MobileDriverInitializeException, IOException, InterruptedException,
            AppiumStartException {
        return AppiumDriverManager.createMobileDriver(osType, deviceId,
                createCapabilities(osType, deviceId, appFile, uninstallAfterCloseApp));
    }
}
