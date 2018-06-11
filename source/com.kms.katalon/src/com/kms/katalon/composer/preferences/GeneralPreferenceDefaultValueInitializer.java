package com.kms.katalon.composer.preferences;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.PreferenceConstants;

public class GeneralPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final boolean AUTO_RESTORE_PREVIOUS_SESSION = true;

    /**
     * @deprecated This code doesn't work when applying for PlatformUI
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        prefStore.setDefault(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION,
                AUTO_RESTORE_PREVIOUS_SESSION);
        prefStore.setDefault(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION, true);
        prefStore.setDefault(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP, true);
        prefStore.setDefault(PreferenceConstants.GENERAL_LAST_HELP_SELECTED_TAB, 1);
    }
    
    public void applyDefaultValues() {
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        if (isFirstTimeSetup()) {
            prefStore.setValue(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION,
                    AUTO_RESTORE_PREVIOUS_SESSION);
            prefStore.setValue(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION, true);
            prefStore.setValue(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP, true);
            prefStore.setValue(PreferenceConstants.GENERAL_LAST_HELP_SELECTED_TAB, 1);
            prefStore.setValue(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED, true);
            save();
        }
    }
    
    public boolean isFirstTimeSetup() {
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        if (prefStore.contains(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED)) {
            return prefStore.getBoolean(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED);
        }
        return false;
    }
    
    public void save() {
        try {
            ((IPersistentPreferenceStore) PlatformUI.getPreferenceStore()).save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

}
