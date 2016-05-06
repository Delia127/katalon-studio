package com.kms.katalon.core.constants;

import java.io.File;

public class StringConstants {
    // KeywordLogger
    public static final String LOG_START_SUITE = "Start Test Suite";

    public static final String LOG_START_SUITE_METHOD = "startSuite";

    public static final String LOG_START_TEST = "Start Test Case";

    public static final String LOG_START_TEST_METHOD = "startTest";

    public static final String LOG_START_KEYWORD = "Start action";

    public static final String LOG_START_KEYWORD_METHOD = "startKeyword";

    public static final String LOG_END_SUITE = "End Test Suite";

    public static final String LOG_END_SUITE_METHOD = "endSuite";

    public static final String LOG_END_TEST = "End Test Case";

    public static final String LOG_END_TEST_METHOD = "endTest";

    public static final String LOG_END_KEYWORD = "End action";

    public static final String LOG_END_KEYWORD_METHOD = "endKeyword";

    // LogLevel
    public static final String LOG_LVL_PASSED = "PASSED";

    public static final String LOG_LVL_ERROR = "ERROR";

    public static final String LOG_LVL_INFO = "INFO";

    public static final String LOG_LVL_WARNING = "WARNING";

    public static final String LOG_LVL_FAILED = "FAILED";

    public static final String LOG_LVL_ABORTED = "ABORTED";

    public static final String LOG_LVL_START = "START";

    public static final String LOG_LVL_END = "END";

    public static final String LOG_LVL_INCOMPLETE = "INCOMPLETE";

    public static final String LOG_LVL_RUN_DATA = "RUN_DATA";

    // TestCaseMain
    public static final String MAIN_LOG_ERROR_UNKNOWN_PROP_OF_TEST_CASE = "System doesn''t know property: {0} of Test Case: {1}";

    public static final String MAIN_LOG_PASSED_METHOD_COMPLETED = "Method ''{0}'' complete successfully";

    public static final String MAIN_LOG_WARNING_ERROR_OCCURRED_WHEN_RUN_METHOD = "Error occurred when try to run method ''{0}'' (Root cause: {1} - {2})";

    public static final String MAIN_MSG_START_RUNNING_SETUP_METHODS_FOR_TC = "Start running set up methods for Test Case";

    public static final String MAIN_MSG_START_RUNNING_TEAR_DOWN_METHODS_FOR_PASSED_TC = "Start running tear down methods for passed Test Case";

    public static final String MAIN_MSG_START_RUNNING_TEAR_DOWN_METHODS_FOR_TC = "Start running tear down methods for Test Case";

    public static final String MAIN_MSG_START_RUNNING_TEAR_DOWN_METHODS_FOR_FAILED_TC = "Start running tear down methods for failed Test Case";

    public static final String MAIN_MSG_START_RUNNING_TEAR_DOWN_METHODS_FOR_ERROR_TC = "Start running tear down methods for error Test Case";

    public static final String MAIN_LOG_MSG_FAILED_BECAUSE_OF = "{0} FAILED because (of) {1}";

    public static final String MAIN_LOG_MSG_ERROR_BECAUSE_OF = "{0} has ERROR(s) because (of) {1}";

    public static final String MAIN_LOG_INFO_START_EVALUATE_VARIABLE = "Evaluating variables for test case";

    public static final String MAIN_LOG_INFO_VARIABLE_NAME_X_IS_SET_TO_Y = "Variable ''{0}'' is set to {1}";

    public static final String MAIN_LOG_INFO_VARIABLE_NAME_X_IS_SET_TO_Y_AS_DEFAULT = "Variable ''{0}'' is set to {1} as default";

    public static final String MAIN_LOG_MSG_SET_TEST_VARIABLE_ERROR_BECAUSE_OF = "Unable to set variable ''{0}'' for test case because (of) {1}";

    // TestData
    public static final String TD_ROW_INDEX_X_FOR_TEST_DATA_Y_INVALID = "Invalid row index {0} for test data ''{1}''. Row index must be between 1..{2}";

