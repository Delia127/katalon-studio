package com.kms.katalon.engine.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.constants.PreferenceConstants;


public class HostPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

	private static final String HOST_CONFIG_SUPPORTED_BROWSER_DEFAULT_VALUE = "IE9, Chrome";
	private static final String HOST_CONFIG_HOST_IP_DEFAULT_VALUE = "192.168.1.2";

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE, PreferenceConstants.EnginePreferenceConstants.QUALIFIER);
	
		store.setDefault(PreferenceConstants.EnginePreferenceConstants.HOST_CONFIG_HOST_IP, HOST_CONFIG_HOST_IP_DEFAULT_VALUE);
		store.setDefault(PreferenceConstants.EnginePreferenceConstants.HOST_CONFIG_HOST_NAME, "");
		store.setDefault(PreferenceConstants.EnginePreferenceConstants.HOST_CONFIG_SUPPORTED_BROWSER, HOST_CONFIG_SUPPORTED_BROWSER_DEFAULT_VALUE);
	}

}
