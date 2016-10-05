package com.kms.katalon.composer.integration.git.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerIntegrationGitMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.integration.git.constants.composerIntegrationGitMessages";

    public static String ENABLE_GIT_CHECK_LABEL;

    public static String HAND_ERROR_MSG_UNABLE_TO_SHARE_PROJ;

    public static String HAND_SUCCESS_MSG_SHARE_PROJECT;

    public static String GIT_MENU_LABEL;

    public static String GIT_SHARE_PROJECT_MENU_ITEM_LABEL;

    public static String GIT_BRANCH_MENU_LABEL;

    public static String GIT_ADVANCE_BRANCH_MENU_ITEM_LABEL;

    public static String GIT_NEW_BRANCH_MENU_ITEM_LABEL;

    public static String GIT_CHECKOUT_BRANCH_MENU_ITEM_LABEL;

    public static String GIT_DELETE_BRANCH_MENU_ITEM_LABEL;

    public static String GIT_COMMIT_MENU_ITEM_LABEL;

    public static String GIT_FETCH_MENU_ITEM_LABEL;

    public static String GIT_PULL_MENU_ITEM_LABEL;

    public static String GIT_PUSH_MENU_ITEM_LABEL;

    public static String GIT_SHOW_HISTORY_MENU_ITEM_LABEL;

    public static String GIT_CLONE_MENU_ITEM_LABEL;

    public static String LBL_REPOSITORY_URL;

    public static String CHCK_SAVE_AUTHENTICATION;

    public static String HAND_ERROR_MSG_UNABLE_TO_CLONE;

    public static String HAND_ERROR_MSG_UNABLE_TO_CONNECT;

    public static String HAND_ERROR_MSG_UNABLE_TO_ACCESS_COULD_NOT_RESOLVE_Y;

    public static String MSG_PUSH_BRANCH_DIALOG;

    public static String LBL_REMOTE_BRANCH_NAME;

    public static String LBL_CURRENT_BRANCH_NAME;

    public static String MSG_ERR_MISSING_REMOTE_BRANCH;

    public static String LBL_REFRESH_BUTTON;

    public static String HAND_ERROR_MSG_NO_REMOTE_BRANCHES_FOUND;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerIntegrationGitMessageConstants.class);
    }

    private ComposerIntegrationGitMessageConstants() {
    }
}