    public static final String TD_COLUMN_INDEX_X_FOR_TEST_DATA_Y_INVALID = "Invalid column index {0} for test data ''{1}''. Column index must be between 1..{2}";

    public static final String TD_COLUMN_NAME_X_FOR_TEST_DATA_Y_INVALID = "Invalid column name ''{0}'' for test data ''{1}''. Possible values are ''{2}''";

    // AppPOI
    public static final String UTIL_EXC_FILE_IS_UNSUPPORTED = "File is unsupported: {0}";

    public static final String UTIL_EXC_FILE_IS_NOT_HTML_FORMAT = "File is not in HTML format.";

    public static final String UTIL_EXC_FILE_NOT_FOUND = "File: {0} not found.";

    // ShettPOI
    public static final String EXCEL_INVALID_ROW_NUMBER = "Invalid row index: {0}. Posible index cannot be greater than {1}.";

    public static final String EXCEL_INVALID_COL_NUMBER = "Invalid column index: {0}. Posible index cannot be greater than {1}.";

    // ObjectRepository
    public static final String TO_LOG_WARNING_TEST_OBJ_NULL = "Test object id is null";

    public static final String TO_LOG_INFO_FINDING_TEST_OBJ_W_ID = "Finding Test Object with id ''{0}''";

    public static final String TO_LOG_WARNING_TEST_OBJ_DOES_NOT_EXIST = "Test object with id ''{0}'' does not exist";

    public static final String TO_LOG_WARNING_CANNOT_GET_TEST_OBJECT_X_BECAUSE_OF_Y = "Cannot find test object with id ''{0}'' because of ''{1}''";

    // BuiltinKeywords
    public static final String KW_LOG_INFO_MATCHING_ACTUAL_TXT_W_EXPECTED_TXT = "Matching actual text ''{0}'' with expected text ''{1}''{2}";

    public static final String KW_LOG_PASSED_ACTUAL_TXT_MATCHED_EXPECTED_TXT = "Actual text ''{0}'' and expected text ''{1}'' are matched {2}";

    public static final String KW_MSG_ACTUAL_TXT_NOT_MATCHED_EXPECTED_TXT = "Actual text ''{0}'' and expected text ''{1}'' are not matched{2}";

    public static final String KW_MSG_CANNOT_VERIFY_MATCHING_BETWEEN_TXTS = "Unable to verify match between actual text ''{0}'' and expected text ''{1}''{2}";

    public static final String KW_LOG_INFO_MATCHING_ACTUAL_TXT_W_EXPECTED_VAL = "Matching actual text ''{0}'' with expected value ''{1}''{2}";

    public static final String KW_MSG_TXTS_MATCHED_BUT_EXPECTED_UNMATCHED = "Actual text ''{0}'' and expected text ''{1}'' are matched {2}, but expected not to be matched";

    public static final String KW_LOG_PASSED_TXTS_UNMATCHED = "Actual text ''{0}'' and expected text ''{1}'' are not matched {2}";

    public static final String KW_MSG_CANNOT_VERIFY_TXTS_ARE_UNMATCHED = "Unable to verify not match between actual text ''{0}'' and expected text ''{1}'' {2}";

    public static final String KW_MSG_OBJECTS_ARE_NOT_EQUAL = "Actual object ''{0}'' and expected object ''{1}'' are not equal";

    public static final String KW_LOG_PASSED_OBJECTS_ARE_EQUAL = "Actual object ''{0}'' and expected object ''{1}'' are equal";

    public static final String KW_MSG_CANNOT_VERIFY_OBJECTS_ARE_EQUAL = "Unable to verify equal between actual object ''{0}'' and expected object ''{1}''";

    public static final String KW_MSG_OBJECTS_ARE_EQUAL = "Actual object ''{0}'' and expected object ''{1}'' are equal";

    public static final String KW_LOG_PASSED_OBJECTS_ARE_NOT_EQUAL = "Actual object ''{0}'' and expected object ''{1}'' are not equal";

    public static final String KW_MSG_CANNOT_VERIFY_OBJECTS_ARE_NOT_EQUAL = "Unable to verify not equal between actual object ''{0}'' and expected object ''{1}''";

