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

    public static String MSG_DLG_PRG_TITLE_UPLOAD_CODE;

    public static String MSG_DLG_PRG_CREATE_TEST_PLAN;

    public static String VIEW_ERROR_MSG_PROJ_USER_CAN_NOT_ACCESS_PROJECT;

    public static String MSG_INTEGRATE_WITH_KA;

    public static String MSG_EXECUTION_URL;

    public static String VIEW_ERROR_MSG_SPECIFY_KATALON_API_KEY;

    public static String STORE_CODE_COMPRESSING_PROJECT;

    public static String STORE_CODE_REQUEST_SERVER;

    public static String STORE_CODE_GET_TEAM_PROJECT;

    public static String STORE_CODE_UPLOAD;

    public static String STORE_CODE_OPEN_BROWSER;

    public static String STORE_CODE_ERROR_COMPRESS;

    public static String STORE_CODE_ERROR_NO_FILE_NAME;

    public static String STORE_CODE_ERROR_NO_NAME;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, IntegrationAnalyticsMessages.class);
    }

    private IntegrationAnalyticsMessages() {
    }
}
