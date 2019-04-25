package com.kms.katalon.execution.preferences;

import java.io.IOException;

import com.kms.katalon.core.model.SSLSettings;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.constants.SSLPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class SSLPreferences {
    public static SSLSettings getSSLSettings() {
        ScopedPreferenceStore store = getPreferenceStore();
        SSLSettings settings = new SSLSettings();
        settings.setKeyStoreFile(store.getString(SSLPreferenceConstants.KEYSTORE));
        settings.setKeyStorePassword(store.getString(SSLPreferenceConstants.KEYSTORE_PASSWORD));
        return settings;
    }
    
    public static void saveSSLSettings(SSLSettings settings) throws IOException {
        ScopedPreferenceStore store = getPreferenceStore();
        store.setValue(SSLPreferenceConstants.KEYSTORE, settings.getKeyStoreFile());
        store.setValue(SSLPreferenceConstants.KEYSTORE_PASSWORD, settings.getKeyStorePassword());
        store.save();
    }
    
    private static ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
    }
}
