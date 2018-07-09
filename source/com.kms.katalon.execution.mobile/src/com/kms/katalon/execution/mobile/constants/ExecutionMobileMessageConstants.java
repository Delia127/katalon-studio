package com.kms.katalon.execution.mobile.constants;

import org.eclipse.osgi.util.NLS;

public class ExecutionMobileMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.execution.mobile.constants.executionMobileMessages";

    public static String MOBILE_ERR_NO_DEVICE_NAME_AVAILABLE;

    public static String MOBILE_ERR_CANNOT_FIND_DEVICE_WITH_NAME_X;

    // MobileExecutionUtil
    public static String MSG_NO_APPIUM;

    public static String MSG_NO_NODEJS;

    public static String MSG_NO_APPIUM_AND_NODEJS;

    // AndroidSDKDownloadManager
    public static String MSG_SDK_FETCHING_SDK_INFO;

    public static String MSG_SDK_FETCH_COMPLETED;

    public static String MSG_SDK_PARSING_SDK_INFO;

    public static String MSG_SDK_PARSE_COMPLETED;

    public static String MSG_SDK_DOWNLOADING_X_FROM_Y;

    public static String MSG_SDK_DOWNLOAD_COMPLETED;

    public static String MSG_SDK_EXTRACTING_X_TO_Y;

    public static String MSG_SDK_EXTRACT_COMPLETED;

    public static String MSG_SDK_DONWLOADING_SDK;

    public static String MSG_SDK_DOWNLOAD_AND_INSTALL_SDK_COMPELTED;

    public static String MSG_SDK_COULD_NOT_CONNECT;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ExecutionMobileMessageConstants.class);
    }

    private ExecutionMobileMessageConstants() {
    }
}
