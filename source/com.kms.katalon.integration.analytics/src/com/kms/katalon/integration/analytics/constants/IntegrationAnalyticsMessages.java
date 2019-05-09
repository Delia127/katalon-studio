package com.kms.katalon.integration.analytics.constants;

import org.eclipse.osgi.util.NLS;

public class IntegrationAnalyticsMessages extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.integration.analytics.constants.integrationAnalyticsMessages"; //$NON-NLS-1$
    
    public static String MSG_SEND_TEST_RESULT_START;
    
    public static String MSG_SEND_TEST_RESULT_END;
    
    public static String MSG_REQUEST_TOKEN_ERROR;
    
    public static String MSG_SEND_ERROR;
    
    public static String MSG_DLG_PRG_RETRIEVING_PROJECTS;
    
    public static String MSG_DLG_PRG_GETTING_PROJECTS;
    
    public static String MSG_DLG_PRG_RETRIEVING_TEAMS;
    
    public static String MSG_DLG_PRG_GETTING_TEAMS;
    
    public static String MSG_DLG_PRG_CONNECTING_TO_SERVER;
    
    public static String MSG_INTEGRATE_WITH_KA;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, IntegrationAnalyticsMessages.class);
    }
    
    private IntegrationAnalyticsMessages() {
    }
}
