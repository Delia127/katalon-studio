package com.kms.katalon.composer.webui.execution.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class WebUIExecutionPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final int EXECUTION_DEFAULT_WAIT_FOR_IE_HANGING = 600;

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getPreferenceStore(WebUIExecutionPreferenceDefaultValueInitializer.class);
        store.setDefault(PreferenceConstants.WEBUI_EXECUTION_WAIT_FOR_IE_HANGING, EXECUTION_DEFAULT_WAIT_FOR_IE_HANGING);
    }
}
