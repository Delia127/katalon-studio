package com.kms.katalon.composer.artifact.constant;

import org.eclipse.osgi.util.NLS;

public class MessageConstants {
    
    private static final String MESSAGE_FILE_NAME = "ConstantMessages";
    
    static {
        NLS.initializeMessages("com.kms.katalon.composer.artifact.constant." + MESSAGE_FILE_NAME, MessageConstants.class);
    }
    
    public static String ERROR;
    
    public static String INFO;
    
    public static String DIALOG_TITLE_EXPORT_TEST_ARTIFACTS;
    
    public static String LBL_TEST_CASE;
    
    public static String LBL_TEST_OBJECT;
    
    public static String LBL_PROFILE;
    
    public static String LBL_KEYWORD;
    
    public static String COL_TEST_CASE_NUMBER;
    
    public static String COL_TEST_CASE_ID;
    
    public static String COL_TEST_OBJECT_ID;
    
    public static String COL_PROFILE_ID;
    
    public static String COL_KEYWORD_ID;
    
    public static String TOOL_ITEM_ADD;
    
    public static String TOOL_ITEM_DELETE;
    
    public static String LBL_EXPORT_LOCATION;
    
    public static String BTN_BROWSE;
    
    public static String MSG_INVALID_EXPORT_LOCATION;
    
    public static String MSG_UNABLE_TO_EXPORT_TEST_ARTIFACTS;
    
    public static String MSG_UNABLE_TO_IMPORT_TEST_ARTIFACTS;

    public static String DIALOG_TITLE_IMPORT_TEST_ARTIFACTS;
    
    public static String LBL_CHOOSE_IMPORT_FILE;
    
    public static String LBL_CHOOSE_TEST_CASE_IMPORT_LOCATION;
    
    public static String LBL_CHOOSE_TEST_OBJECT_IMPORT_LOCATION;
    
    public static String LBL_CHOOSE_KEYWORD_IMPORT_LOCATION;
    
    public static String MSG_INVALID_IMPORT_FILE;
    
    public static String MSG_INVALID_TEST_CASE_IMPORT_LOCATION;
    
    public static String MSG_INVALID_TEST_OBJECT_IMPORT_LOCATION;
    
    public static String MSG_TEST_ARTIFACTS_EXPORTED_SUCCESSFULLY;
    
    public static String MSG_TEST_ARTIFACTS_IMPORTED_SUCCESSFULLY;
    
    public static String MSG_FAILED_TO_EXPORT_TEST_ARTIFACTS;
    
    public static String MSG_FAILED_TO_IMPORT_TEST_ARTIFACTS;
    
    public static String MSG_OPEN_A_PROJECT;
    
    public static String MSG_EXPORTING_TEST_ARTIFACTS;
    
    public static String MSG_EXPORTING_TEST_CASES;
    
    public static String MSG_EXPORTING_TEST_SCRIPTS;
    
    public static String MSG_EXPORTING_TEST_OBJECTS;
    
    public static String MSG_EXPORTING_PROFILES;
    
    public static String MSG_EXPORTING_KEYWORDS;
    
    public static String MSG_COMPRESSING_FILES;
    
    public static String MSG_ERROR_EXPORTING_TEST_ARTIFACTS;
    
    public static String MSG_IMPORTING_TEST_ARTIFACTS;
    
    public static String MSG_ERROR_IMPORTING_TEST_ARTIFACTS;
    
    public static String IMPORT_EXPORT_EXPORT_TEMP_FOLDER;
    
    public static String IMPORT_EXPORT_IMPORT_TEMP_FOLDER;
    
    public static String IMPORT_EXPORT_EXPORT_FILE_NAME;

    public static String IMPORT_EXPORT_TEST_CASES_FOLDER;

    public static String IMPORT_EXPORT_TEST_SCRIPTS_FOLDER;

    public static String IMPORT_EXPORT_TEST_OBJECTS_FOLDER;

    public static String IMPORT_EXPORT_PROFILES_FOLDER;

    public static String IMPORT_EXPORT_KEYWORDS_FOLDER;
}
