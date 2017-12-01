package com.kms.katalon.composer.testlistener.constant;

import org.eclipse.osgi.util.NLS;

public class ComposerTestListenerMessageConstants extends NLS {

    private static final String BUNDLE_NAME = "com.kms.katalon.composer.testlistener.constant.composerTestListenerMessageConstants";

    public static String HDL_MSG_UNABLE_TO_CREATE_TEST_LISTENER;

    public static String ITEM_LBL_NEW_TEST_LISTENER;

    public static String HAND_JOB_DELETING;

    public static String HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_LISTENER;

    public static String HDL_MSG_UNABLE_TO_RENAME_TEST_LISTENER;

    public static String DIA_TITLE_RENAME_TEST_LISTENER;

    public static String DIA_MSG_NAME_ALREADY_EXISTS;

    public static String DIA_MSG_CREATE_NEW_TEST_LISTENER;

    public static String DIA_LBL_GENERATE_SAMPLE_BEFORE_TEST_CASE;

    public static String DIA_LBL_GENERATE_SAMPLE_AFTER_TEST_CASE;

    public static String DIA_LBL_GENERATE_SAMPLE_BEFORE_TEST_SUITE;

    public static String DIA_LBL_GENERATE_SAMPLE_AFTER_TEST_SUITE;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerTestListenerMessageConstants.class);
    }

    private ComposerTestListenerMessageConstants() {
    }
}
