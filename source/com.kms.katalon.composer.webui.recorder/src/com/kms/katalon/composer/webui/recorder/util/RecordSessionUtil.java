package com.kms.katalon.composer.webui.recorder.util;

import com.kms.katalon.composer.webui.recorder.constants.RecorderPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class RecordSessionUtil {
    public static boolean isNotShowingInstantBrowserDialog() {
        return getPreferenceStore().getBoolean(
                RecorderPreferenceConstants.WEBUI_RECORDER_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN);
    }

    public static void setNotShowingInstantBrowserDialog(boolean toogleState) {
        getPreferenceStore().setValue(
                RecorderPreferenceConstants.WEBUI_RECORDER_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN, toogleState);
    }

    private static ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(RecorderPreferenceConstants.WEBUI_RECORDER_QUALIFIER);
    }
}
