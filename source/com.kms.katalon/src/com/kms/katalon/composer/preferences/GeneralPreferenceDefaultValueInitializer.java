package com.kms.katalon.composer.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.constants.PreferenceConstants;

public class GeneralPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final boolean AUTO_RESTORE_PREVIOUS_SESSION = true;

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        prefStore.setDefault(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION,
                AUTO_RESTORE_PREVIOUS_SESSION);
        prefStore.setDefault(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION, true);
        prefStore.setDefault(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP, true);
        prefStore.setDefault(PreferenceConstants.GENERAL_LAST_HELP_SELECTED_TAB, 1);
    }

}
