package com.kms.katalon.composer.search.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerSearchMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.search.constants.composerSearchMessages";

    public static String ACT_WARN_MSG_LINE_NOT_FOUND;

    public static String ACT_WARN_MSG_UNABLE_TO_OPEN_TEST_CASE;

    public static String ACT_ERROR_MSG_UNABLE_TO_SHOW_REFERENCES;

    public static String VIEW_LBL_CONTAINING_TEXT;

    public static String VIEW_CHKBOX_CASE_SENSITIVE;

    public static String VIEW_CHKBOX_REGULAR_EXPRESSION;

    public static String VIEW_CHKBOX_WHOLE_WORD;

    public static String VIEW_LBL_SEARCH_IN;

    public static String CONTR_MENU_CONTEXT_SHOW_REFERENCES;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerSearchMessageConstants.class);
    }

    private ComposerSearchMessageConstants() {
    }
}
