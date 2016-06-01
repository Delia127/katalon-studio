package com.kms.katalon.composer.testdata.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    // DeleteTestDataHandler
    public static final String ERROR_TITLE = ERROR;

    public static final String HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_DATA = "Unable to delete Test Data.";

    public static final String HAND_JOB_DELETING_ENTITY_W_TYPE_NAME = "Deleting {0} ''{1}''...";

    public static final String HAND_JOB_DELETING_ENTITY = "Deleting ''{0}''...";

    public static final String HAND_JOB_DELETING_FOLDER = "Deleting folder: ''{0}''...";

    // NewTestDataHandler
    public static final String HAND_NEW_TEST_DATA = "New Test Data";

    public static final String HAND_ERROR_MSG_UNABLE_TO_CREATE_TEST_DATA = "Unable to create Test Data.";

    // NewTestDataHandler
    public static final String HAND_ERROR_MSG_UNABLE_TO_RENAME_TEST_DATA = "Unable to rename Test Data.";

    // NewTestDataHandler
    public static final String MENU_CONTEXT_TEST_DATA = TEST_DATA;

    public static final String COMMAND_ID_NEW_TEST_DATA = "com.kms.katalon.composer.testdata.command.add";

    // MainTestDataPart
    public static final String PA_TOOLTIP_WARNING_COLUMN_HEADER = "This column has empty header";

    public static final String PA_LBL_WARNING_COLUMN_HEADER = "Warning: {0}/{1} empty column header(s)";

    // CSVTestDataPart
    public static final String PA_LBL_FILE_INFO = "File's Information";

    public static final String PA_LBL_FILE_NAME = "File Name";

    public static final String PA_BTN_BROWSE = BROWSE;

    public static final String PA_LBL_SEPARATOR = "Separator";

    public static final String PA_CHKBOX_USE_FIRST_ROW_AS_HEADER = "Use first row as header";

    public static final String PA_CHKBOX_USE_RELATIVE_PATH = "Use relative path";

    public static final String PA_ERROR_MSG_UNABLE_TO_SAVE_TEST_DATA = "Unable to save Test Data.";

    // ExcelTestDataPart
    public static final String PA_LBL_SHEET_NAME = "Sheet Name";

    public static final String WARN_TITLE = WARN;

    public static final String PA_WARN_MSG_SHEET_NOT_FOUND = "Sheet ''{0}'' not found. Please choose another one.";

    public static final String PA_WARN_MSG_UNABLE_TO_LOAD_SHEET_NAME = "Unable to load sheet names.";

    public static final String PA_WARN_MSG_UNABLE_TO_LOAD_SOURCE_FILE = "Unable to load selected file.";

    // InternalTestDataPart
    public static final String PA_MENU_CONTEXT_INSERT_COL = "Insert column";

    public static final String PA_MENU_CONTEXT_INSERT_COL_TO_THE_LEFT = "To the left";

    public static final String PA_MENU_CONTEXT_INSERT_COL_TO_THE_RIGHT = "To the right";

    public static final String PA_MENU_CONTEXT_RENAME_COL = "Rename column";

    public static final String PA_MENU_CONTEXT_INSERT_ROW = "Insert row";

    public static final String PA_MENU_CONTEXT_DEL = "Delete";

    public static final String PA_MENU_CONTEXT_DEL_COL = "Column";

    public static final String PA_MENU_CONTEXT_DEL_ROWS = "Row(s)";

    public static final String PA_COL_NO = NO_;

    public static final String PA_TOOL_TIP_ADD_COLUMN = "Add column";

    // TestDataMainPart
    public static final String PA_LBL_GENERAL_INFO = "General Information";

    public static final String PA_LBL_ID = ID;

    public static final String PA_LBL_NAME = NAME;

    public static final String PA_LBL_DESCRIPTION = DESCRIPTION;

    public static final String PA_LBL_DATA_TYPE = "Data Type";

    public static final String PA_CONFIRM_TITLE_FILE_CHANGED = "File changed";

    public static final String PA_CONFIRM_MSG_RELOAD_FILE = "The file ''{0}'' has been changed on the file system. Do "
            + "you want to reload it?";

    public static final String PA_ERROR_MSG_FILE_X_IS_WRONG_FORMAT_AT_LINE_Y = "The file ''{0}'' is wrong format at line"
            + " number [{1}]";

    public static final String PA_FILE_TOO_LARGE = "We only display first {0} columns for you because it''s not a good "
            + "practice to have so many columns in the current sheet. Please break down your data into smaller ones for "
            + "better execution and displaying.";

    // NewTestDataColumnDialog
    public static final String VIEW_COL_COL_NAME = "Column Name";

    public static final String VIEW_COL_DATA_TYPE = "Data Type";

    public static final String VIEW_COMBO_STRING = "String";

    public static final String VIEW_COMBO_NUMBER = "Number";

    public static final String VIEW_SHELL_DATA_COL_DEFINITION = "Data Column Definition";

    public static final String VIEW_INFO_TITLE_INVALID_DATA = "Data Column Definition";

    public static final String VIEW_INFO_MSG_ENTER_COL_NAME = "Please give a column name";

    public static final String VIEW_WARN_MSG_DUPLICATED_NAME = "Name duplication";

    public static final String VIEW_WARN_MSG_NAME_IS_EMPTY = "Name cannot be empty";

    // NewTestDataDialog
    public static final String VIEW_LBL_NAME = NAME;

    public static final String VIEW_LBL_DATA_TYPE = "Data Type";

    public static final String VIEW_TITLE_TEST_DATA = TEST_DATA;

    public static final String VIEW_MSG_CREATE_NEW_TEST_DATA = "Create Test Data";

    public static final String VIEW_WINDOW_TITLE_NEW = "New";

    // LoadExcelFileJob
    public static final String JOB_LOAD_EXCL_TITLE = "Load excel file";

    public static final String JOB_LOAD_EXCL_TASK_NAME = "Loading excel file...";

    // TestDataReferencesDialog
    public static final String DIA_TITLE_TEST_DATA_REFERENCES = "Test Data's References";

    public static final String DIA_LBL_REFERENCED_TEST_CASES = "Test Cases";

    public static final String DIA_COL_REFERENCED_BY = "Referenced by";

    public static final String DIA_COL_TEST_CASE_ID = "Test Case ID";

    // TestDataPropertiesDialog
    public static final String DIA_TITLE_TEST_DATA_PROPERTIES = "Test Data Properties";

    public static final String DIA_LBL_DATA_TYPE = "Data Type";

    // DBTestDataPart EditTestDataQueryDialog
    public static final String DIA_LBL_SQL_QUERY = "SQL Query";

    public static final String DIA_BTN_EDIT_QUERY = "Edit Query";

    public static final String DIA_BTN_FETCH_DATA = "Fetch Data";

    public static final String DIA_LBL_STATUS_PARTIALLY_LOADED_ON = "Partially loaded on {0}";

    public static final String DIA_MSG_DATA_IS_TOO_LARGE_FOR_PREVIEW = "The responsive data from database is too large to display. The maximum data can be displayed is {0} for column and {1} for row.";

    public static final String DIA_LBL_STATUS_LOADED_ON = "Loaded on {0}";

    public static final String DIA_MSG_CANNOT_FETCH_DATA = "Cannot fetch data from database";

    // EditTestDataQueryDialog
    public static final String DIA_TITLE_DB_CONNECTION_QUERY_SETTINGS = "Database Connection and Query Settings";

    public static final String DIA_GRP_DATABASE_CONNECTION = "Database Connection";

    public static final String DIA_CHK_USE_GLOBAL_DB_SETTINGS = "Use global database connection settings";

    public static final String DIA_CHK_SECURE_USER_PASSWORD = "Secure User and Password";

    public static final String DIA_LBL_USER = "User";

    public static final String DIA_LBL_PASSWORD = "Password";

    public static final String DIA_LBL_CONNECTION_URL = "Connection URL";

    public static final String DIA_BTN_TEST_CONNECTION = "Test Connection";

    public static final String DIA_LBL_CONNECTION_URL_SAMPLE = "Connection URL Sample";

    public static final String DIA_LNK_MYSQL = "MySQL";

    public static final String DIA_LNK_SQL_SERVER = "SQL Server";

    public static final String DIA_LNK_ORACLE_SQL = "Oracle SQL";

    public static final String DIA_LNK_POSTGRESQL = "PostgreSQL";

    public static final String DIA_LBL_CONNECTION_CLOSED = "Connection is closed.";

    public static final String DIA_MSG_CONNECTION_EMPTY = "Database Connection settings are empty.";

    public static final String DIA_LBL_TEST_STATUS_FAIL = "Connection failed! {0}";

    public static final String DIA_LBL_TEST_STATUS_SUCCESS = "Connection successful!";

    public static final String DIA_TITLE_STATUS_DETAILS = "Status Details";

    public static final String DIA_LINK_MYSQL_DOC = "https://dev.mysql.com/doc/connectors/en/connector-j-reference-configuration-properties.html";

    public static final String DIA_LINK_SQL_SERVER_DOC = "https://msdn.microsoft.com/en-us/library/ms378672(v=sql.110).aspx";

    public static final String DIA_LINK_ORACLE_SQL_DOC = "http://docs.oracle.com/database/121/JJDBC/urls.htm";

    public static final String DIA_LINK_POSTGRESQL_DOC = "https://jdbc.postgresql.org/documentation/head/connect.html";

    public static final String DIA_TXT_MYSQL_SAMPLE_LINK = "jdbc:mysql://localhost:3306/DB_NAME";

    public static final String DIA_TXT_SQL_SERVER_SAMPLE_LINK = "jdbc:sqlserver://localhost:1433;databaseName=DB_NAME";

    public static final String DIA_TXT_ORACLE_SQL_SAMPLE_THIN_LINK = "jdbc:oracle:thin:@//localhost:1521/SERVICE_NAME";

    public static final String DIA_TXT_ORACLE_SQL_SAMPLE_OCI_LINK = "jdbc:oracle:oci:@//localhost:1521:SID";

    public static final String DIA_TXT_POSTGRESQL_SAMPLE_LINK = "jdbc:postgresql://localhost:5432/DB_NAME?currentSchema=SCHEMA";

    // DatabasePreferencePage
    public static final String DIA_MSG_UNABLE_TO_SAVE_DB_SETTING_PAGE = "Unable to save database settings.";

    public static final String DIA_DB_SETTING_COMMENT = "Global Database Settings.\n@URL database connection URL.\n@SECURE_USER_ACCOUNT true if exclude USER & PASSWORD from URL. Otherwise, false.\n@USER username to access database.\n@PASSWORD encrypted password.";

}
