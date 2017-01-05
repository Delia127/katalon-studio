package com.kms.katalon.objectspy.util;

import com.kms.katalon.objectspy.constants.UtilitiesAddonPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class UtilitiesAddonUtil {
    public static ScopedPreferenceStore getUtilitiesAddonPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_QUALIFIER);
    }
    
    public static boolean isNotShowingInstantBrowserDialog() {
        return getUtilitiesAddonPreferenceStore().getBoolean(
                UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_ACTIVE_BROWSER_DO_NOT_SHOW_AGAIN);
    }

    public static void setNotShowingInstantBrowserDialog(boolean toogleState) {
        getUtilitiesAddonPreferenceStore().setValue(
                UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_ACTIVE_BROWSER_DO_NOT_SHOW_AGAIN, toogleState);
    }
    
    public static int getInstantBrowsersPort() {
        return getUtilitiesAddonPreferenceStore().getInt(UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_ACTIVE_BROWSER_PORT);
    }
}
