package com.kms.katalon.composer.project.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    // CleanProjectHandler
    public static final String HAND_TEMP_CLEANER = "Temporary cleaner";

    public static final String HAND_CLEANING_TEMP_FILES = "Cleaning temporary files";

    public static final String HAND_CLEANING_ITEM = "Cleaning item: ";

    // CloseProjectHandler
    public static final String WARN_TITLE = WARN;

    public static final String HAND_WARN_MSG_UNABLE_TO_CLOSE_CURRENT_PROJ = "Unable to close current project.";

    public static final String HAND_WARN_MSG_NO_PROJ_FOUND = "No project found.";

    // NewProjectHandler
    public static final String ERROR_TITLE = ERROR;

    public static final String HAND_ERROR_MSG_UNABLE_TO_CREATE_NEW_PROJ = "Unable to create project";
    
    public static final String HAND_ERROR_MSG_NEW_PROJ_LOCATION_INVALID = "The specified project directory is either invalid or read-only";

    // OpenProjectHandler
    public static final String HAND_ERROR_MSG_CANNOT_OPEN_PROJ = "Can not open project.";

    public static final String HAND_OPEN_PROJ = "Open project";

    public static final String HAND_OPENING_PROJ = "Opening project...";

    public static final String HAND_LOADING_PROJ = "Loading project...";

    public static final String HAND_REFRESHING_EXPLORER = "Refreshing explorer...";

    // RebuildProjectHandler
    public static final String HAND_REBUILD_PROJ = "Rebuild Project";

    public static final String HAND_REBUILDING_PROJ = "Rebuilding project...";

    public static final String HAND_ERROR_MSG_UNABLE_TO_REBUILD_PROJ = "Unable to rebuild project";

    // SettingHandler
    public static final String HAND_PROJ_SETTING = "Project Settings";

    // UpdateProjectHandler
    public static final String HAND_ERROR_MSG_UNABLE_TO_UPDATE_PROJ = "Unable to update project";

    // NewProjectDialog
    public static final String VIEW_TITLE_NEW_PROJ = "New Project";

    public static final String VIEW_TITLE_PROJECT_PROPERTIES = "Project Properties";

    public static final String VIEW_MSG_PLS_ENTER_PROJ_INFO = "Please enter project information";

    public static final String VIEW_LBL_NAME = NAME;

    public static final String VIEW_LBL_LOCATION = "Location";

    public static final String VIEW_LBL_DESCRIPTION = DESCRIPTION;

    public static final String VIEW_BTN_BROWSE = BROWSE;

    public static final String VIEW_ERROR_MSG_PROJ_LOC_CANNOT_BE_BLANK = "Project's location cannot be blank.";
    
    public static final String VIEW_ERROR_MSG_PROJ_LOC_INVALID = "Project's location is invalid.";

    public static final String VIEW_ERROR_MSG_PROJ_NAME_CANNOT_BE_BLANK = "Project's name cannot be blank.";

    public static final String VIEW_ERROR_MSG_PROJ_NAME_EXISTED_IN_LOC = "A project with the same name already exists in the selected location.";
    
    public static final String VIEW_ERROR_MSG_PROJ_LOC_NOT_READABLE = "Project's location is not readable.";
    
    public static final String VIEW_ERROR_MSG_PROJ_LOC_NOT_WRITEABLE = "Project's location is not writeable.";
    
    public static final String VIEW_NEW_EMPTY_PROJECT_PAGE_NAME = "New Empty Project Page";

    public static final String VIEW_TESTING_TYPES_PROJECT_PAGE_NAME = "Select testing type(s)";

    public static final String VIEW_MSG_SPECIFY_TESTING_TYPES = "Please specify your type(s) of testing. Selected option will be included in sample module of generated project";

    public static final String VIEW_LBL_WEB_TESTING = "Web Testing";

    public static final String VIEW_LBL_MOBILE_TESTING = "Mobile Testing";

    public static final String VIEW_LBL_API_TESTING = "API Testing";
    
    public static final String WEB_ICON_PATH = "/icons/template_web.png";
    
    public static final String MOBILE_ICON_PATH = "/icons/template-mobile.png";
    
    public static final String WEB_API_ICON_PATH = "/icons/template_api.png";
    
    public static final String VIEW_LBL_NEW_PROJECT_WIZARD_TIP = "Click the Finish button to create a blank project. Click Next button to see more template project options";
    
    public static final String TEMPL_CUSTOM_KW_PKG_REL_PATH = GlobalStringConstants.ROOT_FOLDER_NAME_KEYWORD + "/com/example";
}
