package com.kms.katalon.application.constants;

import org.eclipse.osgi.util.NLS;

public class ApplicationMessageConstants {
    private static final String BUNDLE_NAME = "com.kms.katalon.application.constants.applicationMessageConstants"; //$NON-NLS-1$

    public static String ACTIVATE_INFO_INVALID;
    
    public static String KSE_ACTIVATE_INFOR_INVALID;

    public static String ACTIVATION_CODE_INVALID;

    public static String ACTIVATION_COLLECT_FAIL_MESSAGE;

    public static String ERR_CONSOLE_MODE;

    public static String KATALON_NOT_ACTIVATED;

    public static String NETWORK_ERROR;
    
    public static String INVALID_ACCOUNT_ERROR;

    public static String SEND_SUCCESS_RESPONSE;

    public static String NO_PROXY;

    public static String MANUAL_CONFIG_PROXY;

    public static String USE_SYSTEM_PROXY;
    
    public static String PROXY_SERVER_TYPE_NOT_VALID_MESSAGE;
    
    public static String REQUEST_FAILED_AND_RETRY;

    public static String REQUEST_FAILED;

    public static String REQUEST_COMPLETED;

    public static String PROXY_FOUND;

    public static String NO_PROXY_FOUND;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ApplicationMessageConstants.class);
    }

    private ApplicationMessageConstants() {
    }
}
