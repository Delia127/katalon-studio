package com.kms.katalon.composer.webui.recorder.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.composer.webui.recorder.constants.RecorderPreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class RecorderPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final int WEBUI_RECORDER_INSTANT_BROWSERS_PORT_DEFAULT = 50000;
    private static final boolean WEBUI_RECORDER_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN_DEFAULT = false;

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getPreferenceStore(RecorderPreferenceConstants.WEBUI_RECORDER_QUALIFIER);
        store.setDefault(RecorderPreferenceConstants.WEBUI_RECORDER_INSTANT_BROWSER_PORT, WEBUI_RECORDER_INSTANT_BROWSERS_PORT_DEFAULT);
        store.setDefault(RecorderPreferenceConstants.WEBUI_RECORDER_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN, WEBUI_RECORDER_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN_DEFAULT);
    }
}
