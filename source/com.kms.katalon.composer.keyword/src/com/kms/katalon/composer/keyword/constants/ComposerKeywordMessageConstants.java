package com.kms.katalon.composer.keyword.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerKeywordMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.keyword.constants.composerKeywordMessages";

    public static String HAND_ERROR_MSG_UNABLE_TO_DELETE_KEYWORD;

    public static String HAND_ERROR_MSG_UNABLE_TO_CREATE_KEYWORD;

    public static String HAND_ERROR_MSG_UNABLE_TO_CREATE_PACKAGE;

    public static String HAND_REFRESHING_PROJECT;

    public static String HAND_COLLECTING_CUSTOM_KEYWORD;

    public static String HAND_ERROR_MSG_CANNOT_OPEN_KEYWORD_FILE;

    public static String HAND_ERROR_MSG_UNABLE_TO_PASTE_DATA;

    public static String HAND_ERROR_MSG_FILE_NOT_EXIST;

    public static String HAND_TITLE_NAME_CONFLICT;

    public static String HAND_MSG_KW_NAME_ALREADY_EXISTS;

    public static String HAND_ERROR_MSG_UNABLE_TO_RENAME_PACKAGE;

    public static String DIA_TITLE_PACKAGE;

    public static String DIA_TITLE_RENAME;

    public static String DIA_MSG_NEW_PACKAGE;

    public static String DIA_MSG_RENAME_PACKAGE;

    public static String DIA_MSG_INVALID_PACKAGE_NAME;

    public static String DIA_MSG_INVALID_KEYWORD_NAME;

    public static String DIA_MSG_INVALID_JAVA_IDENTIFIER;

    public static String DIA_MSG_RENAME_KEYWORD;

    public static String DIA_MSG_CREATE_KEYWORD;

    public static String DIA_TITLE_PACKAGE_SELECTION;

    public static String DIA_MSG_CHOOSE_A_PACKAGE;

    public static String DIA_MSG_NO_PACKAGE;

    public static String DIA_WARN_DEFAULT_PACKAGE;

    public static String DIA_WARN_KEYWORD_START_WITH_LOWERCASE;

    public static String HAND_ERROR_MSG_EXCEED_PKG_NAME_LENGTH;

    public static String HAND_ERROR_MSG_EXCEED_CLASS_NAME_LENGTH;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerKeywordMessageConstants.class);
    }

    private ComposerKeywordMessageConstants() {
    }
}
