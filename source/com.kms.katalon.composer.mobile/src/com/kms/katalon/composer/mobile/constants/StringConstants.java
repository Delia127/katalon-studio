package com.kms.katalon.composer.mobile.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	// DeviceSelectionDialog
	public static final String OS_ANDROID = ANDROID;
	public static final String OS_IOS = IOS;
	public static final String DIA_DEVICE_NAME = "Device Name:";
	public static final String DIA_BROWSER_NAME = "Browser Name:";
	public static final String DIA_SELECT_DEVICE_NAME_MSG = "Select a device name to execute with";
	
	public static final String DIA_SELECT_MIXED_MODE_MSG = "Select your browser, device to run with";
	
	// MobileExecutionHandler
	public static final String DIA_ERROR_NULL_DEVICE_NAME = "No device is selected";
	public static final String DIA_ERROR_CANNOT_FOUND_DEVICE_NAME = "Device ''{0}'' cannot be found.";
	
	// MixedModeExecutionHandler
	public static final String DIA_ERROR_NULL_DEVICE_BROWSER_NAME = "No device/browser is selected";
	
	//MobileSettingPreferencePage
	public static final String PREF_LBL_APPIUM_DIRECTORY = "Appium Directory";
	public static final String PREF_LBL_APPIUM_LOG_LEVEL = "Appium Log Level";
}
