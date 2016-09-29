package com.kms.katalon.integration.kobiton.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.integration.kobiton.constants.KobitonPreferenceConstants;
import com.kms.katalon.integration.kobiton.constants.KobitonStringConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class KobitonPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    private static final boolean DEFAULT_KOBITON_INTEGRATION_ENABLE = false;

    private static final String DEFAULT_KOBITON_AUTHENTICATION_PASSWORD = "";

    private static final String DEFAULT_KOBITON_AUTHENTICATION_USERNAME = "";

    private static final String DEFAULT_KOBITON_AUTHENTICATION_TOKEN = "";

    private static final boolean DEFAULT_KOBITON_AUTHENTICATION_REMEMBER = true;

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = PreferenceStoreManager.getPreferenceStore(KobitonPreferenceConstants.KOBITON_QUALIFIER);
        store.setDefault(KobitonPreferenceConstants.KOBITON_INTEGRATION_ENABLE, DEFAULT_KOBITON_INTEGRATION_ENABLE);
        store.setDefault(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_REMEBER,
                DEFAULT_KOBITON_AUTHENTICATION_REMEMBER);
        store.setDefault(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_USERNAME,
                DEFAULT_KOBITON_AUTHENTICATION_USERNAME);
        store.setDefault(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_PASSWORD,
                DEFAULT_KOBITON_AUTHENTICATION_PASSWORD);
        store.setDefault(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_TOKEN, DEFAULT_KOBITON_AUTHENTICATION_TOKEN);
        store.setDefault(KobitonPreferenceConstants.KOBITON_SERVER_ENDPOINT,
                KobitonStringConstants.KOBITON_SERVER_TARGET_ENDPOINT);
    }
}
