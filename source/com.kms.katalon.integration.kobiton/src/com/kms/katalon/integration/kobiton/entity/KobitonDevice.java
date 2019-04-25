package com.kms.katalon.integration.kobiton.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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

    public static final String PLATFORM_NAME_ANDROID = "Android";

    public static final String PLATFORM_NAME_IOS = "iOS";

    private int id;

    private String udid;

    private boolean isBooked;

    private KobitonDeviceCapabilities capabilities;

    private ScreenOrientation orientation = ScreenOrientation.PORTRAIT;

    private boolean captureScreenShots = true;

    private boolean isHidden;

    private boolean isOnline;

    private boolean isFavorite;

    private boolean isCloud;

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

    public boolean isCaptureScreenShots() {
        return captureScreenShots;
    }

    public void setCaptureScreenShots(boolean captureScreenShots) {
        this.captureScreenShots = captureScreenShots;
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
            systemProperties.put(AppiumDriverManager.EXECUTED_PLATFORM, MobileDriverType.IOS_DRIVER.getPropertyValue());
        }
        if (capabilities.getPlatformName().equals(PLATFORM_NAME_ANDROID)) {
            systemProperties.put(AppiumDriverManager.EXECUTED_PLATFORM,
                    MobileDriverType.ANDROID_DRIVER.getPropertyValue());
        }
        return systemProperties;
    }

    @Override
    public String toString() {
        return "KobitonDevice [id=" + id + ", uuid=" + udid + ", isBooked=" + isBooked + ", isHidden=" + isHidden
                + ", isOnline=" + isOnline + ", isFavorite=" + isFavorite + ", isCloud=" + isCloud + ", capabilities="
                + capabilities + ", orientation=" + orientation + ", captureScreenShots=" + captureScreenShots + "]";
    }

    public String getDisplayString() {
        return (getDeviceDisplayName() + ", version=" + capabilities.getPlatformVersion()).trim() + ", id=" + getId();
    }

    private String getDeviceDisplayName() {
        return WordUtils.capitalize(StringUtils.defaultString(capabilities.getBrandName())) + " "
                + capabilities.getDeviceName();
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isCloud() {
        return isCloud;
    }

    public void setCloud(boolean isCloud) {
        this.isCloud = isCloud;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                .append(udid)
                .append(isBooked)
                .append(orientation)
                .append(captureScreenShots)
                .append(isHidden)
                .append(isOnline)
                .append(isFavorite)
                .append(isCloud)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        KobitonDevice other = (KobitonDevice) obj;
        return new EqualsBuilder().append(this.id, other.id)
                .append(this.udid, other.udid)
                .append(this.isBooked, other.isBooked)
                .append(this.orientation, other.orientation)
                .append(this.captureScreenShots, other.captureScreenShots)
                .append(this.isHidden, this.isHidden)
                .append(this.isOnline, other.isOnline)
                .append(this.isFavorite, this.isFavorite)
                .append(this.isCloud, this.isCloud)
                .isEquals();
    }
}
