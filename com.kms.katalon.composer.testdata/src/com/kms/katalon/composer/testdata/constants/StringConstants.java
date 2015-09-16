package com.kms.katalon.composer.testdata.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    // DeleteTestDataHandler
    public static final String ERROR_TITLE = ERROR;
    public static final String HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_DATA = "Unable to delete Test Data.";

    // NewTestDataHandler
    public static final String HAND_NEW_TEST_DATA = "New Test Data";
    public static final String HAND_ERROR_MSG_UNABLE_TO_CREATE_TEST_DATA = "Unable to create Test Data.";

    // NewTestDataHandler
    public static final String HAND_ERROR_MSG_UNABLE_TO_RENAME_TEST_DATA = "Unable to rename Test Data.";

    // NewTestDataHandler
    public static final String MENU_CONTEXT_TEST_DATA = TEST_DATA;
    public static final String COMMAND_ID_NEW_TEST_DATA = "com.kms.katalon.composer.testdata.command.add";

    // CSVTestDataPart
    public static final String PA_LBL_FILE_INFO = "File's Information";
    public static final String PA_LBL_FILE_NAME = "File Name";
    public static final String PA_BTN_BROWSE = BROWSE;
    public static final String PA_LBL_SEPARATOR = "Separator";
    public static final String PA_CHKBOX_USE_FIRST_ROW_AS_HEADER = "Use first row as header";
    public static final String PA_CHKBOX_USE_RELATIVE_PATH = "Use relative path";
    public static final String PA_ERROR_MSG_UNABLE_TO_SAVE_TEST_DATA = "Unable to save Test Data.";
    public static final String PA_ERROR_REASON_TEST_DATA_EXISTED = "Test data ''{0}'' has already existed.";

    // ExcelTestDataPart
    public static final String PA_LBL_SHEET_NAME = "Sheet Name";
    public static final String WARN_TITLE = WARN;
    public static final String PA_WARN_MSG_UNABLE_TO_LOAD_SHEET_NAME = "Unable to load sheet names.";
    public static final String PA_WARN_MSG_UNABLE_TO_LOAD_SOURCE_FILE = "Unable to load selected file.";

    // InternalTestDataPart
    public static final String PA_MENU_CONTEXT_INSERT_COL_TO_THE_LEFT = "Insert column to the left";
    public static final String PA_MENU_CONTEXT_INSERT_COL_TO_THE_RIGHT = "Insert column to the right";
    public static final String PA_MENU_CONTEXT_RENAME_COL = "Rename column";
    public static final String PA_MENU_CONTEXT_DEL_COL = "Delete column";
    public static final String PA_MENU_CONTEXT_INSERT_ROW = "Insert row";
    public static final String PA_MENU_CONTEXT_DEL_ROWS = "Delete row(s)";
    public static final String PA_COL_NO = NO_;
    public static final String PA_TOOL_TIP_ADD_COLUMN = "Add column";

    // TestDataMainPart
    public static final String PA_LBL_GENERAL_INFO = "General Information";
    public static final String PA_LBL_ID = ID;
    public static final String PA_LBL_NAME = NAME;
    public static final String PA_LBL_DESCRIPTION = DESCRIPTION;
    public static final String PA_LBL_DATA_TYPE = "Data Type";
    public static final String PA_CONFIRM_TITLE_FILE_CHANGED = "File changed";
    public static final String PA_CONFIRM_MSG_RELOAD_FILE = "The file ''{0}'' has been changed on the file system. Do you want to reload it?";
    public static final String PA_ERROR_MSG_FILE_X_IS_WRONG_FORMAT_AT_LINE_Y = "The file ''{0}'' is wrong format at line number [{1}]";

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
}
