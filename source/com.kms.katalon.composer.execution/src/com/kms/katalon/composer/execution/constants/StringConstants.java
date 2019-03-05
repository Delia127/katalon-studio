package com.kms.katalon.composer.execution.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    
    //MailSettingsPage
    public static final String EMAIL_TEMPLATE_PAGE_ID = "com.kms.katalon.composer.execution.page.emailTemplate";

    // TestExecutionAddon
    public static final String KATALON_COMPOSER_EXECUTION_BUNDLE_URI = "bundleclass://com.kms.katalon.composer.execution/";

    public static final String KATALON_COMPOSER_EXECUTION_ID = "com.kms.katalon.composer.execution";

    public static final String CUSTOM_RUN_MENU_ID = KATALON_COMPOSER_EXECUTION_ID + ".run.custom";

    public static final String CUSTOM_DEBUG_MENU_ID = KATALON_COMPOSER_EXECUTION_ID + ".debug.custom";

    public static final String CUSTOM_RUN_MENU_LABEL = ComposerExecutionMessageConstants.CUSTOM_RUN_MENU_LABEL;

    public static final String CUSTOM_RUN_CONFIG_CONTRIBUTOR_ID = CUSTOM_RUN_MENU_ID + ".contributor";

    // Dialog
    public static final String INVALID_TYPE_MESSAGE = ComposerExecutionMessageConstants.INVALID_TYPE_MESSAGE;

    public static final String DIA_LBL_TIME = TIME;

    public static final String DIA_LBL_LEVEL = LEVEL;

    public static final String DIA_LBL_MESSAGE = MESSAGE;

    public static final String DIA_TITLE_LOG_PROPERTIES = ComposerExecutionMessageConstants.DIA_TITLE_LOG_PROPERTIES;

    // ExecuteHandler
    public static final String ERROR_TITLE = ERROR;

    public static final String HAND_ERROR_MSG_NO_DEVICE = ComposerExecutionMessageConstants.HAND_ERROR_MSG_NO_DEVICE;

    public static final String HAND_ERROR_MSG_UNABLE_TO_EXECUTE_TEST_SCRIPT_ROOT_CAUSE = ComposerExecutionMessageConstants.HAND_ERROR_MSG_UNABLE_TO_EXECUTE_TEST_SCRIPT_ROOT_CAUSE;

    public static final String HAND_ERROR_MSG_UNABLE_TO_EXECUTE_SELECTED_TEST_CASE = ComposerExecutionMessageConstants.HAND_ERROR_MSG_UNABLE_TO_EXECUTE_SELECTED_TEST_CASE;

    public static final String HAND_ERROR_MSG_UNABLE_TO_EXECUTE_SELECTED_TEST_SUITE = ComposerExecutionMessageConstants.HAND_ERROR_MSG_UNABLE_TO_EXECUTE_SELECTED_TEST_SUITE;

    public static final String HAND_WARN_MSG_NO_TEST_CASE_IN_TEST_SUITE = ComposerExecutionMessageConstants.HAND_WARN_MSG_NO_TEST_CASE_IN_TEST_SUITE;

    public static final String HAND_ERROR_MSG_REASON_WRONG_SYNTAX = ComposerExecutionMessageConstants.HAND_ERROR_MSG_REASON_WRONG_SYNTAX;

    public static final String HAND_ERROR_MSG_REASON_INVALID_TEST_SUITE = ComposerExecutionMessageConstants.HAND_ERROR_MSG_REASON_INVALID_TEST_SUITE;

    public static final String HAND_JOB_LAUNCHING_TEST_CASE = ComposerExecutionMessageConstants.HAND_JOB_LAUNCHING_TEST_CASE;

    public static final String HAND_JOB_LAUNCHING_TEST_SUITE = ComposerExecutionMessageConstants.HAND_JOB_LAUNCHING_TEST_SUITE;

    public static final String HAND_JOB_VALIDATING_TEST_SUITE = ComposerExecutionMessageConstants.HAND_JOB_VALIDATING_TEST_SUITE;

    public static final String HAND_JOB_ACTIVATING_VIEWERS = ComposerExecutionMessageConstants.HAND_JOB_ACTIVATING_VIEWERS;

    public static final String HAND_JOB_BUILDING_SCRIPTS = ComposerExecutionMessageConstants.HAND_JOB_BUILDING_SCRIPTS;

    public static final String HAND_TITLE_INFORMATION = ComposerExecutionMessageConstants.HAND_TITLE_INFORMATION;

    public static final String HAND_ERROR_MSG_ERROR_IN_SCRIPT = ComposerExecutionMessageConstants.HAND_ERROR_MSG_ERROR_IN_SCRIPT;
    
    public static final String HAND_INFO_MSG_NO_TEST_STEP_IN_TEST_CASE = ComposerExecutionMessageConstants.HAND_INFO_MSG_NO_TEST_STEP_IN_TEST_CASE;
    
    public static final String HAND_CONFIRM_MSG_NO_TEST_CASE_IN_TEST_SUITE = ComposerExecutionMessageConstants.HAND_CONFIRM_MSG_NO_TEST_CASE_IN_TEST_SUITE;

    public static final String HAND_WARN_MSG_NO_TEST_CASE_SELECTED = ComposerExecutionMessageConstants.HAND_WARN_MSG_NO_TEST_CASE_SELECTED;

    // ToggleBreakpointHandler
    public static final String HAND_ERROR_MSG_CANNOT_TOGGLE_LINE_BREAKPOINT = ComposerExecutionMessageConstants.HAND_ERROR_MSG_CANNOT_TOGGLE_LINE_BREAKPOINT;

    // LogViewerPart
    public static final String PA_COLLAPSE_ALL = ComposerExecutionMessageConstants.PA_COLLAPSE_ALL;

    public static final String PA_EXPAND_ALL = ComposerExecutionMessageConstants.PA_EXPAND_ALL;

    public static final String PA_PREV_FAILURE = ComposerExecutionMessageConstants.PA_PREV_FAILURE;

    public static final String PA_NEXT_FAILURE = ComposerExecutionMessageConstants.PA_NEXT_FAILURE;

    public static final String PA_LOADING_LOG = ComposerExecutionMessageConstants.PA_LOADING_LOG;

    public static final String PA_LOADING_LOGS = ComposerExecutionMessageConstants.PA_LOADING_LOGS;

    public static final String PA_LBL_START = ComposerExecutionMessageConstants.PA_LBL_START;

    public static final String PA_LBL_ELAPSED_TIME = ComposerExecutionMessageConstants.PA_LBL_ELAPSED_TIME;

    public static final String PA_LBL_MESSAGE = MESSAGE + ":";

    public static final String PA_LBL_RUNS = ComposerExecutionMessageConstants.PA_LBL_RUNS;

    public static final String PA_LBL_PASSES = ComposerExecutionMessageConstants.PA_LBL_PASSES;

    public static final String PA_LBL_FAILURES = ComposerExecutionMessageConstants.PA_LBL_FAILURES;

    public static final String PA_LBL_ERRORS = ComposerExecutionMessageConstants.PA_LBL_ERRORS;

    public static final String PA_TIP_ALL = ALL;

    public static final String PA_TIP_INFO = INFO;

    public static final String PA_TIP_PASSED = PASSED;

    public static final String PA_TIP_FAILED = FAILED;

    public static final String PA_TIP_ERROR = ERROR;

    public static final String PA_TIP_WARNING = WARN;

    public static final String PA_TIP_NOT_RUN = NOT_RUN;

    public static final String PA_COL_LEVEL = LEVEL;

    public static final String PA_COL_TIME = TIME;

    public static final String PA_COL_MESSAGE = MESSAGE;

    public static final String PA_LOG_CONTEXT_MENU_PROPERTIES = DIA_TITLE_LOG_PROPERTIES;

    public static final String PA_LOG_RESUME = ComposerExecutionMessageConstants.PA_LOG_RESUME;

    public static final String PA_LOG_PAUSE = ComposerExecutionMessageConstants.PA_LOG_PAUSE;

    // LogExceptionNavigator
    public static final String WARN_TITLE = WARN;

    public static final String TRACE_WARN_MSG_NOT_FOUND = NOT_FOUND;

    public static final String TRACE_WARN_MSG_TEST_CASE_NOT_FOUND = TEST_CASE + NOT_FOUND;

    public static final String TRACE_WARN_MSG_UNABLE_TO_OPEN_TEST_CASE = ComposerExecutionMessageConstants.TRACE_WARN_MSG_UNABLE_TO_OPEN_TEST_CASE;

    public static final String TRACE_WARN_MSG_UNABLE_TO_OPEN_KEYWORD_FILE = ComposerExecutionMessageConstants.TRACE_WARN_MSG_UNABLE_TO_OPEN_KEYWORD_FILE;

    // ExecutionPreferencePage
    public static final String PREF_LBL_DEFAULT_IMPLICIT_TIMEOUT = ComposerExecutionMessageConstants.PREF_LBL_DEFAULT_IMPLICIT_TIMEOUT;

    public static final String PREF_GRP_POST_EXECUTION_OPTIONS = ComposerExecutionMessageConstants.PREF_GRP_POST_EXECUTION_OPTIONS;

    public static final String PREF_CHKBOX_NOTIFY_ME_AFTER_EXE_COMPLETELY = ComposerExecutionMessageConstants.PREF_CHKBOX_NOTIFY_ME_AFTER_EXE_COMPLETELY;

    public static final String PREF_CHKBOX_OPEN_RPT_AFTER_EXE_COMPLETELY = ComposerExecutionMessageConstants.PREF_CHKBOX_OPEN_RPT_AFTER_EXE_COMPLETELY;

    public static final String PREF_CHKBOX_QUIT_DRIVERS_AFTER_EXE_COMPLETELY = ComposerExecutionMessageConstants.PREF_CHKBOX_QUIT_DRIVERS_AFTER_EXE_COMPLETELY;

    public static final String PREF_ERROR_MSG_VAL_MUST_BE_AN_INT_BETWEEN_X_Y = ComposerExecutionMessageConstants.PREF_ERROR_MSG_VAL_MUST_BE_AN_INT_BETWEEN_X_Y;

    // MailPreferencePage
    public static final String PREF_GROUP_LBL_MAIL_SERVER = ComposerExecutionMessageConstants.PREF_GROUP_LBL_MAIL_SERVER;

    public static final String PREF_LBL_HOST = ComposerExecutionMessageConstants.PREF_LBL_HOST;

    public static final String PREF_LBL_SECURITY_PROTOCOL = ComposerExecutionMessageConstants.PREF_LBL_SECURITY_PROTOCOL;

    public static final String PREF_LBL_PORT = ComposerExecutionMessageConstants.PREF_LBL_PORT;

    public static final String PREF_LBL_USERNAME = ComposerExecutionMessageConstants.PREF_LBL_USERNAME;

    public static final String PREF_LBL_PASSWORD = ComposerExecutionMessageConstants.PREF_LBL_PASSWORD;

    public static final String PREF_LBL_REPORT_RECIPIENTS = ComposerExecutionMessageConstants.PREF_LBL_REPORT_RECIPIENTS;

    public static final String PREF_LBL_SIGNATURE = ComposerExecutionMessageConstants.PREF_LBL_SIGNATURE;

    public static final String PREF_LBL_INCLUDE_ATTACHMENT = ComposerExecutionMessageConstants.PREF_LBL_INCLUDE_ATTACHMENT;

    public static final String PREF_GROUP_LBL_EXECUTION_MAIL = ComposerExecutionMessageConstants.PREF_GROUP_LBL_EXECUTION_MAIL;

    public static final String WARN_EMPTY_RECIPIENTS = ComposerExecutionMessageConstants.WARN_EMPTY_RECIPIENTS;

    public static final String WARN_EMPTY_HOST = ComposerExecutionMessageConstants.WARN_EMPTY_HOST;

    public static final String WARN_EMPTY_PORT = ComposerExecutionMessageConstants.WARN_EMPTY_PORT;

    public static final String WARN_INVALID_PORT = ComposerExecutionMessageConstants.WARN_INVALID_PORT;

    public static final String WARN_EMPTY_USERNAME = ComposerExecutionMessageConstants.WARN_EMPTY_USERNAME;

    public static final String WARN_EMPTY_PASSWORD = ComposerExecutionMessageConstants.WARN_EMPTY_PASSWORD;

    public static final String PREF_LBL_SEND_TEST_EMAIL = ComposerExecutionMessageConstants.PREF_LBL_SEND_TEST_EMAIL;

    public static final String PREF_SEND_TEST_EMAIL_JOB_NAME = ComposerExecutionMessageConstants.PREF_SEND_TEST_EMAIL_JOB_NAME;

    public static final String PREF_BUTTON_SEND_TEST_EMAIL = ComposerExecutionMessageConstants.PREF_BUTTON_SEND_TEST_EMAIL;

    public static final String ERROR_SEND_TEST_EMAIL_FAIL = ComposerExecutionMessageConstants.ERROR_SEND_TEST_EMAIL_FAIL;

    // DriverPreferencePage
    public static final String SETT_COL_PREFERENCE_NAME = NAME;

    public static final String SETT_COL_DRIVER_PREFERENCE_NAME = ComposerExecutionMessageConstants.SETT_COL_DRIVER_PREFERENCE_NAME;

    public static final String SETT_COL_PREFERENCE = ComposerExecutionMessageConstants.SETT_COL_PREFERENCE;

    public static final String SETT_COL_PREFERENCE_TYPE = ComposerExecutionMessageConstants.SETT_COL_PREFERENCE_TYPE;

    public static final String SETT_COL_PREFERENCE_VALUE = VALUE;

    public static final String SETT_TOOLITEM_ADD = ADD;

    public static final String SETT_TOOLITEM_REMOVE = DELETE;

    public static final String SETT_TOOLITEM_CLEAR = CLEAR;

    public static final String SETT_TOOLITEM_UP = UP;

    public static final String SETT_TOOLITEM_DOWN = DOWN;

    public static final String SETT_ERROR_MSG_UNABLE_TO_SAVE_PROJ_SETTS = ComposerExecutionMessageConstants.SETT_ERROR_MSG_UNABLE_TO_SAVE_PROJ_SETTS;

    // AddNewDriverPropertyDialog
    public static final String DIA_LBL_NAME = NAME;

    public static final String DIA_LBL_TYPE = ComposerExecutionMessageConstants.DIA_LBL_TYPE;

    public static final String DIA_LBL_VALUE = VALUE;

    public static final String DIA_SHELL_ADD_NEW_PREFERENCE = ComposerExecutionMessageConstants.DIA_SHELL_ADD_NEW_PREFERENCE;

    public static final String DIA_SHELL_EDIT_PREFERENCE = ComposerExecutionMessageConstants.DIA_SHELL_EDIT_PREFERENCE;

    // ListPropertyValueBuilderDialog
    public static final String DIA_LIST_PROPERTY_VALUE_NAME = ComposerExecutionMessageConstants.DIA_LIST_PROPERTY_VALUE_NAME;

    // MapPropertyValueBuilderDialog
    public static final String DIA_MAP_PROPERTY_VALUE_NAME = ComposerExecutionMessageConstants.DIA_MAP_PROPERTY_VALUE_NAME;

    // DriverConnectorBuilderBuilderDialog
    public static final String DIA_DRIVER_CONNECTOR_BUILDER = ComposerExecutionMessageConstants.DIA_DRIVER_CONNECTOR_BUILDER;

    // DriverConnectorListBuilderDialog
    public static final String DIA_DRIVER_LIST_CONNECTOR_BUILDER = ComposerExecutionMessageConstants.DIA_DRIVER_LIST_CONNECTOR_BUILDER;

    // CustomRunConfigurationHandler
    public static final String CUSTOM_RUN_CONFIG_ID_PREFIX = "com.kms.katalon.composer.execution.custom.";

    // GenerateCommandDialog
    public static final String DIA_TITLE_GENERATE_COMMAND_FOR_CONSOLE = ComposerExecutionMessageConstants.DIA_TITLE_GENERATE_COMMAND_FOR_CONSOLE;

    public static final String DIA_GRP_EXECUTED_PLATFORM = ComposerExecutionMessageConstants.DIA_GRP_EXECUTED_PLATFORM;

    public static final String DIA_REMOTE_WEB_DRIVER_URL = ComposerExecutionMessageConstants.DIA_REMOTE_WEB_DRIVER_URL;

    public static final String DIA_RADIO_BROWSER = ComposerExecutionMessageConstants.DIA_RADIO_BROWSER;

    public static final String DIA_RADIO_MOBILE_DEVICE = ComposerExecutionMessageConstants.DIA_RADIO_MOBILE_DEVICE;

    public static final String DIA_GRP_REPORT_CONFIG = ComposerExecutionMessageConstants.DIA_GRP_REPORT_CONFIG;

    public static final String DIA_OUTPUT_LOCATION = ComposerExecutionMessageConstants.DIA_OUTPUT_LOCATION;

    public static final String DIA_CHK_USE_RELATIVE_PATH = ComposerExecutionMessageConstants.DIA_CHK_USE_RELATIVE_PATH;

    public static final String DIA_LBL_REPORT_NAME = ComposerExecutionMessageConstants.DIA_LBL_REPORT_NAME;

    public static final String DIA_TXT_DEFAULT_REPORT_NAME = ComposerExecutionMessageConstants.DIA_TXT_DEFAULT_REPORT_NAME;

    public static final String DIA_LBL_POST_EXECUTION = ComposerExecutionMessageConstants.DIA_LBL_POST_EXECUTION;

    public static final String DIA_CHK_SEND_SUMMARY_REPORT = ComposerExecutionMessageConstants.DIA_CHK_SEND_SUMMARY_REPORT;

    public static final String DIA_LBL_MAIL_RECIPIENTS = ComposerExecutionMessageConstants.DIA_LBL_MAIL_RECIPIENTS;

    public static final String DIA_GRP_OTHER_OPTIONS = ComposerExecutionMessageConstants.DIA_GRP_OTHER_OPTIONS;

    public static final String DIA_CHK_DISPLAY_CONSOLE_LOG = ComposerExecutionMessageConstants.DIA_CHK_DISPLAY_CONSOLE_LOG;

    public static final String DIA_CHK_KEEP_CONSOLE_LOG = ComposerExecutionMessageConstants.DIA_CHK_KEEP_CONSOLE_LOG;

    public static final String DIA_LBL_UPDATE_EXECUTION_STATUS = ComposerExecutionMessageConstants.DIA_LBL_UPDATE_EXECUTION_STATUS;

    public static final String DIA_LBL_SECONDS = ComposerExecutionMessageConstants.DIA_LBL_SECONDS;

    public static final String DIA_BTN_GEN_PROPERTY_FILE = ComposerExecutionMessageConstants.DIA_BTN_GEN_PROPERTY_FILE;

    public static final String DIA_BTN_GEN_COMMAND = ComposerExecutionMessageConstants.DIA_BTN_GEN_COMMAND;

    public static final String DIA_BTN_COPY_TO_CLIPBOARD = ComposerExecutionMessageConstants.DIA_BTN_COPY_TO_CLIPBOARD;

    public static final String DIA_TITLE_GENERATED_COMMAND = ComposerExecutionMessageConstants.DIA_TITLE_GENERATED_COMMAND;

    public static final String DIA_LBL_GENERATED_COMMAND_MESSAGE = ComposerExecutionMessageConstants.DIA_LBL_GENERATED_COMMAND_MESSAGE;

    public static final String DIA_MSG_PLS_SPECIFY_X = ComposerExecutionMessageConstants.DIA_MSG_PLS_SPECIFY_X;

    public static final String DIA_MSG_PLS_SPECIFY_FILE_LOCATION = ComposerExecutionMessageConstants.DIA_MSG_PLS_SPECIFY_FILE_LOCATION;

    public static final String DIA_LBL_CUSTOM_EXECUTION = ComposerExecutionMessageConstants.DIA_LBL_CUSTOM_EXECUTION;

    public static final String DIA_LBL_RETRY_TEST_SUITE = ComposerExecutionMessageConstants.DIA_LBL_RETRY_TEST_SUITE;

    public static final String DIA_LBL_RETRY_TIMES = ComposerExecutionMessageConstants.DIA_LBL_RETRY_TIMES;

    public static final String DIA_CHK_FOR_FAILED_TEST_CASES = ComposerExecutionMessageConstants.DIA_CHK_FOR_FAILED_TEST_CASES;
    
    public static final String DIA_LBL_KSTORE_API_KEY_USAGE = ComposerExecutionMessageConstants.KSTORE_API_KEY_USAGE;

    // TestSuiteSelectionDialog
    public static final String DIA_TITLE_TEST_SUITE_BROWSER = ComposerExecutionMessageConstants.DIA_TITLE_TEST_SUITE_BROWSER;

    // LaunchDelegate
    public static final String LAUNCH_CONFIGURATION_TYPE_ID = "com.kms.katalon.composer.execution.scriptLaunchConfiguration";

    // ExternalLibratiesSettingPage
    public static final String PAGE_EXTERNAL_LIB_JOB_TASK_REBUILD_PROJECT = ComposerExecutionMessageConstants.PAGE_EXTERNAL_LIB_JOB_TASK_REBUILD_PROJECT;

    public static final String PAGE_EXTERNAL_LIB_MSG_UNABLE_UPDATE_PROJECT = ComposerExecutionMessageConstants.PAGE_EXTERNAL_LIB_MSG_UNABLE_UPDATE_PROJECT;

    public static final String DIA_MSG_SEND_EMAIL_REPORT_FOR_FAILED_TEST_ONLY = ComposerExecutionMessageConstants.DIA_MSG_SEND_EMAIL_REPORT_FOR_FAILED_TEST_ONLY;

    public static final String DIA_MSG_SEND_EMAIL_REPORT_FOR_ALL_CASES = ComposerExecutionMessageConstants.DIA_MSG_SEND_EMAIL_REPORT_FOR_ALL_CASES;

    public static final String DIA_TITLE_UNABLE_TO_OPEN_HAR_FILE = ComposerExecutionMessageConstants.DIA_TITLE_UNABLE_TO_OPEN_HAR_FILE;
}
