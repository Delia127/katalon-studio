package com.kms.katalon.objectspy.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.swt.SWT;

import com.google.gson.Gson;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.objectspy.websocket.AddonHotKeyConfig;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ObjectSpyPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    private static final String DEMOAUT_KATALON = "http://demoaut.katalon.com/";
    
    private static final String DEFAULT_HANDLE_SAVING_OBJECT_CONFLICT = "MERGE_CHANGE_TO_EXISTING_OBJECT";

    public static final String WEBUI_OBJECTSPY_DEFAULT_BROWSER = WebUIDriverType.CHROME_DRIVER.toString();

    public static final String[] SUPPORTED_BROWSERS = new String[] { WebUIDriverType.CHROME_DRIVER.toString(),
            WebUIDriverType.FIREFOX_DRIVER.toString(), WebUIDriverType.IE_DRIVER.toString() };
    
    public static final int DEFAULT_KEY_CODE = (int) '`';
    
    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER);
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_DIA_CREATE_FOLDER_AS_PAGE_NAME, true);
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_DIA_CONFLICT_OPTION, DEFAULT_HANDLE_SAVING_OBJECT_CONFLICT);
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_BROWSER, WEBUI_OBJECTSPY_DEFAULT_BROWSER);
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_STARTING_URL, DEMOAUT_KATALON);
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_PIN_WINDOW, true);

        Gson gson = new Gson();
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_HK_CAPTURE_OBJECT,
                gson.toJson(new AddonHotKeyConfig(DEFAULT_KEY_CODE, SWT.ALT)));
        store.setDefault(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_HK_LOAD_DOM_MAP,
                gson.toJson(new AddonHotKeyConfig(DEFAULT_KEY_CODE, SWT.ALT | SWT.CTRL)));
    }
}
