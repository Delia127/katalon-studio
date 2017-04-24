package com.kms.katalon.execution.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.constants.ProxyPreferenceConstants;
import com.kms.katalon.execution.entity.ProxyOption;
import com.kms.katalon.execution.entity.ProxyServerType;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class ProxyPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = PreferenceStoreManager
                .getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
        store.setDefault(ProxyPreferenceConstants.PROXY_OPTION, ProxyOption.NO_PROXY.getDisplayName());
        store.setDefault(ProxyPreferenceConstants.PROXY_SERVER_TYPE, ProxyServerType.HTTP.toString());
        store.setDefault(ProxyPreferenceConstants.PROXY_SERVER_ADDRESS, "");
        store.setDefault(ProxyPreferenceConstants.PROXY_SERVER_PORT, 0);
        store.setDefault(ProxyPreferenceConstants.PROXY_USERNAME, "");
        store.setDefault(ProxyPreferenceConstants.PROXY_PASSWORD, "");
        store.setDefault(ProxyPreferenceConstants.PROXY_PREFERENCE_SET, false);
    }
}
