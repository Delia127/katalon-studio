package com.kms.katalon.integration.analytics.constants;

import org.eclipse.osgi.util.NLS;

public class IntegrationAnalyticsMessages extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.integration.analytics.constants.integrationAnalyticsMessages"; //$NON-NLS-1$
    
    public static String MSG_SEND_TEST_RESULT_START;
    
    public static String MSG_SEND_TEST_RESULT_END;
    
    public static String MSG_REQUEST_TOKEN_ERROR;
    
    public static String MSG_SEND_ERROR;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, IntegrationAnalyticsMessages.class);
    }
    
    private IntegrationAnalyticsMessages() {
    }
}
