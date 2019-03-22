package com.kms.katalon.composer.integration.slack.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerIntegrationSlackMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.integration.slack.constants.composerIntegrationSlackMessages";

    public static String PREF_LBL_SLACK;

    public static String PREF_LBL_TEAM_COLLABORATION;

    public static String PREF_LBL_SLACK_ENABLED;

    public static String PREF_LBL_TIP_SLACK_ENABLED;

    public static String PREF_LBL_SLACK_AUTH_TOKEN;

    public static String PREF_LBL_SLACK_CHANNEL;

    public static String PREF_LBL_SLACK_CHANNEL_DESC;

    public static String PREF_LBL_SLACK_USERNAME;

    public static String PREF_LBL_SLACK_USERNAME_DESC;

    public static String PREF_LBL_SLACK_AS_USER;

    public static String PREF_LBL_SLACK_AS_USER_DESC;

    public static String PREF_LBL_TEST_CONNECTION;

    public static String PREF_MSG_TEST_CONNECTION;

    public static String PREF_ERROR_MSG_X_CANNOT_BE_EMPTY_OR_BLANK;

    public static String PREF_SUCCESS_MSG_STATUS;

    public static String PREF_ERROR_MSG_PLS_CHK_INTERNET_CONNECTION;

    public static String PREF_QUESTION_MSG_DO_YOU_WANT_TO_DISABLE_SLACK;

    public static String PREF_LBL_SEND_MSG_TO_SLACK_WHEN;

    public static String PREF_SEND_OPEN_PROJECT;

    public static String PREF_SEND_CLOSE_PROJECT;

    public static String PREF_SEND_UPDATE_TEST_CASE;

    public static String PREF_SEND_UPDATE_TEST_SUITE;

    public static String PREF_SEND_UPDATE_TEST_DATA;

    public static String PREF_SEND_UPDATE_TEST_OBJECT;

    public static String PREF_SEND_RENAME_ITEM;

    public static String PREF_SEND_PASTE_FROM_COPY;

    public static String PREF_SEND_PASTE_FROM_CUT;

    public static String PREF_SEND_DELETE_ITEM;

    public static String PREF_SEND_CREATE_TEST_CASE;

    public static String PREF_SEND_CREATE_TEST_SUITE;

    public static String PREF_SEND_CREATE_TEST_DATA;

    public static String PREF_SEND_CREATE_TEST_OBJECT;

    public static String PREF_SEND_CREATE_KEYWORD;

    public static String PREF_SEND_CREATE_FOLDER;

    public static String PREF_SEND_CREATE_PACKAGE;

    public static String UTIL_ERROR;

    public static String UTIL_ERROR_MSG_PLS_CHK_INTERNET_CONNECTION;

    public static String UTIL_MSG_PREFIX_TEAM_COLLABORATION;

    public static String UTIL_SENDING;

    public static String UTIL_SENT;

    public static String EMOJI_MSG_OPEN_PROJECT;

    public static String EMOJI_MSG_CLOSE_PROJECT;

    public static String EMOJI_MSG_NEW;

    public static String EMOJI_MSG_DELETE;

    public static String EMOJI_MSG_DELETE_FOLDER;

    public static String EMOJI_MSG_UPDATE;

    public static String EMOJI_MSG_RENAME;
    
    public static String PREF_LBL_GETSLACKPLUGIN;

    public static String EMOJI_MSG_COPY;

    public static String EMOJI_MSG_MOVE;

    public static String SLACK_ERROR_MSG_CHANNEL_NOT_FOUND;

    public static String SLACK_ERROR_MSG_NOT_IN_CHANNEL;

    public static String SLACK_ERROR_MSG_IS_ARCHIVED;

    public static String SLACK_ERROR_MSG_MSG_TOO_LONG;

    public static String SLACK_ERROR_MSG_NO_TEXT;

    public static String SLACK_ERROR_MSG_RATE_LIMITED;

    public static String SLACK_ERROR_MSG_NOT_AUTHED;

    public static String SLACK_ERROR_MSG_INVALID_AUTH;

    public static String SLACK_ERROR_MSG_ACCOUNT_INACTIVE;
    
    public static String SlackSettingsComposite_MSG_DEPRECATED;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerIntegrationSlackMessageConstants.class);
    }

    private ComposerIntegrationSlackMessageConstants() {
    }
}
