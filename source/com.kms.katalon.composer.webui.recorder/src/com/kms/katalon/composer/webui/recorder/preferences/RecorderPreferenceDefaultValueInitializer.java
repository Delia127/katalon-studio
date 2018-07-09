package com.kms.katalon.composer.webui.recorder.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.composer.webui.recorder.constants.RecorderPreferenceConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class RecorderPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final int WEBUI_RECORDER_ACTIVE_BROWSERS_PORT_DEFAULT = 50000;

    public static final boolean WEBUI_RECORDER_ACTIVE_BROWSER_DO_NOT_SHOW_AGAIN_DEFAULT = false;

    public static final String WEBUI_RECORDER_DEFAULT_BROWSER = WebUIDriverType.FIREFOX_DRIVER.toString();
    
    public static final String WEBUI_RECORDER_DEFAULT_URL = "http://demoaut.katalon.com/";

    public static final String[] SUPPORTED_BROWSERS = new String[] { WebUIDriverType.CHROME_DRIVER.toString(),
            WebUIDriverType.FIREFOX_DRIVER.toString(), WebUIDriverType.IE_DRIVER.toString() };

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getPreferenceStore(RecorderPreferenceConstants.WEBUI_RECORDER_QUALIFIER);
        store.setDefault(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_BROWSER, WEBUI_RECORDER_DEFAULT_BROWSER);
        store.setDefault(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_URL, WEBUI_RECORDER_DEFAULT_URL);
        store.setDefault(RecorderPreferenceConstants.WEBUI_RECORDER_PIN_WINDOW, true);
    }
}
