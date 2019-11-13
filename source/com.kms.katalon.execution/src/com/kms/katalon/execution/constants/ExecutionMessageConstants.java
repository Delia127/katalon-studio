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
    
    public static String MNG_PRT_PROJECT_PATH_IS_FOLDER;
    
    public static String MNG_PRT_FOUND_PROJECT_FILE;
    
    public static String MNG_PR_EXAMINE_FILE;
    
    public static String MNG_PR_EXAMINE_FOLDER;

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

    public static String MSG_PREPARE_GENERATE_REPORT;

    public static String MSG_PREPARE_REPORT_CSV;

    public static String MSG_PREPARE_REPORT_HTML;

    public static String MSG_PREPARE_REPORT_SIMPLE_HTML;

    public static String MSG_PREPARE_REPORT_JSON;

    public static String MSG_PREPARE_REPORT_JUNIT;
    
    public static String MSG_PREPARE_REPORT_UUID;

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
    
    public static String LAU_MESSAGE_UNABLE_TO_EXECUTE_TEST_SUITE;
    
    public static String LAU_MESSAGE_EMPTY_TEST_SUITE;
    
    public static String PREF_DEFAULT_EMAIL_SUBJECT;

    public static String CONSOLE_CANNOT_START_EXECUTION;

    public static String LBL_DEFAULT_EXECUTION;
    
    public static String LBL_APPLY_NEIGHBOR_XPATHS;
    
    // ConsoleOptionBuilder
    public static String CONSOLE_RUN_CONFIGURATION_NOT_FOUND;

    //MailUtil
    public static String MSG_EMAIL_ATTACHMENT_EXCEEDS_SIZE;
    
    //ConsoleMain
    public static String MNG_PRT_INVALID_PROPERTY_FILE_ARG;

    public static String ACTIVATE_MOVE_TO_KATALONC;

    public static String ACTIVATE_IN_ACTIVATING;

    public static String ACTIVATE_LICENSE_FILE_PATH;

    public static String ACTIVATE_LICENSE_FILE_FROM_OPTIONS;

    public static String ACTIVATE_LICENSE_FILE_FROM_ENVIRONMENT;

    public static String ACTIVATE_LICENSE_FILE_DEFAULT_PATH;

    public static String ACTIVATE_START_ACTIVATE_OFFLINE;

    public static String ACTIVATE_START_ACTIVATE_ONLINE;

    public static String ACTIVATE_START_ACTIVATE_ONLINE_WITH_LICENSE_SERVER;

    public static String ACTIVATE_FAIL_OFFLINE;

    public static String ACTIVATE_FAIL_ONLINE;

    public static String ACTIVATE_FAIL_RUNTIME_ENGINE;

    public static String RE_DONT_PERMISSION_TO_USE;

    public static String RE_ERROR_DELETE_LIB_FOLDERS;

    public static String RE_ERROR_DELETE_FOLDERS;

    public static String RE_DELETE_FOLDER;

    public static String RE_EXECUTE_COMPLETED;

    // TestSuiteLauncherOptionParser
    public static String CONSOLE_MSG_PROFILE_NOT_FOUND;
    
    public static String LAU_TS_REQUIRES_TAGS_PLUGIN_TO_EXECUTE;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ExecutionMessageConstants.class);
    }

    private ExecutionMessageConstants() {
    }
}
