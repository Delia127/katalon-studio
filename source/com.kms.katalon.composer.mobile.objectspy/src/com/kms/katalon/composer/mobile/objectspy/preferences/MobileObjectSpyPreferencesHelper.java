package com.kms.katalon.composer.mobile.objectspy.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;

import com.kms.katalon.composer.mobile.objectspy.constant.PreferencesConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class MobileObjectSpyPreferencesHelper {
    private ScopedPreferenceStore getStore() {
        return getPreferenceStore(getClass());
    }
    
    public String getLastAppFile() {
        return getStore().getString(PreferencesConstants.PREF_LAST_APP_FILE);
    }
    
    public void setLastAppFile(String filePath) {
        getStore().setValue(PreferencesConstants.PREF_LAST_APP_FILE, filePath);
    }
    
    public void save() throws IOException {
        getStore().save();
    }
}
