package com.kms.katalon.composer.mobile.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerMobileMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.mobile.constants.composerMobileMessages";

    public static String DIA_DEVICE_NAME;

    public static String DIA_BROWSER_NAME;

    public static String DIA_SELECT_DEVICE_NAME_MSG;

    public static String DIA_SELECT_MIXED_MODE_MSG;

    public static String DIA_ERROR_NULL_DEVICE_NAME;

    public static String DIA_ERROR_CANNOT_FOUND_DEVICE_NAME;

    public static String DIA_ERROR_NULL_DEVICE_BROWSER_NAME;

    public static String PREF_LBL_APPIUM_DIRECTORY;

    public static String PREF_LBL_APPIUM_LOG_LEVEL;

    public static String LBL_IOS_EXECUTION_MENU_ITEM;

    public static String WARNING_TITLE;

    public static String MSG_NO_APPIUM;

    public static String MSG_NO_NODEJS;

    public static String MSG_NO_APPIUM_AND_NODEJS;

    public static String MSG_FAILED_DETECT_NODEJS;

    public static String LINK;

    public static String MAC_DEFAULT_NODEJS_LOCATION;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerMobileMessageConstants.class);
    }

    private ComposerMobileMessageConstants() {
    }
}
