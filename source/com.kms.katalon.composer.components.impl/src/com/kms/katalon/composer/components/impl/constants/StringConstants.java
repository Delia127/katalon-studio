package com.kms.katalon.composer.components.impl.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    // AddMailRecipientDialog
    public static final String DIA_LBL_EMAIL = EMAIL + ":";

    public static final String DIA_DESC_PLS_ENTER_EMAIL_ADDR = "Please enter your email address."
            + "You can enter many email addresses by using \";\".\n For example: abcd@gmail.com; xyz@kms-technology.com";

    public static final String DIA_DESC_INVALID_EMAIL_ADDR = "Email is not valid.";

    public static final String DIA_DESC_DUPLICATED_EMAIL_ADDR = "Email(s) {0} has/have already existed in mail recipients.";

    public static final String DIA_ADD_EMAIL_RECIPIENT = "Add email recipient";

    // AbstractEntityDialog
    public static final String DIA_LBL_NAME = NAME;

    public static final String DIA_LBL_CREATE_NEW = "Create new";

    public static final String DIA_WINDOW_TITLE_NEW = "New";

    public static final String DIA_NAME_EXISTED = "Name already exists.";

    // TreeEntitySelectionDialog
    public static final String DIA_SEARCH_TEXT_DEFAULT_VALUE = "Enter text to search...";

    public static final String DIA_IMAGE_SEARCH_TOOLTIP = SEARCH;

    public static final String DIA_IMAGE_CLOSE_SEARCH_TOOLTIP = CLEAR;

    public static final String DIA_KEYWORD_SEARCH_ALL = "all";

    // MultiStatusErrorDialog
    public static final String DIA_TITLE_DETAILS = "Details";

    public static final String DIA_TITLE_REASON = "Reason:";

    // FolderTreeEntity
    public static final String TREE_FOLDER_TYPE_NAME = FOLDER;

    // KeywordTreeEntity
    public static final String TREE_KEYWORD_TYPE_NAME = KEYWORD;

    public static final String TREE_KEYWORD_KW = ENTITY_KW_KEYWORD;

    // PackageTreeEntity
    public static final String TREE_PACKAGE_TYPE_NAME = PACKAGE;

    public static final String TREE_PACKAGE_DEFAULT_LBL = DEFAULT_PACKAGE_NAME;

    // ReportTreeEntity
    public static final String TREE_REPORT_TYPE_NAME = REPORT;

    public static final String TREE_REPORT_KW = ENTITY_KW_REPORT;

    // TestCaseTreeEntity
    public static final String TREE_TEST_CASE_TYPE_NAME = TEST_CASE;

    public static final String TREE_TEST_CASE_KW = ENTITY_KW_TEST_CASE;

    // TestDataTreeEntity
    public static final String TREE_TEST_DATA_TYPE_NAME = TEST_DATA;

    public static final String TREE_TEST_DATA_KW = ENTITY_KW_TEST_DATA;

    public static final String TREE_TEST_DATA_PROP_DATA_TYPE = "Data Type";

    // TestSuiteTreeEntity
    public static final String TREE_TEST_SUITE_TYPE_NAME = TEST_SUITE;

    public static final String TREE_TEST_SUITE_KW = ENTITY_KW_TEST_SUITE;

    // WebElementTreeEntity
    public static final String TREE_OBJECT_TYPE_NAME = OBJECT;

    public static final String TREE_OBJECT_KW = ENTITY_KW_TEST_OBJECT;

    // CheckpointTreeEntity
    public static final String TREE_CHECK_POINT_PROP_TAKEN_DATE = "Taken Date";

    // DatabaseQueryDialog
    public static final String DIA_TITLE_DB_CONNECTION_QUERY_SETTINGS = "Database Connection and Query Settings";

    public static final String DIA_GRP_DATABASE_CONNECTION = "Database Connection";

    public static final String DIA_CHK_USE_GLOBAL_DB_SETTINGS = "Use global database connection settings";

    public static final String DIA_CHK_SECURE_USER_PASSWORD = "Secure User and Password";

    public static final String DIA_LBL_USER = "User";

    public static final String DIA_LBL_PASSWORD = "Password";

    public static final String DIA_LBL_CONNECTION_URL = "Connection URL";

    public static final String DIA_LBL_SQL_QUERY = "SQL Query";

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

}
