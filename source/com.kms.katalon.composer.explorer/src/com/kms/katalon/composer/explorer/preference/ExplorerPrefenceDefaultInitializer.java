package com.kms.katalon.composer.explorer.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ExplorerPrefenceDefaultInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExplorerPreferenceConstants.QUALIFIER);
        store.setDefault(PreferenceConstants.ExplorerPreferenceConstants.EXPLORER_LINK_WITH_PART, false);
    }

}
