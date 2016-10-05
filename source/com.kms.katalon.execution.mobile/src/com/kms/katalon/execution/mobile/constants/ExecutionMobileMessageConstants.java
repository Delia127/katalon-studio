package com.kms.katalon.execution.mobile.constants;

import org.eclipse.osgi.util.NLS;

public class ExecutionMobileMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.execution.mobile.constants.executionMobileMessages";

    public static String MOBILE_ERR_NO_DEVICE_NAME_AVAILABLE;

    public static String MOBILE_ERR_CANNOT_FIND_DEVICE_WITH_NAME_X;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ExecutionMobileMessageConstants.class);
    }

    private ExecutionMobileMessageConstants() {
    }
}
