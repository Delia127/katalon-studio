package com.kms.katalon.dal.fileservice.constants;

import org.eclipse.osgi.util.NLS;

public class DalFileserviceMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.dal.fileservice.constants.dalFileserviceMessages";

    public static String FS_INVALID_FILE_NAME_BY_BLANK;

    public static String FS_INVALID_FILE_NAME_BY_DOTS;

    public static String FS_INVALID_FILE_NAME_BY_SPECIAL_CHAR;

    public static String DP_EXC_NAME_CANNOT_BE_NULL_OR_EMPTY;

    public static String DP_EXC_NAME_ALREADY_EXISTED;

    public static String MNG_EXC_EXISTED_DATA_FILE_NAME;

    public static String MNG_NEW_FOLDER;

    public static String MNG_EXC_EXISTED_FOLDER_NAME;

    public static String MNG_EXC_FAILED_TO_UPDATE_PROJ;

    public static String MNG_EXC_EXISTED_TEST_CASE_NAME_INSENSITVE;

    public static String MNG_EXC_EXISTED_TEST_SUITE_NAME;

    public static String MNG_NEW_REQUEST;

    public static String MNG_EXC_CHECKPOINT_IS_NULL;

    public static String MNG_EXC_PROJECT_IS_NULL;

    public static String MNG_EXC_PARENT_FOLDER_IS_NULL;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, DalFileserviceMessageConstants.class);
    }

    private DalFileserviceMessageConstants() {
    }
}
