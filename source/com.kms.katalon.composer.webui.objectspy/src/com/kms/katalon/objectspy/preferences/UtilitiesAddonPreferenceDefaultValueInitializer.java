package com.kms.katalon.objectspy.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.objectspy.constants.UtilitiesAddonPreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class UtilitiesAddonPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    private static final boolean UTILITIES_ADDON_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN_DEFAULT = false;

    public static final int UTILITIES_ADDON_INSTANT_BROWSERS_PORT_DEFAULT = 50000;

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getPreferenceStore(UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_QUALIFIER);
        store.setDefault(UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_ACTIVE_BROWSER_PORT,
                UTILITIES_ADDON_INSTANT_BROWSERS_PORT_DEFAULT);
        store.setDefault(UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_ACTIVE_BROWSER_DO_NOT_SHOW_AGAIN,
                UTILITIES_ADDON_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN_DEFAULT);
    }
}
