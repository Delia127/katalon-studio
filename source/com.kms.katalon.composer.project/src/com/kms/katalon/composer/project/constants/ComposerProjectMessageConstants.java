package com.kms.katalon.composer.project.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerProjectMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.project.constants.composerProjectMessages";

	public static String CANNOT_CREATE_PROJECT_IN_KATALON_FOLDER;

    public static String HAND_TEMP_CLEANER;

    public static String HAND_CLEANING_TEMP_FILES;

    public static String HAND_CLEANING_ITEM;

    public static String HAND_WARN_MSG_UNABLE_TO_CLOSE_CURRENT_PROJ;

    public static String HAND_WARN_MSG_NO_PROJ_FOUND;

    public static String HAND_ERROR_MSG_UNABLE_TO_CREATE_NEW_PROJ;

    public static String HAND_ERROR_MSG_NEW_PROJ_LOCATION_INVALID;

    public static String HAND_ERROR_MSG_CANNOT_OPEN_PROJ;

    public static String HAND_OPEN_PROJ;

    public static String HAND_OPENING_PROJ;

    public static String HAND_LOADING_PROJ;

    public static String HAND_REFRESHING_EXPLORER;

    public static String HAND_REBUILD_PROJ;

    public static String HAND_REBUILDING_PROJ;

    public static String HAND_ERROR_MSG_UNABLE_TO_REBUILD_PROJ;

    public static String HAND_PROJ_SETTING;

    public static String HAND_ERROR_MSG_UNABLE_TO_UPDATE_PROJ;

    public static String VIEW_TITLE_NEW_PROJ;

    public static String VIEW_TITLE_PROJECT_PROPERTIES;

    public static String VIEW_MSG_PLS_ENTER_PROJ_INFO;

    public static String VIEW_LBL_LOCATION;

    public static String VIEW_ERROR_MSG_PROJ_LOC_CANNOT_BE_BLANK;

    public static String VIEW_ERROR_MSG_PROJ_LOC_INVALID;

    public static String VIEW_ERROR_MSG_PROJ_NAME_CANNOT_BE_BLANK;

    public static String VIEW_ERROR_MSG_PROJ_NAME_EXISTED_IN_LOC;

    public static String VIEW_ERROR_MSG_PROJ_LOC_NOT_READABLE;

    public static String VIEW_ERROR_MSG_PROJ_LOC_NOT_WRITEABLE;

    public static String VIEW_NEW_EMPTY_PROJECT_PAGE_NAME;

    public static String VIEW_TESTING_TYPES_PROJECT_PAGE_NAME;

    public static String VIEW_MSG_SPECIFY_TESTING_TYPES;

    public static String VIEW_LBL_WEB_TESTING;

    public static String VIEW_LBL_MOBILE_TESTING;

    public static String VIEW_LBL_API_TESTING;

    public static String VIEW_LBL_NEW_PROJECT_WIZARD_TIP;

    public static String HAND_PROJECT_SETTINGS_PAGE_ID_NOT_FOUND;

    public static String HAND_INDEXING_PROJECT;
    
    public static String VIEW_TITLE_NEW_SAMPLE_WEB_UI_PROJ;

    public static String VIEW_TITLE_NEW_SAMPLE_MOBILE_PROJ;

    public static String VIEW_TITLE_NEW_SAMPLE_WS_PROJ;
    
    public static String HAND_IMPORT_SELENIUM_IDE;
    
    public static String VIEW_OPTION_BLANK_PROJECT;
    
    public static String VIEW_OPTION_SAMPLE_PROJECT;
    
    public static String VIEW_OPTION_WEB_SERVICE_PROJECT;
    
    public static String VIEW_OPTION_GENERIC_PROJECT;
    
    public static String VIEW_LBL_PROJECT;
    
    public static String VIEW_LBL_REPOSITORY_URL;

    public static String SAMPLE_WEB_UI_PROJECT;
    
    public static String SAMPLE_MOBILE_PROJECT;
    
    public static String SAMPLE_WEB_SERVICE_PROJECT;
    
    public static String BLANK_PROJECT;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerProjectMessageConstants.class);
    }

    private ComposerProjectMessageConstants() {
    }
}
