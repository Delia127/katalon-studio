package com.kms.katalon.logging.constant;

import org.eclipse.osgi.util.NLS;

public class LoggingMessageConstants {
    private static final String BUNDLE_NAME = "com.kms.katalon.logging.constant.loggingMessageConstants";
    
    public static String MSG_WARNING_SYSTEM_NOT_SUPPORT_UTF8;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, LoggingMessageConstants.class);
    }

    private LoggingMessageConstants() {
    }
}
