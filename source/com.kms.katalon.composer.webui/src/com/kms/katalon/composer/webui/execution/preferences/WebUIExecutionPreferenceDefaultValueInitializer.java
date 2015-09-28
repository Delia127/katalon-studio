package com.kms.katalon.composer.webui.execution.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class WebUIExecutionPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final int EXECUTION_DEFAULT_WAIT_FOR_IE_HANGING = 600;

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.WebUiPreferenceConstants.QUALIFIER);
        store.setDefault(PreferenceConstants.WebUiPreferenceConstants.EXECUTION_WAIT_FOR_IE_HANGING,
        		EXECUTION_DEFAULT_WAIT_FOR_IE_HANGING);
    }
}
