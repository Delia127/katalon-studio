package com.kms.katalon.preferences.internal;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceStoreProvider implements IPreferenceStoreProvider {

	public PreferenceStoreProvider() {
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE,
				"com.kms.katalon.preferences");
	}

}
