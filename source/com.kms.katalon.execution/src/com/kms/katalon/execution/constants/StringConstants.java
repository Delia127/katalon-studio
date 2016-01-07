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
	public static final String MNG_PRT_MISSING_EXECUTION_ARG = "Cannot find any execution argument. Please provide one with param '-execute'";
	public static final String MNG_PRT_MISSING_TESTSUITE_ARG = "Cannot find any test suite id argument. Please provide one with param '-testSuiteID'";
	public static final String MNG_PRT_MISSING_BROWSERTYPE_ARG = "Cannot find any browser type argument. Please provide one with param '-browserType'";
	public static final String MNG_PRT_CLOSING_WORKBENCH = "Closing workbench...";
	public static final String MNG_PRT_WORKBENCH_CLOSED = "Workbench closed.";
	public static final String MNG_PRT_MISSING_PROJ_ARG = "Missing project argument. Please specify using the prefix \"{0}\".";
	public static final String MNG_PRT_INVALID_ARG_CANNOT_FIND_PROJ_X = "Invalid argument: Cannot find project ''{0}''.";
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
}
