package com.kms.katalon.composer.checkpoint.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerCheckpointMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.checkpoint.constants.composerCheckpointMessages";

    public static String HAND_MSG_UNABLE_TO_CREATE_CHECKPOINT;

    public static String DIA_TITLE_CHECKPOINT_PROPERTIES;

    public static String DIA_LBL_TAKEN_DATE;

    public static String DIA_MSG_CREATE_CHECKPOINT;

    public static String DIA_MSG_CREATE_CHECKPOINT_FROM_TEST_DATA;

    public static String DIA_CHK_IS_TEST_DATA_SOURCE;

    public static String DIA_LBL_DATA_TYPE;

    public static String DIA_LBL_TEST_DATA_ID;

    public static String MENU_LBL_CHECKPOINT_FROM_TEST_DATA;

    public static String HAND_MSG_DELETING_X_Y;

    public static String HAND_MSG_UNABLE_TO_DELETE_CHECKPOINT;

    public static String HAND_MSG_UNABLE_TO_RENAME_CHECKPOINT;

    public static String HAND_EXC_CHECKPOINT_ENTITY_IS_NULL;

    public static String DIA_TITLE_CHECKPOINT_REFERENCES;

    public static String DIA_LBL_SEPARATOR;

    public static String DIA_TITLE_CHECKPOINT_SOURCE_INFO;

    public static String DIA_LBL_FILE_PATH;

    public static String DIA_CHK_USING_FIRST_ROW_AS_HEADER;

    public static String DIA_CHK_IS_USING_RELATIVE_PATH;

    public static String DIA_LBL_SHEET_NAME;

    public static String PART_SOURCE_INFO;

    public static String PART_TITLE_TAKE_CHECKPOINT_SNAPSHOT;

    public static String PART_MSG_CLEAR_CHECKPOINT_DATA;

    public static String PART_MSG_CANNOT_TAKE_SNAPSHOT;

    public static String PART_TITLE_RELOAD;

    public static String PART_MSG_RELOAD_FILE_CONTENT;

    public static String PART_LBL_NO_CHECKPOINT_DATA;

    public static String PART_LBL_SNAPSHOT_WAS_TAKEN_ON_X;

    public static String PART_MSG_UNABLE_TO_SAVE_CHECKPOINT;

    public static String PART_BTN_TAKE_SNAPSHOT;

    public static String PART_MENU_CHECK_COLUMN;

    public static String PART_MENU_UNCHECK_COLUMN;

    public static String PART_MENU_CHECK_ROW;

    public static String PART_MENU_UNCHECK_ROW;

    public static String PART_MENU_CHECK_ALL;

    public static String PART_MENU_UNCHECK_ALL;

    public static String PART_LBL_SQL_QUERY;

    public static String PART_LBL_FILE_PATH;

    public static String PART_TITLE_TEST_DATA_BROWSER;

    public static String PART_MSG_PLEASE_SELECT_A_TEST_DATA;

    public static String PART_MSG_UNABLE_TO_SELECT_TEST_DATA;

    public static String WIZ_EXCEL_SOURCE_CONFIGURATION;

    public static String WIZ_TITLE_EXCEL_DATA;

    public static String WIZ_CSV_SOURCE_CONFIGURATION;

    public static String WIZ_TITLE_CSV_DATA;

    public static String WIZ_DATABASE_SOURCE_CONFIGURATION;

    public static String WIZ_TITLE_DATABASE;

    public static String WIZ_TEST_DATA_SOURCE_CONFIGURATION;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerCheckpointMessageConstants.class);
    }

    private ComposerCheckpointMessageConstants() {
    }
}
