package com.kms.katalon.objectspy.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ObjectSpyPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    private static final boolean WEBUI_OBJECTSPY_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN_DEFAULT = false;
    public static final int WEBUI_OBJECTSPY_INSTANT_BROWSERS_PORT_DEFAULT = 50000;

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER);
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_INSTANT_BROWSER_PORT, WEBUI_OBJECTSPY_INSTANT_BROWSERS_PORT_DEFAULT);
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN, WEBUI_OBJECTSPY_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN_DEFAULT);
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_DIA_CREATE_FOLDER_AS_PAGE_NAME, true);
    }
}