    public static final String KW_LOG_PASSED_ACTUAL_NUM_IS_GREATER_THAN_EXPECTED_NUM = "Actual number ''{0}'' is greater than expected number ''{1}''";

    public static final String KW_MSG_ACTUAL_NUM_IS_NOT_GREATER_THAN_EXPECTED_NUM = "Actual number ''{0}'' is not greater than expected number ''{1}''";

    public static final String KW_MSG_CANNOT_VERIFY_WHICH_NUM_IS_GREATER = "Unable to verify greater between actual number ''{0}'' and expected number ''{1}'''";

    public static final String KW_LOG_PASSED_ACTUAL_NUM_IS_GT_OR_EQ_TO_EXPECTED_NUM = "Actual number ''{0}'' is greater than or equal to expected number ''{1}''";

    public static final String KW_MSG_ACTUAL_NUM_IS_NOT_GT_OR_EQ_TO_EXPECTED_NUM = "Actual number ''{0}'' is NOT greater than or equal to expected number ''{1}''";

    public static final String KW_MSG_CANNOT_VERIFY_NUMS_ARE_GT_OR_EQ = "Unable to verify actual number ''{0}'' greater than or equal with expected number ''{1}''";

    public static final String KW_LOG_PASSED_ACTUAL_NUM_IS_LT_EXPECTED_NUM = "Actual number ''{0}'' is less than expected number ''{1}''";

    public static final String KW_MSG_ACTUAL_NUM_IS_NOT_LT_EXPECTED_NUM = "Actual number ''{0}'' is NOT less than expected number ''{1}''";

    public static final String KW_MSG_CANNOT_VERIFY_WHICH_NUM_IS_LT = "Unable to verify actual number ''{0}'' less than expected number ''{1}''";

    public static final String KW_LOG_PASSED_ACTUAL_NUM_IS_LT_OR_EQ_TO_EXPECTED_NUM = "Actual number ''{0}'' is less than or equal to expected number ''{1}''";

    public static final String KW_MSG_ACTUAL_NUM_IS_NOT_LT_OR_EQ_EXPECTED_NUM = "Actual number ''{0}'' is NOT less than or equal to expected number ''{1}''";

    public static final String KW_MSG_CANNOT_VERIFY_WHICH_NUM_IS_LT_OR_EQ_TO = "Unable to verify actual number ''{0}'' less than or equal to expected number ''{1}''";

    public static final String KW_LOG_INFO_CHECING_STRINGS_PARAM = "Checking strings parameter";

    public static final String KW_EXC_STRS_PARAM_IS_NULL = "Strings parameter is null";

    public static final String KW_LOG_INFO_CONCAT_STR_ARRAY = "Concatenating string array: ''{0}''";

    public static final String KW_LOG_PASSED_CONCAT_STR_ARRAY = "Concatenated string array: ''{0}'' into ''{1}''";

    public static final String KW_CANNOT_CONCAT_STR_ARRAY = "Unable to concatenate string array: ''{0}''";

    public static final String KW_CANNOT_CONCAT = "Unable to concatenate";

    public static final String KW_LOG_INFO_CHECKING_CALLED_TC = "Checking called Test Case";

    public static final String KW_EXC_CALLED_TC_IS_NULL = "Called Test Case is null";

    public static final String KW_LOG_INFO_STARTING_TO_CALL_TC = "Starting to call Test Case ''{0}''";

    public static final String KW_MSG_CALL_TC_FAILED = "Call Test Case ''{0}'' failed";

    public static final String KW_MSG_CALL_TC_X_FAILED_BECAUSE_OF_ERROR = "Call Test Case ''{0}'' failed because of error(s)";

    public static final String KW_LOG_PASSED_CALL_TC_X_SUCCESSFULLY = "Call Test Case ''{0}'' sucessfully";

    public static final String KW_MSG_CANNOT_CALL_TC_W_ID_X = "Unable to call Test Case with id ''{0}''";

    public static final String KW_MSG_CANNOT_CALL_TC = "Unable to call Test Case";

    public static final String KW_LOG_INFO_DELAYING_BROWSER_IN_SEC = "Delaying browser in {0} second(s)";

