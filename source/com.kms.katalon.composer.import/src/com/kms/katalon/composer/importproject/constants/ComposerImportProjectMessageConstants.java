package com.kms.katalon.composer.importproject.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerImportProjectMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.importproject.constants.composerImportProjectMessages";

    public static String COMP_DIALOG_MSG_SUFFIX;

    public static String COMP_DIALOG_MSG_PREFIX;

    public static String COMP_BTN_MERGE;

    public static String COMP_BTN_OVERRIDE;

    public static String COMP_BTN_CREATE_NEW;

    public static String COMP_DIALOG_TITLE_IMPORT_PROJECT;

    public static String COMP_CHKBOX_APPLY_TO_ALL;

    public static String HAND_NEW_PROJECT_NAME_TITLE;

    public static String HAND_NEW_PROJECT_NAME_MSG;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerImportProjectMessageConstants.class);
    }

    private ComposerImportProjectMessageConstants() {
    }
}
