package com.kms.katalon.preferences.internal;

import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceStoreProvider implements IPreferenceStoreProvider {

    public PreferenceStoreProvider() {
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(PreferenceStoreProvider.class);
    }

}
