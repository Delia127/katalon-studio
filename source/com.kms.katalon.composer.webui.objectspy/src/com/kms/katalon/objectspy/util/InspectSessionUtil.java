package com.kms.katalon.objectspy.util;

import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class InspectSessionUtil {
    public static boolean isNotShowingInstantBrowserDialog() {
        return getObjectSpyPreferenceStore().getBoolean(
                ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN);
    }

    public static void setNotShowingInstantBrowserDialog(boolean toogleState) {
        getObjectSpyPreferenceStore().setValue(
                ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN, toogleState);
    }

    public static ScopedPreferenceStore getObjectSpyPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER);
    }
}
