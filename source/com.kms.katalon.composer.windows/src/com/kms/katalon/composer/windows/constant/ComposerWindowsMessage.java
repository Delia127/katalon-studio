package com.kms.katalon.composer.windows.constant;

import org.eclipse.osgi.util.NLS;


public class ComposerWindowsMessage {
    
    public static String TITLE_NEW_WINDOWS_OBJECT_NAME;

    public static String NewWindowsElementDialog_MSG_CREATE_NEW_WINDOWS_OBJECT;

    public static String DLG_TITLE_KATALON_NATIVE_RECORDER;

    public static String MSG_FAILED_START_APPLICATION;

    static {
        // initialize resource bundle
        NLS.initializeMessages("com.kms.katalon.composer.windows.constant.composerWindowsMessage", ComposerWindowsMessage.class);
    }

    private ComposerWindowsMessage() {
    }
}
