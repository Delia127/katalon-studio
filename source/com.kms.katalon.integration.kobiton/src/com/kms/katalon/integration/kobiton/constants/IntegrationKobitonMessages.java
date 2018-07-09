package com.kms.katalon.integration.kobiton.constants;

import org.eclipse.osgi.util.NLS;

public class IntegrationKobitonMessages extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.integration.kobiton.constants.integrationKobitonMessages"; //$NON-NLS-1$
    
    public static String ERR_UNSUPPORTED_DEVICE_PLATFORM;

    public static String MSG_ERR_MISSING_EXECUTION_INFO;
    
    public static String MSG_ERR_KOBITON_DEVICE_NOT_FOUND;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, IntegrationKobitonMessages.class);
    }
    
    private IntegrationKobitonMessages() {
    }
}
