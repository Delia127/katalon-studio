package com.kms.katalon.composer.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class GeneralPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final boolean AUTO_RESTORE_PREVIOUS_SESSION = true;

    public static ScopedPreferenceStore getGeneralStore() {
        return getPreferenceStore(GeneralPreferenceDefaultValueInitializer.class);
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore prefStore = getGeneralStore();
        prefStore.setDefault(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION,
                AUTO_RESTORE_PREVIOUS_SESSION);
        prefStore.setDefault(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION, true);
        prefStore.setDefault(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP, true);
        prefStore.setDefault(PreferenceConstants.GENERAL_LAST_HELP_SELECTED_TAB, 1);
        prefStore.setDefault(PreferenceConstants.GENERAL_SHOW_USER_FEEDBACK_DIALOG_ON_APP_CLOSE, true);
        prefStore.setDefault(PreferenceConstants.GENERAL_SHOW_IN_APP_SURVEY_DIALOG_ON_APP_FIRST_CLOSE, true);
        prefStore.setDefault(PreferenceConstants.GENERAL_SHOW_WALKTHROUGH_DIALOG, true);
        prefStore.setDefault(PreferenceConstants.GENERAL_NUMBER_OF_APP_CLOSES, 0);
        prefStore.setDefault(PreferenceConstants.PLUGIN_DIRECTORY, "");
        save();
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
            ((IPersistentPreferenceStore) getGeneralStore()).save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

}
