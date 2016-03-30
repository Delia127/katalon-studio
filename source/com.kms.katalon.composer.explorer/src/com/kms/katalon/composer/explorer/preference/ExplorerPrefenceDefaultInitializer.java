package com.kms.katalon.composer.explorer.preference;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.composer.explorer.constants.ExplorerPreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ExplorerPrefenceDefaultInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getPreferenceStore(ExplorerPrefenceDefaultInitializer.class);
        store.setDefault(ExplorerPreferenceConstants.EXPLORER_LINK_WITH_PART, false);
    }

}
