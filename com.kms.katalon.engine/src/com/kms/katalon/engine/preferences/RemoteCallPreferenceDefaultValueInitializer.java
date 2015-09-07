package com.kms.katalon.engine.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.constants.PreferenceConstants;


public class RemoteCallPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

	private static final int REMOTE_CALL_NUMBER_OF_REMOTE_CALL_DEFAULT_VALUE = 2;
	private static final boolean REMOTE_CALL_IS_ALLOWED_DEFAULT_VALUE = true;

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE, PreferenceConstants.EnginePreferenceConstants.QUALIFIER);
	
		store.setDefault(PreferenceConstants.EnginePreferenceConstants.REMOTE_CALL_IS_ALLOWED, REMOTE_CALL_IS_ALLOWED_DEFAULT_VALUE);
		store.setDefault(PreferenceConstants.EnginePreferenceConstants.REMOTE_CALL_NUMBER_OF_REMOTE_CALL, REMOTE_CALL_NUMBER_OF_REMOTE_CALL_DEFAULT_VALUE);
	}

}
