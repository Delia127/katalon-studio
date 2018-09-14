package com.kms.katalon.entity.constants;

import org.eclipse.osgi.util.NLS;

public class EntityMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.entity.constants.entityMessages";

    public static String EXC_DUPLICATED_DATA_FILE_NAME;

    public static String EXC_DUPLICATED_FILE_NAME;

    public static String EXC_DUPLICATE_FOLDER_NAME;

    public static String EXC_DUPLICATE_ENTITY;

    public static String EXC_CANNOT_SAVE_FILE_PATH_LENG_LIMIT_EXCEEDED;

    public static String EXC_CANNOT_SAVE_CHILD_ENTITY_FILE_PATH_LIMIT_EXCEEDED;

    public static String EXC_X_COULDNT_EXCEED_200_CHARS;

    public static String EXC_CANNOT_DEL_TEST_CASE_X_FOR_REASON;

    public static String EXC_MULTIPLE_ENTITIES_W_ID_X_AND_TYPE_Y;

    public static String EXC_NO_DATA_FILE_W_PROJ_ID_X_AND_GUID_Y;

    public static String EXC_NO_ENTITY_W_ID_X_AND_TYPE_Y;

    public static String EXC_NO_PROJ_VER_W_ID_X_AND_VER_Y;

    public static String EXC_NO_WEB_ELEMENT_W_PROJ_ID_X_AND_GUID_Y;

    public static String EXC_WRONG_ENTITY_VER;
    
    public static String EXC_INVALID_SWAGGER_FILE;

    public static String DEFAULT_WEB_SERVICE_NAME;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, EntityMessageConstants.class);
    }

    private EntityMessageConstants() {
    }
}
