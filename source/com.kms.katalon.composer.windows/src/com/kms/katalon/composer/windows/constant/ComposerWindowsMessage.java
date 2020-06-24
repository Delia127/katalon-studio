package com.kms.katalon.composer.windows.constant;

import org.eclipse.osgi.util.NLS;


public class ComposerWindowsMessage {
    
    public static String TITLE_NEW_WINDOWS_OBJECT_NAME;

    public static String TITLE_GET_ATTRIBUTE_DIALOG;

    public static String TITLE_SET_ENCRYPTED_TEXT_DIALOG;

    public static String NewWindowsElementDialog_MSG_CREATE_NEW_WINDOWS_OBJECT;

    public static String DLG_TITLE_KATALON_NATIVE_RECORDER;

    public static String MSG_FAILED_START_APPLICATION;

    public static String MSG_APP_CLOSED_UNEXPECTEDLY;

    public static String MSG_KATALON_NATIVE_RECORDER_SERVER_EXCEPTION;

    public static String LBL_GET_ATTRIBUTE_INPUT;

    public static String LBL_GET_ATTRIBUTE_RESULT;

    public static String LBL_RAW_TEXT;

    public static String LBL_ENCRYPTED_TEXT;

    public static String BTN_APPLY;

    public static String BTN_CANCEL;


    static {
        // initialize resource bundle
        NLS.initializeMessages("com.kms.katalon.composer.windows.constant.composerWindowsMessage", ComposerWindowsMessage.class);
    }

    private ComposerWindowsMessage() {
    }
}
