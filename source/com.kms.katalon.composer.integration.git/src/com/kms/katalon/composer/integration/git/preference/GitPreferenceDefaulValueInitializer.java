package com.kms.katalon.composer.integration.git.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.composer.integration.git.constants.GitPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

@SuppressWarnings("restriction")
public class GitPreferenceDefaulValueInitializer extends AbstractPreferenceInitializer {
    private static final boolean GIT_INTERGRATION_ENABLE_DEFAULT_VALUE = false;

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = PreferenceStoreManager.getPreferenceStore(GitPreferenceDefaulValueInitializer.class);
        store.setDefault(GitPreferenceConstants.GIT_INTERGRATION_ENABLE, GIT_INTERGRATION_ENABLE_DEFAULT_VALUE);
        store.setDefault(UIPreferences.REMOTE_CONNECTION_TIMEOUT,
                Activator.getDefault().getPreferenceStore().getDefaultInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT));
    }

}
