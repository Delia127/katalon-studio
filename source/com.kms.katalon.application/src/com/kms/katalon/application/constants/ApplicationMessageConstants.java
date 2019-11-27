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

    public static String ACTIVATION_ONLINE_INVALID;

    public static String ACTIVATION_GUI_FAIL;

    public static String ACTIVATION_CLI_FAIL;

    public static String ACTIVATION_OFFLINE_FAIL;

    public static String LICENSE_INVALID;

    public static String LICENSE_INVALID_MACHINE_ID;

    public static String LICENSE_INVALID_KSE_USE_TO_KRE;

    public static String LICENSE_INVALID_KRE_USE_TO_KSE;

    public static String LICENSE_INCORRECT_MACHINE_ID;

    public static String LICENSE_EXPIRED_WITH_FILE_NAME;

    public static String LICENSE_EXPIRED;

    public static String LICENSE_UNABLE_RENEW;

    public static String LICENSE_EXPIRED_MESSAGE;

    public static String LICENSE_EXPIRED_NO_MESSAGE;

    public static String LICENSE_ERROR_RENEW;

    public static String TESTING_LICENSE_WITH_FILENAME_CORRECT;

    public static String TESTING_LICENSE_WITH_FILENAME_INCORRECT;

    public static String TESTING_LICENSE_MACHINE_ID_CORRECT;

    public static String TESTING_LICENSE_MACHINE_ID_INCORRECT;

    public static String AUTO_CLOSE;

    public static String BTN_ACKNOWLEDGE;

    public static String RE_FIND_VAILD_OFFLINE_LICENSE_IN_FOLDER;

    public static String RE_START_CHECK_LICENSE;

    public static String RE_LICENSE_FILE_VAILD;

    public static String TITLE_KS_NOTIFICATION;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ApplicationMessageConstants.class);
    }

    private ApplicationMessageConstants() {
    }
}
