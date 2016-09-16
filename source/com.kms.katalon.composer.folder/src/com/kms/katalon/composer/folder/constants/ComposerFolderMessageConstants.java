package com.kms.katalon.composer.folder.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerFolderMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.folder.constants.composerFolderMessages";

    public static String DIA_FOLDER_NEW_MSG;

    public static String HAND_ERROR_MSG_UNABLE_TO_DELETE_FOLDER;

    public static String HAND_NEW_FOLDER;

    public static String HAND_ERROR_MSG_UNABLE_TO_CREATE_FOLDER;

    public static String HAND_ERROR_MSG_UNABLE_TO_PASTE_DATA;

    public static String HAND_ERROR_MSG_UNABLE_TO_PASTE_SAME_SRC_DEST;

    public static String HAND_ERROR_MSG_CANNOT_PASTE_INTO_DIFF_REGION;

    public static String HAND_ERROR_MSG_UNABLE_TO_RENAME_FOLDER;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerFolderMessageConstants.class);
    }

    private ComposerFolderMessageConstants() {
    }
}
