package com.kms.katalon.composer.integration.slack.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.composer.integration.slack.constants.SlackPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class SlackPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = PreferenceStoreManager
                .getPreferenceStore(SlackPreferenceDefaultValueInitializer.class);
        store.setDefault(SlackPreferenceConstants.SLACK_ENABLED, false);
        store.setDefault(SlackPreferenceConstants.SLACK_AUTH_TOKEN, "");
        store.setDefault(SlackPreferenceConstants.SLACK_CHANNEL_GROUP, "");
        store.setDefault(SlackPreferenceConstants.SLACK_AS_USER, true);
        store.setDefault(SlackPreferenceConstants.SLACK_USERNAME, "");
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_OPEN_PROJECT, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_CLOSE_PROJECT, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_CASE, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_SUITE, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_DATA, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_OBJECT, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_RENAME_ITEM, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_PASTE_FROM_COPY, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_PASTE_FROM_CUT, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_DELETE_ITEM, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_CASE, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_SUITE, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_DATA, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_OBJECT, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_CREATE_KEYWORD, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_CREATE_FOLDER, false);
        store.setDefault(SlackPreferenceConstants.SLACK_SEND_CREATE_PACKAGE, false);
    }

}
