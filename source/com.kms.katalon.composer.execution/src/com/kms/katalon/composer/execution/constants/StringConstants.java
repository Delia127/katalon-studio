package com.kms.katalon.composer.execution.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    // Dialog
    public static final String INVALID_TYPE_MESSAGE = "Invalid Type";
    
	// LogPropertyDialog
	public static final String DIA_LOG_LVL_START = "START";
	public static final String DIA_LOG_LVL_END = "END";
	public static final String DIA_LOG_LVL_INFO = "INFO";
	public static final String DIA_LOG_LVL_PASSED = "PASSED";
	public static final String DIA_LOG_LVL_FAILED = "FAILED";
	public static final String DIA_LOG_LVL_ERROR = "ERROR";
	public static final String DIA_LOG_LVL_WARNING = "WARNING";
	public static final String DIA_LBL_TIME = TIME;
	public static final String DIA_LBL_LEVEL = LEVEL;
	public static final String DIA_LBL_MESSAGE = MESSAGE;
	public static final String DIA_TITLE_LOG_PROPERTIES = "Log's Properties";

	// ExecuteHandler
	public static final String ERROR_TITLE = ERROR;
	public static final String HAND_ERROR_MSG_NO_DEVICE = "No device is selected";
	public static final String HAND_ERROR_MSG_UNABLE_TO_EXECUTE_TEST_SCRIPT = "Unable to execute test script";
	public static final String HAND_ERROR_MSG_UNABLE_TO_EXECUTE_SELECTED_TEST_CASE = "Unable to execute the current selected test case.";
	public static final String HAND_ERROR_MSG_UNABLE_TO_EXECUTE_SELECTED_TEST_SUITE = "Unable to execute the current selected test suite.";
	public static final String HAND_WARN_MSG_NO_TEST_CASE_IN_TEST_SUITE = "The current selected test suite has no test case.";
	public static final String HAND_ERROR_MSG_REASON_WRONG_SYNTAX = "Wrong syntax";
	public static final String HAND_ERROR_MSG_REASON_INVALID_TEST_SUITE = "Test suite is not valid.";
	public static final String HAND_LAUNCHING_TEST_CASE = "Launching test case...";
	public static final String HAND_LAUNCHING_TEST_SUITE = "Launching test suite...";
	public static final String HAND_VALIDATING_TEST_SUITE = "Validating test suite...";
	public static final String HAND_ACTIVATING_VIEWERS = "Activating viewers...";
	public static final String HAND_BUILDING_SCRIPTS = "Building scripts...";

	// LogViewerPart
	public static final String PA_COLLAPSE_ALL = "Collapse all";
	public static final String PA_EXPAND_ALL = "Expand all";
	public static final String PA_PREV_FAILURE = "Show previous failure";
	public static final String PA_NEXT_FAILURE = "Show next failure";
	public static final String PA_LOADING_LOG = "Loading log";
	public static final String PA_LOADING_LOGS = PA_LOADING_LOG + "s";
	public static final String PA_LBL_START = "Start:";
	public static final String PA_LBL_END = "End:";
	public static final String PA_LBL_ELAPSED_TIME = "Elapsed time:";
	public static final String PA_LBL_MESSAGE = MESSAGE + ":";
	public static final String PA_LBL_RUNS = "Runs:";
	public static final String PA_LBL_PASSES = "Passes:";
	public static final String PA_LBL_FAILURES = "Failures:";
	public static final String PA_LBL_ERRORS = "Errors:";
	public static final String PA_TIP_ALL = ALL;
	public static final String PA_TIP_INFO = INFO;
	public static final String PA_TIP_PASSED = PASSED;
	public static final String PA_TIP_FAILED = FAILED;
	public static final String PA_TIP_ERROR = ERROR;
	public static final String PA_COL_LEVEL = LEVEL;
	public static final String PA_COL_TIME = TIME;
	public static final String PA_COL_MESSAGE = MESSAGE;
	public static final String PA_LOG_CONTEXT_MENU_PROPERTIES = DIA_TITLE_LOG_PROPERTIES;

	// LogExceptionNavigator
	public static final String WARN_TITLE = WARN;
	public static final String TRACE_WARN_MSG_NOT_FOUND = NOT_FOUND;
	public static final String TRACE_WARN_MSG_TEST_CASE_NOT_FOUND = TEST_CASE
			+ NOT_FOUND;
	public static final String TRACE_WARN_MSG_UNABLE_TO_OPEN_TEST_CASE = "Unable to open test case.";
	public static final String TRACE_WARN_MSG_UNABLE_TO_OPEN_KEYWORD_FILE = "Unable to open keyword's file";
	
	//Debug
	public static final String DBG_STRING_TYPE_NAME = "org.eclipse.jdt.debug.core.typeName";
	public static final String DBG_STRING_LINE_NUMBER = "lineNumber";
	public static final String DBG_COMMAND_SUSPEND = "org.eclipse.debug.ui.commands.Suspend";
	public static final String DBG_COMMAND_RESUME = "org.eclipse.debug.ui.commands.Resume";
	
	//ExecutionPreferencePage
    public static final String PREF_GRP_DEFAULT_EXECUTION_CONFIG = "Default execution";
    public static final String PREF_LBL_DEFAULT_PAGE_LOAD_TIMEOUT = "Default load timeout (in seconds)";
    public static final String PREF_GRP_POST_EXECUTION_OPTIONS = "Post-Execution Options";
    public static final String PREF_CHKBOX_NOTIFY_ME_AFTER_EXE_COMPLETELY = "Notify me";
    public static final String PREF_CHKBOX_OPEN_RPT_AFTER_EXE_COMPLETELY = "Open report";
    public static final String PREF_ERROR_MSG_VAL_MUST_BE_AN_INT_BETWEEN_X_Y = "Value must be an Integer between {0} and {1}";
    
    // MailPreferencePage
    public static final String PREF_GROUP_LBL_MAIL_SERVER = "Mail Server Settings";
    public static final String PREF_LBL_HOST = "Host";
    public static final String PREF_LBL_SECURITY_PROTOCOL = "Protocol";
    public static final String PREF_LBL_PORT = "Port";
    public static final String PREF_LBL_USERNAME = "Username";
    public static final String PREF_LBL_PASSWORD = "Password";
    public static final String PREF_LBL_REPORT_RECIPIENTS = "Report Recipients";
    public static final String PREF_LBL_SIGNATURE = "Signature";
    public static final String PREF_LBL_SEND_ATTACHMENT = "Send attachment";
    public static final String PREF_GROUP_LBL_EXECUTION_MAIL = "Post-Execution Options";    
    public static final String WARN_EMPTY_RECIPIENTS = "Empty email recipients";
    public static final String WARN_EMPTY_HOST = "Empty host";
    public static final String WARN_EMPTY_PORT = "Empty port";
    public static final String WARN_INVALID_PORT = "Invalid port. Port must be an integer number.";
    public static final String WARN_EMPTY_USERNAME = "Empty username";
    public static final String WARN_EMPTY_PASSWORD = "Empty password";
    public static final String PREF_LBL_SEND_TEST_EMAIL = "Send test email";
    public static final String PREF_SEND_TEST_EMAIL_JOB_NAME = "Sending test email";
    public static final String PREF_BUTTON_SEND_TEST_EMAIL = "Send";
    public static final String ERROR_SEND_TEST_EMAIL_FAIL =  "Unable to send test email ({0})";

    // DriverPreferencePage
    public static final String SETT_COL_PREFERENCE_NAME = NAME;
    public static final String SETT_COL_DRIVER_PREFERENCE_NAME = "Driver Name";
    public static final String SETT_COL_PREFERENCE = "Preferences";
    public static final String SETT_COL_PREFERENCE_TYPE = "Type";
    public static final String SETT_COL_PREFERENCE_VALUE = VALUE;
    public static final String SETT_TOOLITEM_ADD = ADD;
    public static final String SETT_TOOLITEM_REMOVE = DELETE;
    public static final String SETT_TOOLITEM_CLEAR = CLEAR;
    public static final String SETT_TOOLITEM_UP = UP;
    public static final String SETT_TOOLITEM_DOWN = DOWN;
    public static final String SETT_ERROR_MSG_UNABLE_TO_SAVE_PROJ_SETTS = "Unable to save project settings";
    
    // AddNewDriverPropertyDialog
    public static final String DIA_LBL_NAME = NAME;
    public static final String DIA_LBL_TYPE = "Type";
    public static final String DIA_LBL_VALUE = VALUE;
    public static final String DIA_SHELL_ADD_NEW_PREFERENCE = "Add new preference";
    public static final String DIA_SHELL_EDIT_PREFERENCE = "Edit preference";
    
    // ListPropertyValueBuilderDialog
    public static final String DIA_LIST_PROPERTY_VALUE_NAME = "List Property Builder";
    
    // MapPropertyValueBuilderDialog
    public static final String DIA_MAP_PROPERTY_VALUE_NAME = "Dictionary Property Builder";
    
    // DriverConnectorBuilderBuilderDialog
    public static final String DIA_DRIVER_CONNECTOR_BUILDER = "Driver Builder";
    
    // DriverConnectorListBuilderDialog
    public static final String DIA_DRIVER_LIST_CONNECTOR_BUILDER = "Custom Execution Configuration Builder";
    
    // CustomRunConfigurationHandler
    public static final String CUSTOM_RUN_CONFIG_ID_PREFIX = "com.kms.katalon.composer.execution.custom.";
}
