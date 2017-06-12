package com.kms.katalon.integration.kobiton.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.openqa.selenium.ScreenOrientation;

import com.kms.katalon.core.appium.constants.AppiumStringConstants;
import com.kms.katalon.core.appium.driver.AppiumDriverManager;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.integration.kobiton.entity.KobitonDeviceCapabilities.Browser;

public class KobitonDevice {
    private static final String CAPABILITIES_SESSION_DESCRIPTION = "sessionDescription";

    private static final String CAPABILITIES_SESSION_NAME = "sessionName";

    private static final String CAPABILITIES_ACCEPT_SSL_CERTS = "acceptSslCerts";

    private static final String CAPABILITIES_CAPTURE_SREEN_SHOTS = "captureSreenShots";

    private static final String CAPABILITIES_DEVICE_ORIENTATION = "deviceOrientation";

    private static final String CAPABILITIES_PLATFORM_VERSION = "platformVersion";

    private static final String CAPABILITIES_BROWSER_NAME = "browserName";

    private static final String CAPABILITIES_DEVICE_NAME = "deviceName";

    private static final String CAPABILITIES_PLATFORM_NAME = "platformName";

    private static final String BROWSER_CHROME = "chrome";

    private static final String BROWSER_SAFARI = "safari";

    private static final String PLATFORM_NAME_ANDROID = "Android";

    private static final String PLATFORM_NAME_IOS = "iOS";

    private int id;

    private String udid;

    private boolean isBooked;

    private KobitonDeviceCapabilities capabilities;

    private ScreenOrientation orientation = ScreenOrientation.PORTRAIT;

    private boolean captureSreenShots = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }

    public KobitonDeviceCapabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(KobitonDeviceCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    public ScreenOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(ScreenOrientation orientation) {
        this.orientation = orientation;
    }

    public boolean isCaptureSreenShots() {
        return captureSreenShots;
    }

    public void setCaptureSreenShots(boolean captureSreenShots) {
        this.captureSreenShots = captureSreenShots;
    }

    private String getBrowserName() {
        Browser[] installedBrowsers = capabilities.getInstalledBrowsers();
        if (installedBrowsers == null || installedBrowsers.length == 0) {
            if (capabilities.getPlatformName().equals(PLATFORM_NAME_IOS)) {
                return BROWSER_SAFARI;
            }
            if (capabilities.getPlatformName().equals(PLATFORM_NAME_ANDROID)) {
                return BROWSER_CHROME;
            }
        }
        return installedBrowsers[0].getName();
    }

    public Map<String, Object> toDesireCapabilitiesMap() {
        Map<String, Object> desireCapabilitiesMap = new HashMap<>();
        desireCapabilitiesMap.put(CAPABILITIES_PLATFORM_NAME, capabilities.getPlatformName());
        desireCapabilitiesMap.put(CAPABILITIES_DEVICE_NAME, capabilities.getDeviceName());
        desireCapabilitiesMap.put(CAPABILITIES_BROWSER_NAME, getBrowserName());
        desireCapabilitiesMap.put(CAPABILITIES_PLATFORM_VERSION, capabilities.getPlatformVersion());
        desireCapabilitiesMap.put(CAPABILITIES_DEVICE_ORIENTATION, orientation.value());
        desireCapabilitiesMap.put(CAPABILITIES_CAPTURE_SREEN_SHOTS, true);
        desireCapabilitiesMap.put(CAPABILITIES_ACCEPT_SSL_CERTS, true);
        desireCapabilitiesMap.put(CAPABILITIES_SESSION_NAME, "Automation test session");
        desireCapabilitiesMap.put(CAPABILITIES_SESSION_DESCRIPTION, "");
        return desireCapabilitiesMap;
    }
    
    public Map<String, Object> getSystemPropertiesMap() {
        Map<String, Object> systemProperties = new HashMap<>();
        systemProperties.put(AppiumStringConstants.CONF_EXECUTED_DEVICE_NAME, capabilities.getDeviceName());
        systemProperties.put(AppiumStringConstants.CONF_EXECUTED_DEVICE_OS, capabilities.getPlatformName());
        systemProperties.put(AppiumStringConstants.CONF_EXECUTED_DEVICE_OS_VERSON, capabilities.getPlatformVersion());
        if (capabilities.getPlatformName().equals(PLATFORM_NAME_IOS)) {
            systemProperties.put(AppiumDriverManager.EXECUTED_PLATFORM,
                    MobileDriverType.IOS_DRIVER.getPropertyValue());
        }
        if (capabilities.getPlatformName().equals(PLATFORM_NAME_ANDROID)) {
            systemProperties.put(AppiumDriverManager.EXECUTED_PLATFORM,
                    MobileDriverType.ANDROID_DRIVER.getPropertyValue());
        }
        return systemProperties;
    }

    @Override
    public String toString() {
        return "KobitonDevice [id=" + id + ", uuid=" + udid + ", isBooked=" + isBooked + ", capabilities="
                + capabilities + ", orientation=" + orientation + ", captureSreenShots=" + captureSreenShots + "]";
    }

    public String getDisplayString() {
        return (WordUtils.capitalize(StringUtils.defaultString(capabilities.getBrandName())) + " "
                + capabilities.getDeviceName() + " " + capabilities.getPlatformVersion()).trim();
    }
}
