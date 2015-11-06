package com.kms.katalon.composer.objectrepository.constant;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    // NewTestObjectDialog
    public static final String DIA_TITLE_TEST_OBJECT = "Test Object";
    public static final String DIA_MSG_CREATE_NEW_TEST_OBJECT = "Create Test Object";

    // DeleteTestObjectHandler
    public static final String ERROR_TITLE = ERROR;
    public static final String HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_OBJ = "Unable to delete Test Object";
    public static final String HAND_DELETE_OBJECT_TASK_NAME = "Deleting {0} ''{1}''...";
    
    // DeleteTestObjectFolderHandler
    public static final String HAND_DELETE_OBJECT_FOLDER_TASK_NAME = "Deleting folder ''{0}''...";
    public static final String HAND_DELETE_OBJECT_SUB_TASK_NAME = "Deleting test object ''{0}''...";
    public static final String HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_OBJ_FOLDER = "Unable to delete Test Object folder";
    
    // NewTestObjectHandler
    public static final String HAND_NEW_TEST_OBJ = "New Test Object";
    public static final String HAND_ERROR_MSG_UNABLE_TO_CREATE_TEST_OBJ = "Unable to create Test Object";

    // RenameTestObjectHandler
    public static final String HAND_ERROR_MSG_UNABLE_TO_RENAME_TEST_OBJECT = "Unable to rename Test Object";

    // SaveTestObjectHandler
    public static final String HAND_ERROR_MSG_UNABLE_TO_SAVE_TEST_OBJ = "Unable to save Test Object";

    // AddToObjectSpyMenuContribution
    public static final String MENU_CONTEXT_ADD_TO_OBJECTSPY = "Add to object spy";

    // NewTestObjectPopupMenuContribution
    public static final String MENU_CONTEXT_NEW_TEST_OBJ = "Test Object";

    // TestObjectPart
    public static final String PA_CONFIRM_TITLE_FILE_CHANGED = "Test Object";
    public static final String PA_CONFIRM_MSG_RELOAD_FILE = "The file ''{0}'' has been changed on the file system. Do you want to reload it?";

    // AddPropertyDialog
    public static final String VIEW_LBL_NAME = NAME;
    public static final String VIEW_LBL_MATCH_COND = "Match condition";
    public static final String VIEW_LBL_VALUE = VALUE;
    public static final String VIEW_LBL_ADD_PROPERTY = "Add property";
    public static final String WARN_TITLE = WARN;
    public static final String VIEW_WARN_MSG_PROPERTY_CANNOT_BE_BLANK = "Property name can not be blank";

    // ObjectPropertyView
    public static final String VIEW_TEST_OBJECT_BROWSE = "Object Repository Browser";
    public static final String VIEW_COL_NAME = NAME;
    public static final String VIEW_COL_MATCH_COND = "Match Condition";
    public static final String VIEW_COL_VALUE = VALUE;
    public static final String VIEW_COL_CHKBOX = "Detect object by?";
    public static final String VIEW_LBL_INFO = INFORMATION;
    public static final String VIEW_LBL_ID = ID;
    public static final String VIEW_LBL_IMAGE = IMAGE;
    public static final String VIEW_LBL_DESC = DESCRIPTION;
    public static final String VIEW_CHKBOX_LBL_USE_RELATIVE_PATH = "Use relative path";
    public static final String VIEW_BTN_BROWSE = BROWSE;
    public static final String VIEW_BTN_TIP_BROWSE = "Browse to select image file";
    public static final String VIEW_LBL_ADD = ADD;
    public static final String VIEW_LBL_DELETE = DELETE;
    public static final String VIEW_LBL_HIGHLIGHT = "Highlight";
    public static final String VIEW_ERROR_MSG_FAILED_TO_LOAD_OBJ_REPOSITORY = "Failed to load Object Repository";
    public static final String VIEW_ERROR_MSG_UNABLE_TO_SAVE_TEST_OBJ = "Unable to save Test Object";
    public static final String VIEW_ERROR_REASON_OBJ_EXISTED = "Object ''{0}'' has already existed";
    public static final String VIEW_ERROR_REASON_OBJ_PROP_EXISTED = "Duplicated test object property name ''{0}''";
    public static final String VIEW_LBL_USE_IFRAME = "Use below object as parent iframe of this object";
    public static final String VIEW_WARN_FILE_NOT_FOUND = "File not found. Please choose another file.";

    // TestObjectReferencesDialog
    public static final String DIA_MSG_HEADER_TEST_OBJECT_REFERENCES = "Test object ''{0}'' has been referred by some test "
            + "objects listed below.\nDo you want to delete all these references?";
    public static final String DIA_FIELD_TEST_OBJECT_ID = "Test Object ID";
}
