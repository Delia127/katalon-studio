package com.kms.katalon.core.mobile.driver;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.constants.StringConstants;

public enum MobileDriverType implements DriverType {
	IOS_DRIVER(StringConstants.IOS_NATIVE), ANDROID_DRIVER(StringConstants.ANDROID_NATIVE);

	private String driverName;

	private MobileDriverType(String driverName) {
		this.driverName = driverName;
	}

	@Override
	public String getName() {
		return name();
	}

	public String getPlatform() {
		if (this == IOS_DRIVER) {
			return StringConstants.IOS;
		} else {
			return StringConstants.ANDROID;
		}
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

	public static MobileDriverType fromStringValue(String stringValue) {
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
		return com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_PLATFORM;
	}

	@Override
	public String getPropertyValue() {
		return getPlatform();
	}
}
