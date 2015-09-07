package com.kms.katalon.dal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;


public class MailPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

	private static final String MAIL_CONFIG_PASSWORD_DEFAULT_VALUE = "testemail123";
	private static final String MAIL_CONFIG_USERNAME_DEFAULT_VALUE = "testemailkms@gmail.com";
	private static final String MAIL_CONFIG_PORT_DEFAULT_VALUE = "465";
	private static final String MAIL_CONFIG_HOST_DEFAULT_VALUE = "smtp.gmail.com";

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE, PreferenceConstants.DALPreferenceConstans.QUALIFIER);
	
		store.setDefault(PreferenceConstants.DALPreferenceConstans.MAIL_CONFIG_HOST, MAIL_CONFIG_HOST_DEFAULT_VALUE);
		store.setDefault(PreferenceConstants.DALPreferenceConstans.MAIL_CONFIG_PORT, MAIL_CONFIG_PORT_DEFAULT_VALUE);
		store.setDefault(PreferenceConstants.DALPreferenceConstans.MAIL_CONFIG_USERNAME, MAIL_CONFIG_USERNAME_DEFAULT_VALUE);
		store.setDefault(PreferenceConstants.DALPreferenceConstans.MAIL_CONFIG_PASSWORD, MAIL_CONFIG_PASSWORD_DEFAULT_VALUE);
		store.setDefault(PreferenceConstants.DALPreferenceConstans.MAIL_CONFIG_REPORT_RECIPIENTS, "");
	}

}
