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

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ExecutionMobileMessageConstants.class);
    }

    private ExecutionMobileMessageConstants() {
    }
}
