package com.kms.katalon.objectspy.preferences;

import com.google.gson.Gson;
import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.objectspy.websocket.AddonHotKeyConfig;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ObjectSpyPreferences {
    public static AddonHotKeyConfig getCaptureObjectHotKey() {
        return new Gson().fromJson(
                getPreferenceStore().getString(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_HK_CAPTURE_OBJECT),
                AddonHotKeyConfig.class);
    }

    public static AddonHotKeyConfig getLoadDomMapHotKey() {
        return new Gson().fromJson(
                getPreferenceStore().getString(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_HK_LOAD_DOM_MAP),
                AddonHotKeyConfig.class);
    }

    private static ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER);
    }
}
