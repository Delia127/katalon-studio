package com.kms.katalon.composer.explorer.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerExplorerMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.explorer.constants.composerExplorerMessages";

    public static String CUS_DIALOG_TITLE;

    public static String HAND_DELETE_CONFIRM_MSG;

    public static String HAND_MULTI_DELETE_CONFIRM_MSG;

    public static String DIA_FIELD_SOURCE_ID;

    public static String DIA_MSG_HEADER_ENTITY_REFERENCES;

    public static String HAND_JOB_DELETING_FOLDER;

    public static String HAND_CONFIRM_TITLE;

    public static String HAND_CONFIRM_MSG_REQUIRE_SAVE_ALL_B4_CONTINUE;

    public static String PA_SEARCH_TEXT_DEFAULT_VALUE;

    public static String PA_IMAGE_TIP_ADVANCED_SEARCH;

    public static String LIS_ERROR_MSG_CANNOT_MOVE_THE_SELECTION;

    public static String LIS_ERROR_MSG_CANNOT_MOVE_INTO_DIFF_REGION;
    
    public static String LIS_ERROR_MSG_CANNOT_MOVE_TO_SUBFOLDER;

    public static String TOOLTIP_MESSAGE_PROPERTIES_ENTITY;
    
    public static String ERROR_CANNOT_FIND_CONTAINING_FOLDER;
    
    public static String CONTR_MENU_CONTEXT_IMPORT_OBJECTS;
    
    public static String PAGE_ERROR_MSG_UNABLE_TO_READ_SETTINGS;
    
    public static String PAGE_ERROR_MSG_UNABLE_TO_UPDATE_SETTINGS;
    
    public static String PAGE_ERROR_MSG_UNABLE_TO_REFESH_EXPLORER;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerExplorerMessageConstants.class);
    }

    private ComposerExplorerMessageConstants() {
    }
}
