package com.kms.katalon.execution.constants;

import org.eclipse.osgi.util.NLS;

public class ExecutionMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.execution.constants.executionMessages";

    public static String MNG_PRT_LOADING_PROJ;

    public static String MNG_PRT_PROJ_LOADED;

    public static String MNG_PRT_INVALID_EXECUTION_ARG;

    public static String MNG_PRT_INVALID_DELAY_TIME_ARG;

    public static String MNG_PRT_INVALID_FILE_NAME_ARG;

    public static String MNG_PRT_CLOSING_WORKBENCH;

    public static String MNG_PRT_WORKBENCH_CLOSED;

    public static String MNG_PRT_MISSING_REQUIRED_ARG;

    public static String MNG_PRT_INVALID_ARG_CANNOT_FIND_PROJ_X;

    public static String MNG_PRT_INVALID_ARG_CANNOT_PARSE_X_FOR_Y_TO_INTEGER;

    public static String MNG_PRT_TEST_SUITE_X_NOT_FOUND;

    public static String MNG_PRT_INVALID_BROWSER_X;

    public static String MNG_PRT_INVALID_RETRY_ARGUMENT;

    public static String MODEL_TOTAL_PASSED_FAILED_ERRORS;

    public static String UTIL_EXC_TEST_CASE_X_NOT_FOUND;

    public static String UTIL_EXC_TD_DATA_SRC_X_UNAVAILABLE;

    public static String UTIL_EXC_TD_X_DOES_NOT_CONTAIN_ANY_RECORDS;

    public static String UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_START_AT_ROW_IDX;

    public static String UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_ENDS_AT_ROW_IDX;

    public static String UTIL_EXC_IDX_X_INVALID_TC_Y_TD_Z;

    public static String MNG_INVALID_CONF_FILE_NAME_ARG;

    public static String LAU_RPT_RERUN_TEST_SUITE;

    public static String LAU_RPT_ERROR_TO_GENERATE_REPORT;

    public static String MSG_RP_ERROR_TO_EMAIL_REPORT;

    public static String MSG_RP_ERROR_TO_RERUN_TEST_SUITE;

    public static String MSG_RP_ERROR_TO_SEND_INTEGRATION_REPORT;

    public static String LAU_PRT_LAUNCHING_X;

    public static String LAU_PRT_X_DONE;

    public static String LAU_PRT_SENDING_EMAIL_RPT_TO;

    public static String LAU_PRT_SENDING_RPT_TO;

    public static String LAU_PRT_EMAIL_SENT;

    public static String LAU_PRT_REPORT_SENT;

    public static String LAU_PRT_COPYING_RPT_TO_USR_RPT_FOLDER;

    public static String LAU_PRT_CLEANING_USR_RPT_FOLDER;

    public static String LAU_PRT_REPORT_COPIED;

    public static String LAU_PRT_CANNOT_CREATE_REPORT_FOLDER;

    public static String LAU_PRT_CANNOT_SEND_EMAIL;

    public static String LAU_PRT_CANNOT_EXECUTE_TEST_SUITE;

    public static String LAU_PRT_X_FAILED_AT_LINE_Y;

    public static String LAU_PRT_FAILED_AT_LINE_X;

    public static String LAU_PRT_CANNOT_CLEAN_TEMP_FILES;

    public static String LAU_MESSAGE_UPLOADING_RPT;

    public static String LAU_MESSAGE_SENDING_EMAIL;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ExecutionMessageConstants.class);
    }

    private ExecutionMessageConstants() {
    }
}
