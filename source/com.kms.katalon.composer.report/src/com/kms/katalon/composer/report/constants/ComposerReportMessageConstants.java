package com.kms.katalon.composer.report.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerReportMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.report.constants.composerReportMessages";

    public static String HAND_ERROR_MSG_UNABLE_TO_DELETE_REPORT;

    public static String TITLE_SUMMARY;

    public static String TITLE_EXECUTION_ENVIRONMENT;

    public static String TITLE_EXECUTION_SETTINGS;

    public static String LBL_EXECUTION_ENVIRONMENT;

    public static String LBL_HOST_NAME;

    public static String LBL_OS;

    public static String LBL_KATALON_VERSION;

    public static String LBL_PLATFORM;

    public static String LBL_BROWSER;

    public static String LBL_SOURCE_NAME;

    public static String LBL_STATUS;

    public static String HAND_ERROR_MSG_UNABLE_TO_OPEN_REPORT;

    public static String HAND_ERROR_MSG_TEST_CASE_NOT_FOUND;

    public static String REPORT_TABLE_ITEM_COLUMN_HEADER;

    public static String REPORT_TABLE_START_TIME_COLUMN_HEADER;

    public static String REPORT_TABLE_END_TIME_COLUMN_HEADER;

    public static String REPORT_TABLE_ELAPSED_TIME_COLUMN_HEADER;

    public static String REPORT_TABLE_STATUS_COLUMN_HEADER;

    public static String REPORT_TABLE_DESCRIPTION_COLUMN_HEADER;

    public static String REPORT_TABLE_ATTACHMENT_COLUMN_HEADER;

    public static String PA_SEARCH_TEXT_DEFAULT_VALUE;

    public static String COLUMN_LBL_RUN_DATA_KEY;

    public static String COLUMN_LBL_RUN_DATA_VALUE;

    public static String DIA_TITLE_ADVANCED_SEARCH;

    public static String DIA_ERROR_MSG_UNABLE_TO_UPDATE_ADVANCED_SEARCH;

    public static String DIA_LBL_INCLUDE_CHILD_LOGS;

    public static String PAGE_LBL_TAKE_SCREENSHOT_SETTINGS;

    public static String PAGE_TXT_ENABLE_TAKE_SCREENSHOT;

    public static String PAGE_ERROR_MSG_UNABLE_TO_READ_SETTINGS;

    public static String PAGE_ERROR_MSG_UNABLE_TO_UPDATE_SETTINGS;

    public static String REPORT_COLLECTION_LBL_ENVIRONMENT;

    public static String REPORT_COLLECTION_COLUMN_FAILED_TEST;

    public static String PROVIDER_TOOLTIP_CLICK_TO_SEE_DETAILS;

    public static String PROVIDER_TOOLTIP_SHOW_DETAILS;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerReportMessageConstants.class);
    }

    private ComposerReportMessageConstants() {
    }
}