    public static final String KW_LOG_PASSED_DELAYED_SEC = "Delayed {0} second(s)";

    public static final String KW_MSG_CANNOT_DELAY_BROWSER = "Unable to delay browser";

    public static final String KW_CATEGORIZE_NUMBER = "Number";

    public static final String KW_CATEGORIZE_TEXT = "Text";

    public static final String KW_CATEGORIZE_UTILITIES = "Utilities";

    // KeywordHelper
    public static final String COMM_LOG_INFO_CHECKING_OBJ = "Checking object";

    public static final String COMM_EXC_OBJ_IS_NULL = "Object is null";

    public static final String COMM_LOG_INFO_CHECKING_TIMEOUT = "Checking timeout";

    public static final String COMM_LOG_WARNING_INVALID_TIMEOUT = "Timeout ''{0}'' is invalid. Using default page load timeout: ''{1}''";

    public static final String COMM_EXC_ACTUAL_NUM_IS_NULL = "Actual number is null";

    public static final String COMM_EXC_EXPECTED_NUM_IS_NULL = "Expected number is null";

    public static final String COMM_EXC_INVALID_ACTUAL_NUM = "Actual number with value ''{0}'' is not a valid number";

    public static final String COMM_EXC_INVALID_EXPECTED_NUM = "Expected number with value ''{0}'' is not a valid number";

    public static final String COMM_LOG_INFO_COMPARE_ACTUAL_W_EXPECTED = "Comparing actual object ''{0}'' with expected object ''{1}''";

    public static final String COMM_LOG_INFO_CONVERTING_RANGE_PARAM_TO_INDEX_ARRAY = "Converting range parameter ''{0}'' to index array";

    public static final String COMM_EXC_INVALID_RANGE = "Range ''{0}'' is invalid ({1})";

    public static final String COMM_LOG_INFO_RANGE_PARAM_IS_CONVERTED_TO_INDEX_ARRAY = "Range parameter ''{0}'' is converted to index array {1}";

    public static final String COMM_LOG_INFO_CHECKING_NUM_PARAMS = "Checking number parameters";

    // XML LOG
    public static final String XML_LOG_DESCRIPTION_PROPERTY = "description";

    public static final String XML_LOG_ATTACHMENT_PROPERTY = "attachment";

    public static final String XML_LOG_NAME_PROPERTY = "name";

    public static final String XML_LOG_ID_PROPERTY = "id";

    public static final String XML_LOG_SOURCE_PROPERTY = "source";

    public static final String XML_LOG_BROWSER_TYPE_PROPERTY = "browserType";

    public static final String XML_LOG_DEVICE_ID_PROPERTY = "deviceId";

    public static final String XML_LOG_DEVICE_PLATFORM_PROPERTY = "devicePlatform";

    public static final String XML_LOG_START_LINE_PROPERTY = "startLine";

    public static final String XML_LOG_STEP_INDEX = "stepIndex";

    public static final String XML_LOG_OS_PROPERTY = "os";

    public static final String XML_LOG_HOST_NAME_PROPERTY = "hostName";

    public static final String XML_LOG_HOST_ADDRESS_PROPERTY = "hostAddress";

    // CustomKeywords
    public static final String GENERATED_SCRIPT_FOLDER = "Libs";

    public static final String SCRIPT_FILE_EXT = "groovy";

    public static final String CUSTOM_KEYWORD_FOLDER_NAME = "Keywords";

    public static final String CUSTOM_KEYWORD_CLASS_NAME = "CustomKeywords";

    public static final String GLOBAL_VARIABLE_CLASS_NAME = "GlobalVariable";

    public static final String GLOBAL_VARIABLE_FILE_NAME = GENERATED_SCRIPT_FOLDER + File.separator + "GlobalVariable."
            + SCRIPT_FILE_EXT;

    // TestDataFactory
    public static final String XML_LOG_TEST_DATA_CHECKING_TEST_DATA_ID = "Checking test data id";

    public static final String XML_LOG_TEST_DATA_FINDING_TEST_DATA_WITH_ID_X = "Finding test data with id ''{0}''";

