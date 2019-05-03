package com.kms.katalon.composer.webui.preferences;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webui.constants.PreferenceConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class WebUiPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

    public static ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_WEB_UI_BUNDLE_ID);
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore prefStore = getPreferenceStore();
        prefStore.setDefault(PreferenceConstants.AUTO_UPDATE_WEBDRIVERS, true);
        save();
    }
    
    public void save() {
        try {
            getPreferenceStore().save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }
}
