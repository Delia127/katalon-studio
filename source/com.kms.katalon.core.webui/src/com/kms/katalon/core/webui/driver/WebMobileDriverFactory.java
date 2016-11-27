package com.kms.katalon.core.webui.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.core.appium.constants.AppiumStringConstants;
import com.kms.katalon.core.appium.driver.AppiumDriverManager;
import com.kms.katalon.core.appium.exception.AppiumStartException;
import com.kms.katalon.core.appium.exception.MobileDriverInitializeException;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.webui.constants.StringConstants;

public class WebMobileDriverFactory {
    private static final String CHROME = "Chrome";

    private static final String SAFARI = "Safari";

    public static final String MOBILE_DRIVER_PROPERTY = StringConstants.CONF_PROPERTY_MOBILE_DRIVER;

    public static final String EXECUTED_MOBILE_PLATFORM = AppiumStringConstants.CONF_EXECUTED_PLATFORM;

    public static final String EXECUTED_MOBILE_DEVICE_ID = AppiumStringConstants.CONF_EXECUTED_DEVICE_ID;

    public static void cleanup() throws InterruptedException, IOException {
        AppiumDriverManager.cleanup();
    }

    public static AppiumDriver<?> getDriver() throws StepFailedException {
        return AppiumDriverManager.getDriver();
    }

    public static void closeDriver() {
        AppiumDriverManager.closeDriver();
    }

    private static DesiredCapabilities toDesireCapabilities(Map<String, Object> propertyMap,
            WebUIDriverType WebUIDriverType) {
        DesiredCapabilities desireCapabilities = new DesiredCapabilities();
        for (Entry<String, Object> property : propertyMap.entrySet()) {
            KeywordLogger.getInstance().logInfo(
                    MessageFormat.format(StringConstants.KW_LOG_WEB_UI_PROPERTY_SETTING, property.getKey(),
                            property.getValue()));
            desireCapabilities.setCapability(property.getKey(), property.getValue());
        }
        return desireCapabilities;
    }

    private static DesiredCapabilities createCapabilities(WebUIDriverType osType) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        Map<String, Object> driverPreferences = RunConfiguration.getDriverPreferencesProperties(MOBILE_DRIVER_PROPERTY);
        String deviceId = getDeviceId();
        if (driverPreferences != null && osType == WebUIDriverType.IOS_DRIVER) {
            capabilities.merge(toDesireCapabilities(driverPreferences, WebUIDriverType.IOS_DRIVER));
            capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, SAFARI);
            if (deviceId == null) {
                capabilities.setCapability(MobileCapabilityType.PLATFORM, getDeviceOS());
                capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, getDeviceOSVersion());
            }
        } else if (driverPreferences != null && osType == WebUIDriverType.ANDROID_DRIVER) {
            capabilities.merge(toDesireCapabilities(driverPreferences, WebUIDriverType.ANDROID_DRIVER));
            capabilities.setPlatform(Platform.ANDROID);
            capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, CHROME);
        }
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, getDeviceName());
        if (deviceId != null) {
            capabilities.setCapability(MobileCapabilityType.UDID, deviceId);
        }
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 1800);
        return capabilities;
    }
    
    public static AppiumDriver<?> startExisitingMobileDriver(DriverType driverType, String sessionId,
            String remoteServerUrl) throws MalformedURLException, MobileDriverInitializeException {
        return AppiumDriverManager.startExisitingMobileDriver(driverType, sessionId, remoteServerUrl);
    }

    public static AppiumDriver<?> createMobileDriver(WebUIDriverType osType)
            throws MobileDriverInitializeException, IOException, InterruptedException, AppiumStartException {
        return AppiumDriverManager.createMobileDriver(osType, getDeviceId(), createCapabilities(osType));
    }

    public static WebUIDriverType getWebMobileDriverType() {
        return WebUIDriverType.valueOf(RunConfiguration.getDriverSystemProperty(DriverFactory.WEB_UI_DRIVER_PROPERTY,
                AppiumDriverManager.EXECUTED_PLATFORM));
    }

    public static String getDevicePlatform() {
        return getWebMobileDriverType().toString();
    }

    public static String getDeviceId() {
        return AppiumDriverManager.getDeviceId(DriverFactory.MOBILE_DRIVER_PROPERTY);
    }

    public static String getDeviceName() {
        return AppiumDriverManager.getDeviceName(DriverFactory.MOBILE_DRIVER_PROPERTY);
    }

    public static String getDeviceModel() {
        return AppiumDriverManager.getDeviceModel(DriverFactory.MOBILE_DRIVER_PROPERTY);
    }

    public static String getDeviceManufacturer() {
        return AppiumDriverManager.getDeviceManufacturer(DriverFactory.MOBILE_DRIVER_PROPERTY);
    }

    public static String getDeviceOSVersion() {
        return AppiumDriverManager.getDeviceOSVersion(DriverFactory.MOBILE_DRIVER_PROPERTY);
    }

    public static String getDeviceOS() {
        return AppiumDriverManager.getDeviceOS(DriverFactory.MOBILE_DRIVER_PROPERTY);
    }
}