    public static final String XML_LOG_ERROR_TEST_DATA_NULL_TEST_DATA_ID = "Test data id is null";

    public static final String XML_LOG_ERROR_TEST_DATA_X_NOT_EXISTS = "Test data with id ''{0}'' does not exist";

    public static final String XML_LOG_ERROR_TEST_DATA_CANNOT_FIND_TEST_DATA_X_BECAUSE_OF_Y = "Cannot find test data with id ''{0}'' because (of) ''{1}''";

    public static final String XML_LOG_ERROR_TEST_DATA_MISSING_ELEMENT = "Test data missing ''{0}'' element";

    public static final String XML_LOG_TEST_DATA_READING_EXCEL_DATA = "Test data is excel file, reading excel file";

    public static final String XML_LOG_TEST_DATA_READING_INTERNAL_DATA = "Test data is internal data, reading internal data";

    public static final String XML_LOG_TEST_DATA_READING_CSV_DATA = "Test data is csv file, reading csv file";

    public static final String XML_LOG_TEST_DATA_READING_EXCEL_DATA_WITH_SOURCE_X_SHEET_Y = "Reading excel file with source file ''{0}'' and sheet name ''{1}''";

    public static final String XML_LOG_TEST_DATA_READING_CSV_DATA_WITH_SOURCE_X_SEPERATOR_Y_AND_Z = "Reading csv file with source file ''{0}'', seperator ''{1}'' and ''{2}''";

    // ExcelData
    public static final String XML_LOG_ERROR_SHEET_NAME_X_NOT_EXISTS = "Sheet with name ''{0}'' does not exists";

    // RunConfiguration
    public static final String CONF_PROPERTY_WEBUI_DRIVER = "WebUI";

    public static final String CONF_PROPERTY_MOBILE_DRIVER = "Mobile";

    public static final String CONF_PROPERTY_LOG_FILE_PATH = "logFilePath";

    public static final String CONF_APPIUM_LOG_FILE = "appiumLogFile";
    
    public static final String CONF_APPIUM_DIRECTORY = "appiumDirectory";

    public static final String CONF_PROPERTY_TIMEOUT = "timeout";

    public static final String CONF_PROPERTY_PROJECT_DIR = "projectDir";

    public static final String CONF_PROPERTY_HOST = "host";

    public static final String CONF_PROPERTY_HOST_NAME = XML_LOG_HOST_NAME_PROPERTY;

    public static final String CONF_PROPERTY_HOST_OS = XML_LOG_OS_PROPERTY;

    public static final String CONF_PROPERTY_HOST_ADDRESS = XML_LOG_HOST_ADDRESS_PROPERTY;

    public static final String CONF_PROPERTY_HOST_PORT = "hostPort";

    public static final String CONF_PROPERTY_GENERAL = "general";

    public static final String CONF_PROPERTY_DRIVER = "drivers";

    public static final String CONF_PROPERTY_EXEC = "execution";

    public static final String CONF_PROPERTY_EXECUTION_SOURCE = XML_LOG_SOURCE_PROPERTY;

    public static final String CONF_PROPERTY_EXECUTION_SOURCE_NAME = XML_LOG_NAME_PROPERTY;

    public static final String CONF_PROPERTY_EXECUTION_SOURCE_ID = XML_LOG_ID_PROPERTY;

    public static final String CONF_PROPERTY_EXECUTION_SOURCE_DESCRIPTION = XML_LOG_DESCRIPTION_PROPERTY;

    public static final String CONF_PROPERTY_EXECUTION_PREFS_PROPERTY = "preferences";

    public static final String CONF_PROPERTY_EXECUTION_SYSTEM_PROPERTY = "system";

    // TestCaseExecutor
    public static final String TEST_STEP_TRANSFORMATION_CLASS = "com.kms.katalon.core.ast.RequireAstTestStepTransformation";

    public static final String NULL_AS_STRING = "null";

    // alias of GlobalStringConstants.APP_INFO_FILE_LOCATION
    public static final String APP_INFO_FILE_LOCATION = System.getProperty("user.home") + File.separator + ".katalon"
            + File.separator + "application.properties";
}
