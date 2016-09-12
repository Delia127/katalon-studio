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

    // MobileSettingPreferencePage
    public static final String PREF_LBL_APPIUM_DIRECTORY = "Appium Directory";

    public static final String PREF_LBL_APPIUM_LOG_LEVEL = "Appium Log Level";

    // IosExecutionDynamicContribution
    public static final String LBL_IOS_EXECUTION_MENU_ITEM = "iOS";

    public static final String WARNING_TITLE = "Warning";

    public static final String MSG_NO_APPIUM = "It appears that you don't have Appium installed and setup correctly, please setup it by following the user guide in this page:";

    public static final String MSG_NO_NODEJS = "It appears that you don't have NodeJS installed and setup correctly, please setup it by following the user guide in this page:";

    public static final String MSG_NO_APPIUM_AND_NODEJS = "It appears that you don't have Appium and NodeJS installed and setup correctly, please setup them by following the user guide in this page:";

    public static final String MSG_FAILED_DETECT_NODEJS = "Failed to detect NodeJS on your machine";

    public static final String LINK = "http://docs.katalon.com/display/KD/Installation+and+Setup";
    
    public static final String MAC_DEFAULT_NODEJS_LOCATION = "/usr/local/bin/node";
}
