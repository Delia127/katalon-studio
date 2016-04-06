package com.kms.katalon.composer.integration.slack.constants;

import com.kms.katalon.constants.PreferenceConstants;

public interface SlackPreferenceConstants extends PreferenceConstants {
    public static final String SLACK_ENABLED = "slackConfig.enabled";

    public static final String SLACK_AUTH_TOKEN = "slackConfig.token";

    public static final String SLACK_CHANNEL_GROUP = "slackConfig.channel";

    public static final String SLACK_USERNAME = "slackConfig.username";

    public static final String SLACK_AS_USER = "slackConfig.asUser";

    public static final String SLACK_SEND_OPEN_PROJECT = "slackConfig.sendOpenProject";

    public static final String SLACK_SEND_CLOSE_PROJECT = "slackConfig.sendCloseProject";

    public static final String SLACK_SEND_UPDATE_TEST_CASE = "slackConfig.sendUpdateTestCase";

    public static final String SLACK_SEND_UPDATE_TEST_SUITE = "slackConfig.sendUpdateTestSuite";

    public static final String SLACK_SEND_UPDATE_TEST_DATA = "slackConfig.sendUpdateTestData";

    public static final String SLACK_SEND_UPDATE_TEST_OBJECT = "slackConfig.sendUpdateTestObject";

    public static final String SLACK_SEND_RENAME_ITEM = "slackConfig.sendRenameItem";

    public static final String SLACK_SEND_PASTE_FROM_COPY = "slackConfig.sendPasteFromCopy";

    public static final String SLACK_SEND_PASTE_FROM_CUT = "slackConfig.sendPasteFromCut";

    public static final String SLACK_SEND_DELETE_ITEM = "slackConfig.sendDeleteItem";

    public static final String SLACK_SEND_CREATE_TEST_CASE = "slackConfig.sendCreateTestCase";

    public static final String SLACK_SEND_CREATE_TEST_SUITE = "slackConfig.sendCreateTestSuite";

    public static final String SLACK_SEND_CREATE_TEST_DATA = "slackConfig.sendCreateTestData";

    public static final String SLACK_SEND_CREATE_TEST_OBJECT = "slackConfig.sendCreateTestObject";

    public static final String SLACK_SEND_CREATE_KEYWORD = "slackConfig.sendCreateKeyword";

    public static final String SLACK_SEND_CREATE_FOLDER = "slackConfig.sendCreateFolder";

    public static final String SLACK_SEND_CREATE_PACKAGE = "slackConfig.sendCreatePackage";
}
