package com.kms.katalon.composer.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.constants.PreferenceConstants.IPluginPreferenceConstants;

public class GeneralPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final boolean AUTO_RESTORE_PREVIOUS_SESSION = false;

    @Override
    public void initializeDefaultPreferences() {
        PlatformUI.getPreferenceStore().setDefault(IPluginPreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION,
                AUTO_RESTORE_PREVIOUS_SESSION);
    }

}
