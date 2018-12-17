package com.kms.katalon.controller.constants;

import org.eclipse.osgi.util.NLS;

public class ControllerMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.controller.constants.controllerMessages";

    public static String CTRL_ERROR_MSG_CANNOT_EXPORT_PROJ;

    public static String CTRL_ERROR_MSG_CANNOT_IMPORT_PROJ;

    public static String CTRL_TXT_IMPORTING_PROJ;

    public static String CTRL_TXT_EXPORT_PROJ;

    public static String CTRL_TXT_EXPORT_TEST_CASE;

    public static String CTRL_EXC_TEST_CASE_CANNOT_BE_NULL;

    public static String CTRL_EXC_VAR_ID_CANNOT_BE_NULL;

    public static String CTRL_NEW_TEST_CASE;

    public static String CTRL_NEW_TEST_OBJECT;

    public static String CTRL_NEW_WS_REQUEST;

    public static String CTRL_NEW_TEST_DATA;

    public static String CTRL_EXC_TEST_DATA_IS_NULL;

    public static String CTRL_EXC_TEST_DATA_IS_NOT_DB_TYPE;

    public static String CTRL_NEW_TEST_SUITE;

    public static String EXC_MSG_PROJECT_NOT_FOUND;

    public static String CTRL_EXC_CHECKPOINT_IS_NULL;

    public static String CTRL_EXC_SOURCE_URL_IS_NULL;

    public static String CTRL_EXC_CANNOT_TAKE_SNAPSHOT_DATA;

    public static String CTRL_EXC_DB_CONNECTION_SETTINGS_ARE_EMPTY;

    public static String CTRL_EXC_EXCEL_SHEET_NAME_IS_EMPTY;

    public static String CTRL_EXC_INVALID_CSV_SEPARATOR;

    public static String CTRL_EXC_REQUEST_BODY_IS_BLANK;

    public static String CTRL_EXC_REQUEST_URL_IS_BLANK;
    
    public static String GlobalVariableController_MSG_COULD_NOT_GENERATE_GLOBALVARIABLE;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ControllerMessageConstants.class);
    }

    private ControllerMessageConstants() {
    }
}
