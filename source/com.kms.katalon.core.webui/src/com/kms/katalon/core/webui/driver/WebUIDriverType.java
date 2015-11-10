package com.kms.katalon.core.webui.driver;

import com.kms.katalon.core.driver.DriverType;

public enum WebUIDriverType implements DriverType {
    FIREFOX_DRIVER("Firefox"), IE_DRIVER("IE"), CHROME_DRIVER("Chrome"), SAFARI_DRIVER("Safari"), REMOTE_WEB_DRIVER(
            "Remote"), ANDROID_DRIVER("Android"), IOS_DRIVER("iOS"), EDGE_DRIVER("Edge");

    private final String driverName;

    private WebUIDriverType(String driverName) {
        this.driverName = driverName;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String toString() {
        return driverName;
    }

    public static String[] stringValues() {
        String[] stringValues = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            stringValues[i] = values()[i].toString();
        }
        return stringValues;
    }

    public static WebUIDriverType fromStringValue(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        for (int i = 0; i < values().length; i++) {
            if (values()[i].toString().equals(stringValue)) {
                return values()[i];
            }
        }
        return null;
    }

    @Override
    public String getPropertyKey() {
        return com.kms.katalon.core.webui.constants.StringConstants.CONF_PROPERTY_EXECUTED_BROWSER;
    }

    @Override
    public String getPropertyValue() {
        return getName();
    }
}
