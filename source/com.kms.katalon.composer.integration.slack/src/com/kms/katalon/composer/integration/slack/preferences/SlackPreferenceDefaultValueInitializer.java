package com.kms.katalon.composer.integration.slack.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.constants.PreferenceConstants;

public class SlackPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.IntegrationSlackPreferenceConstants.QUALIFIER);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_ENABLED, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_AUTH_TOKEN, "");
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_CHANNEL_GROUP, "");
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_AS_USER, true);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_USERNAME, "");
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_OPEN_PROJECT, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CLOSE_PROJECT, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_CASE, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_SUITE, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_DATA, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_OBJECT, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_RENAME_ITEM, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_PASTE_FROM_COPY, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_PASTE_FROM_CUT, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_DELETE_ITEM, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_CASE, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_SUITE, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_DATA, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_OBJECT, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_KEYWORD, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_FOLDER, false);
		store.setDefault(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_PACKAGE, false);
	}

}
