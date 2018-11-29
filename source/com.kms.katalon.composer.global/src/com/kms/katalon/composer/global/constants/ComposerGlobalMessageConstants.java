package com.kms.katalon.composer.global.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerGlobalMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.global.constants.composerGlobalMessages";

	public static String PA_ERROR_MSG_UNABLE_TO_UPDATE_PROFILE;

    public static String DIA_ERROR_MSG_INVALID_VAR_NAME;

    public static String DIA_LBL_DEFAULT_VALUE;

    public static String DIA_NEW_VAR_TITLE;

    public static String DIA_NEW_VAR_MSG;

    public static String DIA_EDIT_VAR_TITLE;

    public static String DIA_TITLE_NEW_VAR;

    public static String DIA_TITLE_EDIT_VAR;

    public static String DIA_INFO_MSG_CREATE_NEW_VAR;

    public static String DIA_INFO_MSG_EDIT_NEW_VAR;

    public static String PA_COL_DEFAULT_VALUE_TYPE;

    public static String PA_COL_DEFAULT_VALUE;

    public static String PA_MSG_VARIABLE_NAME_EXIST;

    public static String PA_MENU_CONTEXT_SHOW_PREFERENCES;

    public static String PA_ERROR_MSG_UNABLE_TO_UPDATE_VAR_REFERENCES;

    public static String PA_ERROR_MSG_UNABLE_TO_SAVE_ALL_VAR;

    public static String PA_INFO_MSG_REQUIRE_SAVE_B4_REFRESH;

    public static String PA_WARN_TITLE_INVALID_VAR;

    public static String PA_WARN_MSG_INVALID_VAR_NAME;

    public static String PA_WARN_MSG_DUPLICATE_VAR_NAME;
    
    // RenameExecutionProfileDialog
    public static String DIA_TITLE_RENAME_EXECUTION_PROFILE;
    
    // NewExecutionProfileDialog
    public static String DIA_TITLE_NEW_EXECUTION_PROFILE;
    
    // NewExecutionProfilePopupMenu
    public static String ITEM_LBL_NEW_EXECUTION_PROFILE;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerGlobalMessageConstants.class);
    }

    private ComposerGlobalMessageConstants() {
    }
}
