package com.kms.katalon.controller.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    // AbstractExportController
    public static final String CTRL_ERROR_MSG_CANNOT_EXPORT_PROJ = "Cannot export project.";

    // AbstractImportController
    public static final String CTRL_ERROR_MSG_CANNOT_IMPORT_PROJ = "Cannot import project.";

    public static final String CTRL_TXT_IMPORTING_PROJ = "Importing project...";

    // ExportProjectController
    public static final String CTRL_TXT_EXPORT_PROJ = "Export project...";

    // ExportTestCaseController
    public static final String CTRL_TXT_EXPORT_TEST_CASE = "Export Test Cases...";

    // TestCaseController
    public static final String CTRL_EXC_TEST_CASE_CANNOT_BE_NULL = "Test Case cannot be null";

    public static final String CTRL_EXC_VAR_ID_CANNOT_BE_NULL = "Variable's ID cannot be null";

    // TestCaseController
    public static final String CTRL_NEW_TEST_CASE = "New Test Case";

    // ObjectRepositoryController
    public static final String CTRL_NEW_TEST_OBJECT = "New Element";

    // WebServiceController
    public static final String CTRL_NEW_WS_REQUEST = "New Request";

    // TestDataController
    public static final String CTRL_NEW_TEST_DATA = "New Test Data";

    public static final String CTRL_EXC_TEST_DATA_IS_NULL = "Test Data is null.";

    public static final String CTRL_EXC_TEST_DATA_IS_NOT_DB_TYPE = "Test Data with ID ''{0}'' is not ''Database Data'' type.";

    // TestSuiteController
    public static final String CTRL_NEW_TEST_SUITE = "New Test Suite";

    // ProjectNotFoundException
    public static final String EXC_MSG_PROJECT_NOT_FOUND = "No current project is found.";

    // CheckpointController
    public static final String CTRL_EXC_CHECKPOINT_IS_NULL = "Checkpoint is null.";

    public static final String CTRL_EXC_SOURCE_URL_IS_NULL = "Source URL is empty.";

    public static final String CTRL_EXC_CANNOT_TAKE_SNAPSHOT_DATA = "Cannot take a snapshot from data source for Checkpoint with ID ''{0}''. Root cause: Unsupport source data type ''{1}''";

    public static final String CTRL_EXC_DB_CONNECTION_SETTINGS_ARE_EMPTY = "Database Connection settings are empty.";

    public static final String CTRL_EXC_EXCEL_SHEET_NAME_IS_EMPTY = "Excel sheet name is null or empty.";

    public static final String CTRL_EXC_INVALID_CSV_SEPARATOR = "Invalid CSV separator.";

}
