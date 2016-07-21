package com.kms.katalon.composer.mobile.objectspy.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.composer.mobile.objectspy.constant.PreferencesConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class MobileObjectSpyPreferencesInitilizer extends AbstractPreferenceInitializer {
    
    private ScopedPreferenceStore getStore() {
        return getPreferenceStore(MobileObjectSpyPreferencesInitilizer.class);
    }

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getStore();
        store.setDefault(PreferencesConstants.PREF_LAST_APP_FILE, StringUtils.EMPTY);
    }

}
