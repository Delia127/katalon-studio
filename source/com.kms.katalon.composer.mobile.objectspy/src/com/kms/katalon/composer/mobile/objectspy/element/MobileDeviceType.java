package com.kms.katalon.composer.mobile.objectspy.element;

import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;

public enum MobileDeviceType {
    Local("Local devices"), Kobiton("Kobiton devices");

    private String displayName;

    private MobileDeviceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String[] displayNameValues() {
        String[] stringValues = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            stringValues[i] = values()[i].getDisplayName();
        }
        return stringValues;
    }

    public static MobileDeviceType fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        for (int i = 0; i < values().length; i++) {
            if (values()[i].getDisplayName().equals(displayName)) {
                return values()[i];
            }
        }
        return null;
    }

    public boolean isSupported() {
        if (this == MobileDeviceType.Kobiton) {
            return KobitonPreferencesProvider.isKobitonIntegrationEnabled();
        }
        return true;
    }
}
