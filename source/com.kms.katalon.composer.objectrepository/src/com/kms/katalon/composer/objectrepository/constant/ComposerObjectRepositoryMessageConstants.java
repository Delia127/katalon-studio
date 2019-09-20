package com.kms.katalon.composer.objectrepository.constant;

import org.eclipse.osgi.util.NLS;

public class ComposerObjectRepositoryMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.objectrepository.constant.composerObjectRepositoryMessages";

    public static String DIA_TITLE_TEST_OBJECT;

    public static String DIA_MSG_CREATE_NEW_TEST_OBJECT;

    public static String HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_OBJ;

    public static String HAND_DELETE_OBJECT_TASK_NAME;

    public static String HAND_DELETE_OBJECT_FOLDER_TASK_NAME;

    public static String HAND_DELETE_OBJECT_SUB_TASK_NAME;

    public static String HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_OBJ_FOLDER;

    public static String HAND_NEW_TEST_OBJ;

    public static String HAND_ERROR_MSG_UNABLE_TO_CREATE_TEST_OBJ;

    public static String HAND_ERROR_MSG_UNABLE_TO_RENAME_TEST_OBJECT;

    public static String HAND_ERROR_MSG_UNABLE_TO_SAVE_TEST_OBJ;
    
    public static String HAND_ERROR_MSG_TEST_OBJECT_NOT_EXIST;

    public static String MENU_CONTEXT_ADD_TO_OBJECTSPY;

    public static String MENU_CONTEXT_NEW_TEST_OBJ;

    public static String PA_CONFIRM_TITLE_FILE_CHANGED;

    public static String PA_CONFIRM_MSG_RELOAD_FILE;

    public static String VIEW_TEST_OBJECT_BROWSE;

    public static String VIEW_COL_MATCH_COND;

    public static String VIEW_COL_CHKBOX;

    public static String VIEW_CHKBOX_LBL_USE_RELATIVE_PATH;

    public static String VIEW_BTN_TIP_BROWSE;

    public static String VIEW_LBL_HIGHLIGHT;

    public static String VIEW_ERROR_MSG_FAILED_TO_LOAD_OBJ_REPOSITORY;

    public static String VIEW_ERROR_MSG_UNABLE_TO_SAVE_TEST_OBJ;

    public static String VIEW_ERROR_REASON_OBJ_EXISTED;

    public static String VIEW_ERROR_REASON_OBJ_PROP_EXISTED;

    public static String VIEW_LBL_USE_IFRAME;

    public static String VIEW_WARN_FILE_NOT_FOUND;

    public static String VIEW_LBL_SETTINGS;

    public static String VIEW_LBL_OBJ_PROPERTIES;
    
    public static String VIEW_LBL_OBJ_XPATHS;

    public static String VIEW_TITLE_TEST_OBJ_PROPERTIES;

    public static String DIA_MSG_HEADER_TEST_OBJECT_REFERENCES;

    public static String DIA_FIELD_TEST_OBJECT_ID;

    public static String DIA_TITLE_TEST_OBJECT_REFERENCES;

    public static String GRP_HAVE_PARENT_OBJECT;

    public static String RDO_NO_PARENT;

    public static String RDO_SHADOW_ROOT_PARENT;

    public static String VIEW_HELP_SELECTION_METHOD_DOC_URL;

    public static String DIA_TITLE_FIND_UNUSED_TEST_OBJECT;
    
    public static String UNUSED_TEST_OBJECT_LABEL;

    public static String UNUSED_TEST_OBJECT_TOOLBAR_DELETE_ALL;

    public static String UNUSED_TEST_OBJECT_TOOLBAR_EXPORT_CSV;

    public static String DIA_UNUSED_TEST_OBJECT_DELETE_MESSAGE;
    
    // ObjectPropertyView
    public static String VIEW_ITEM_TIP_ADD_NEW_PROPERTY;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerObjectRepositoryMessageConstants.class);
    }

    private ComposerObjectRepositoryMessageConstants() {
    }
}
