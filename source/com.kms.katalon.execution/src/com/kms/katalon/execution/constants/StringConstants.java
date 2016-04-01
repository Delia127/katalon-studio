package com.kms.katalon.execution.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {

    // ConsoleMain
    public static final String MNG_PRT_LOADING_PROJ = "Loading project...";
    public static final String MNG_PRT_PROJ_LOADED = "Project loaded.";
    public static final String MNG_PRT_INVALID_EXECUTION_ARG = "Invalid execution argument.";
    public static final String MNG_PRT_INVALID_DELAY_TIME_ARG = "Invalid delay time argument.";
    public static final String MNG_PRT_INVALID_FILE_NAME_ARG = "Invalid report file name argument.";
    public static final String MNG_PRT_CLOSING_WORKBENCH = "Closing workbench...";
    public static final String MNG_PRT_WORKBENCH_CLOSED = "Workbench closed.";
    public static final String MNG_PRT_MISSING_REQUIRED_ARG = "Missing required argument \"-{0}\".";
    public static final String MNG_PRT_INVALID_ARG_CANNOT_FIND_PROJ_X = "Invalid argument: Cannot find project ''{0}''.";
    public static final String MNG_PRT_INVALID_ARG_CANNOT_PARSE_X_FOR_Y_TO_INTEGER = "Invalid argument: Cannot parse option value ''{0}'' for option ''{1}'' to integer.";
    public static final String MNG_PRT_TEST_SUITE_X_NOT_FOUND = "Test suite ''{0}'' not found.";
    public static final String MNG_PRT_INVALID_BROWSER_X = "Invalid browser: ''{0}''";
    public static final String MNG_PRT_INVALID_RETRY_ARGUMENT = "Invalid retry argument value: ''{0}''. Using test suite's retry value.";

    // LauncherResult
    public static final String MODEL_TOTAL_PASSED_FAILED_ERRORS = "Total: {0}, Passed: {1}, Failed: {2}, Errors: {3}.";

    // ExecutionUtil
    public static final String UTIL_EXC_TEST_CASE_X_NOT_FOUND = "Test case: {0} not found.";
    public static final String UTIL_EXC_TD_DATA_SRC_X_UNAVAILABLE = "Data source of test data: {0} is not available.";
    public static final String UTIL_EXC_TD_X_DOES_NOT_CONTAIN_ANY_RECORDS = "Test data: ''{0}'' does not contain any records";
    public static final String UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_START_AT_ROW_IDX = "Test data: ''{0}'' has only {1} row(s) but test case : ''{2}'' starts at row index: {3}";
    public static final String UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_ENDS_AT_ROW_IDX = "Test data: ''{0}'' has only {1} row(s) but test case : ''{2}'' ends at row index: {3}";
    public static final String UTIL_EXC_IDX_X_INVALID_TC_Y_TD_Z = "Row index: {0} is not valid - Test case: {1} - Test data: {2}";

    public static final String MNG_INVALID_CONF_FILE_NAME_ARG = "Invalid configuration file name or syntax.";

    // Reportable Launcher
    public static final String LAU_RPT_RERUN_TEST_SUITE = "Re-run test suite: {0} # {1}.";

    public static final String LAU_RPT_ERROR_TO_GENERATE_REPORT = "System is unable to generate report. Reason: {0}";
    public static final String MSG_RP_ERROR_TO_EMAIL_REPORT = "System is unable to email report. Reason: {0}";
    public static final String MSG_RP_ERROR_TO_RERUN_TEST_SUITE = "System is unable to re-run test suite. Reason: {0}";

    public static final String MSG_RP_ERROR_TO_SEND_INTEGRATION_REPORT = "System is unable to send report to {0}. Reason: {1}";

    public static final String LAU_PRT_LAUNCHING_X = "Launching {0}...";
    public static final String LAU_PRT_X_DONE = "{0} DONE. {1}";
    public static final String LAU_PRT_SENDING_EMAIL_RPT_TO = "Sending report to email: {0}...";
    public static final String LAU_PRT_SENDING_RPT_TO = "Sending report to {0}...";
    public static final String LAU_PRT_EMAIL_SENT = "Email sent.";
    public static final String LAU_PRT_REPORT_SENT = "Report has been sent to {0}";
    public static final String LAU_PRT_COPYING_RPT_TO_USR_RPT_FOLDER = "Copying report to folder {0}...";
    public static final String LAU_PRT_CLEANING_USR_RPT_FOLDER = "Cleaning user's report folder...";
    public static final String LAU_PRT_REPORT_COPIED = "Copied successfully.";
    public static final String LAU_PRT_CANNOT_CREATE_REPORT_FOLDER = "Unable to create report folder. {0}";
    public static final String LAU_PRT_CANNOT_SEND_EMAIL = "Unable to send email. {0}";
    public static final String LAU_PRT_CANNOT_EXECUTE_TEST_SUITE = "Unable to execute test suite.";
    public static final String LAU_PRT_X_FAILED_AT_LINE_Y = "{0} failed at line: {1}";
    public static final String LAU_PRT_FAILED_AT_LINE_X = "Failed at line: {0}";
    public static final String LAU_PRT_CANNOT_CLEAN_TEMP_FILES = "Unable to clean temporary files";
}
