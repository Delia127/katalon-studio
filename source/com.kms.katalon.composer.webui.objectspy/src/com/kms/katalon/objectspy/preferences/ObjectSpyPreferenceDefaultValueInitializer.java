package com.kms.katalon.objectspy.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ObjectSpyPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final String WEBUI_OBJECTSPY_DEFAULT_BROWSER = WebUIDriverType.FIREFOX_DRIVER.toString();

    public static final String[] SUPPORTED_BROWSERS = new String[] { WebUIDriverType.CHROME_DRIVER.toString(),
            WebUIDriverType.FIREFOX_DRIVER.toString(), WebUIDriverType.IE_DRIVER.toString() };

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER);
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_DIA_CREATE_FOLDER_AS_PAGE_NAME, true);
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_BROWSER, WEBUI_OBJECTSPY_DEFAULT_BROWSER);
    }
}
