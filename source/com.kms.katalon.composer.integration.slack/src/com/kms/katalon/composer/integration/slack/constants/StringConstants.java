package com.kms.katalon.composer.integration.slack.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	// SlackPreferencePage
	public static final String PREF_LBL_SLACK = "Slack";
	public static final String PREF_LBL_TEAM_COLLABORATION = "Team Collaboration";
	public static final String PREF_LBL_SLACK_ENABLED = "Using Slack";
	public static final String PREF_LBL_TIP_SLACK_ENABLED = "Using Slack for activity log";
	public static final String PREF_LBL_SLACK_AUTH_TOKEN = "Authentication Token";
	public static final String PREF_LBL_SLACK_CHANNEL = "Channel/Group";
	public static final String PREF_LBL_SLACK_CHANNEL_DESC = "Specify Channel/Group name";
	public static final String PREF_LBL_SLACK_USERNAME = "Bot Username";
	public static final String PREF_LBL_SLACK_USERNAME_DESC = "Enter Bot Username if the authentication token above is not yours";
	public static final String PREF_LBL_SLACK_AS_USER = "Post the message as the authed user";
	public static final String PREF_LBL_SLACK_AS_USER_DESC = "Check this if you are the authed user in authentication token above";
	public static final String PREF_LBL_TEST_CONNECTION = "Test connection";
	public static final String PREF_MSG_TEST_CONNECTION = ":ok:[Connection Test] Success";
	public static final String PREF_ERROR_MSG_X_CANNOT_BE_EMPTY_OR_BLANK = " cannot be empty or blank";
	public static final String PREF_SUCCESS_MSG_STATUS = "Successful!";
	public static final String PREF_ERROR_MSG_PLS_CHK_INTERNET_CONNECTION = "Please check your Internet connection!";
	public static final String PREF_QUESTION_MSG_DO_YOU_WANT_TO_DISABLE_SLACK = "You cannot leave the fields in blank. Do you want to disable Slack?";
	public static final String PREF_LBL_SEND_MSG_TO_SLACK_WHEN = "Send message to Slack when";
	public static final String PREF_SEND_OPEN_PROJECT = "Open Project";
	public static final String PREF_SEND_CLOSE_PROJECT = "Close Project";
	public static final String PREF_SEND_UPDATE_TEST_CASE = "Update Test Case";
	public static final String PREF_SEND_UPDATE_TEST_SUITE = "Update Test Suite";
	public static final String PREF_SEND_UPDATE_TEST_DATA = "Update Data File (Test Data)";
	public static final String PREF_SEND_UPDATE_TEST_OBJECT = "Update Test Object (in Object Repository)";
	public static final String PREF_SEND_RENAME_ITEM = "Rename any folder, package, or file";
	public static final String PREF_SEND_PASTE_FROM_COPY = "Paste any folder or file from COPY";
	public static final String PREF_SEND_PASTE_FROM_CUT = "Paste any folder or file from CUT";
	public static final String PREF_SEND_DELETE_ITEM = "Delete any folder, package, or file";
	public static final String PREF_SEND_CREATE_TEST_CASE = "Create Test Case";
	public static final String PREF_SEND_CREATE_TEST_SUITE = "Create Test Suite";
	public static final String PREF_SEND_CREATE_TEST_DATA = "Create Data File (Test Data)";
	public static final String PREF_SEND_CREATE_TEST_OBJECT = "Create Test Object (in Object Repository)";
	public static final String PREF_SEND_CREATE_KEYWORD = "Create Keyword";
	public static final String PREF_SEND_CREATE_FOLDER = "Create Folder";
	public static final String PREF_SEND_CREATE_PACKAGE = "Create Keyword Package";
	
	// SlackUtil
	public static final String UTIL_ERROR_MSG_PLS_CHK_INTERNET_CONNECTION = "[ERROR][Team Collaboration] Cannot send message to Slack. Please check your Internet connection!";
	public static final String UTIL_MSG_PREFIX_TEAM_COLLABORATION = "[Team Collaboration]";
	public static final String UTIL_SENDING_MSG_PREFIX = UTIL_MSG_PREFIX_TEAM_COLLABORATION + "[SENDING] ";
	public static final String UTIL_SUCCESS_MSG_PREFIX = UTIL_MSG_PREFIX_TEAM_COLLABORATION + "[SENT] ";
	public static final String UTIL_ERROR_MSG_PREFIX = UTIL_MSG_PREFIX_TEAM_COLLABORATION + "[ERROR] ";
	
	// SlackSendMsgHandler
	public static final String EMOJI_MSG_OPEN_PROJECT = ":open_file_folder:[Open project] {0}";
	public static final String EMOJI_MSG_CLOSE_PROJECT = ":heavy_multiplication_x:[Close project] {0}";
	public static final String EMOJI_MSG_NEW = ":new:[Create] {0}";
	public static final String EMOJI_MSG_DELETE = ":x:[Delete] {0}";
	public static final String EMOJI_MSG_DELETE_FOLDER = ":x:[Delete] {0} and all its belongings";
	public static final String EMOJI_MSG_UPDATE = ":white_check_mark:[Update] {0}";
	public static final String EMOJI_MSG_RENAME = ":pencil2:[Rename] {0} to {1}";
	public static final String EMOJI_MSG_COPY = ":heavy_plus_sign:[Copy] {0} to {1}";
	public static final String EMOJI_MSG_MOVE = ":arrow_right:[Move] {0} to {1}";

	// Slack Error Messages
	public static final String SLACK_ERROR_MSG_CHANNEL_NOT_FOUND = "Value passed for Channel/Group was invalid.";
	public static final String SLACK_ERROR_MSG_NOT_IN_CHANNEL = "Cannot post user messages to a channel/group they are not in.";
	public static final String SLACK_ERROR_MSG_IS_ARCHIVED = "Channel/Group has been archived.";
	public static final String SLACK_ERROR_MSG_MSG_TOO_LONG = "Message text is too long";
	public static final String SLACK_ERROR_MSG_NO_TEXT = "No message text provided";
	public static final String SLACK_ERROR_MSG_RATE_LIMITED = "Application has posted too many messages.";
	public static final String SLACK_ERROR_MSG_NOT_AUTHED = "No authentication token provided.";
	public static final String SLACK_ERROR_MSG_INVALID_AUTH = "Invalid authentication token.";
	public static final String SLACK_ERROR_MSG_ACCOUNT_INACTIVE = "Authentication token is for a deleted user or team.";
}
