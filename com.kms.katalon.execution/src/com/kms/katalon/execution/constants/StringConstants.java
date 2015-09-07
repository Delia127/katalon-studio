package com.kms.katalon.execution.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	// ConsoleLauncher
	public static final String LAU_PRT_LAUNCHING_X = "Launching {0}...";
	public static final String LAU_PRT_X_DONE = "{0} DONE. {1}";
	public static final String LAU_PRT_SENDING_RPT_TO_INTEGRATING_PRODUCTS = "Sending report to integrating products...";
	public static final String LAU_PRT_REPORT_SENT = "Report sent.";
	public static final String LAU_PRT_COPYING_RPT_TO_USR_RPT_FOLDER = "Copying report to user's report folder...";
	public static final String LAU_PRT_USR_REPORT_FOLDER_X = "User''s report folder: {0}";
	public static final String LAU_PRT_CLEANING_USR_RPT_FOLDER = "Cleaning user's report folder...";
	public static final String LAU_PRT_REPORT_COPIED = "Copied successfully.";
	public static final String LAU_PRT_CANNOT_CREATE_REPORT_FOLDER = "Unable to create report folder. {0}";
	public static final String LAU_PRT_CANNOT_SEND_EMAIL = "Unable to send email. {0}";
	public static final String LAU_PRT_CANNOT_EXECUTE_TEST_SUITE = "Unable to execute test suite.";
	public static final String LAU_PRT_X_FAILED_AT_LINE_Y = "{0} failed at line: {1}";
	public static final String LAU_PRT_FAILED_AT_LINE_X = "Failed at line: {0}";
	public static final String LAU_PRT_CANNOT_CLEAN_TEMP_FILES = "Unable to clean temporary files";

	// ConsoleMain
	public static final String MNG_PRT_LOADING_PROJ = "Loading project...";
	public static final String MNG_PRT_PROJ_LOADED = "Project loaded.";
	public static final String MNG_PRT_INVALID_EXECUTION_ARG = "Invalid execution argument.";
	public static final String MNG_PRT_INVALID_DELAY_TIME_ARG = "Invalid delay time argument.";
	public static final String MNG_PRT_INVALID_FILE_NAME_ARG = "Invalid report file name argument.";
	public static final String MNG_PRT_MISSING_EXECUTION_ARG = "Missing execution argument.";
	public static final String MNG_PRT_CLOSING_WORKBENCH = "Closing workbench...";
	public static final String MNG_PRT_WORKBENCH_CLOSED = "Workbench closed.";
	public static final String MNG_PRT_MISSING_PROJ_ARG = "Missing project argument. Please specify using the prefix \"{0}\".";
	public static final String MNG_PRT_INVALID_ARG_CANNOT_FIND_PROJ = "Invalid argument: Cannot find project.";
	public static final String MNG_PRT_TEST_SUITE_X_NOT_FOUND = "Test suite ''{0}'' not found.";
	public static final String MNG_PRT_INVALID_BROWSER_X = "Invalid browser: ''{0}''";

	// LauncherResult
	public static final String MODEL_TOTAL_PASSED_FAILED_ERRORS = "Total: {0}, Passed: {1}, Failed: {2}, Errors: {3}.";

	// ExecutionPreferencePage
	public static final String PREF_GRP_DEFAULT_BROWSER = "Default browser";
	public static final String PREF_RADIO_FIREFOX = "Mozilla Firefox";
	public static final String PREF_RADIO_CHROME = "Google Chrome";
	public static final String PREF_RADIO_IE = "Internet Explorer";
	public static final String PREF_RADIO_SAFARI = "Safari";
	public static final String PREF_LBL_DEFAULT_PAGE_LOAD_TIMEOUT = "Default page load timeout (in seconds):";
	public static final String PREF_LBL_DEFAULT_WAIT_FOR_IE_HANGING_TIMEOUT = "Default wait for IE hanging timeout (in seconds):";
	public static final String PREF_GRP_AFTER_EXECUTING = "After executing";
	public static final String PREF_CHKBOX_NOTIFY_ME_AFTER_EXE_COMPLETELY = "Notify me after executing completely";
	public static final String PREF_CHKBOX_OPEN_RPT_AFTER_EXE_COMPLETELY = "Open report after executing completely";
	public static final String PREF_ERROR_MSG_VAL_MUST_BE_AN_INT_BETWEEN_X_Y = "Value must be an Integer between {0} and {1}";

	// ExecutionUtil
	public static final String UTIL_EXC_TEST_CASE_X_NOT_FOUND = "Test case: {0} not found.";
	public static final String UTIL_EXC_TD_DATA_SRC_X_UNAVAILABLE = "Data source of test data: {0} is not available.";
	public static final String UTIL_EXC_TD_X_DOES_NOT_CONTAIN_ANY_RECORDS = "Test data: ''{0}'' does not contain any records";
	public static final String UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_START_AT_ROW_IDX = "Test data: ''{0}'' has only {1} row(s) but test case : ''{2}'' starts at row index: {3}";
	public static final String UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_ENDS_AT_ROW_IDX = "Test data: ''{0}'' has only {1} row(s) but test case : ''{2}'' ends at row index: {3}";
	public static final String UTIL_EXC_IDX_X_INVALID_TC_Y_TD_Z = "Index: {0} is not valid - Test case: {1} - Test data: {2}";
}
