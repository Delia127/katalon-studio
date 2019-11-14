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

    public static String LBL_ANDROID_EXECUTION_MENU_ITEM;

    public static String LBL_IOS_EXECUTION_MENU_ITEM;

    public static String WARNING_TITLE;

    // AndroidDeviceSelectionDialog
    public static String DIA_MSG_NO_DEVICES_CONNECTED;

    public static String DIA_LNK_TROUBLESHOOT;

    public static String DIA_LBL_TROUBLESHOOT;

    public static String DIA_LBL_LOADING_DEVICES;

    public static String DIA_TITLE_ANDROID_DEVICES;
    
    public static String DIA_TITLE_IOS_DEVICES;
    
    // iOS
    public static String MSG_IOS_INSTALL_DEPENDENCIES;

    public static String MSG_IOS_INSTALL_DEPENDENCIES_TITLE;

    public static String MSG_IOS_INSTALL_DEPENDENCIES_SUCCESSFULLY;

    public static String MSG_IOS_INSTALL_DEPENDENCIES_FAILED;

    public static String MSG_IOS_FAILED_TO_CHECK_NODE_INSTALLED;

    public static String MSG_IOS_INSTALL_WEB_DRIVER_AGENT;

    public static String MSG_IOS_INSTALL_WEB_DRIVER_AGENT_SUCCESSFULLY;

    public static String MSG_IOS_INSTALL_WEB_DRIVER_AGENT_FAILED;

    public static String DIA_TITLE_IOS_IDENTITIES;

    public static String DIA_LBL_LOADING_IDENTITIES;

    public static String DIA_MSG_NO_IDENTITY_FOUND;

    public static String DIA_LBL_IDENTITY_TROUBLESHOOT;

    public static String DIA_LNK_IDENTITY_TROUBLESHOOT;

    public static String MSG_IOS_INSTALL_WEB_DRIVER_AGENT_TITLE;

    public static String MSG_IOS_INSTALLING_WEB_DRIVER_AGENT;

    public static String MSG_IOS_RUN_BOOTSTRAP;

    public static String MSG_IOS_BUILD_WEB_DRIVER_AGENT_LIB;

    public static String MSG_IOS_BUILD_WEB_DRIVER_AGENT_RUNNER;

    public static String MSG_IOS_CANCELLED_WEB_DRIVER_AGENT_INSTALLATION;

    public static String MSG_IOS_CANCELLED_DEPENDENCIES_INSTALLATION;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerMobileMessageConstants.class);
    }

    private ComposerMobileMessageConstants() {
    }
}
