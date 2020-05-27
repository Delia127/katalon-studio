package com.kms.katalon.execution.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.core.network.ProxyOption;
import com.kms.katalon.core.network.ProxyServerType;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.constants.ProxyPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class ProxyPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

    public static final int PROXY_SERVER_PORT_DEFAULT_VALUE = 0;

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = PreferenceStoreManager
                .getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
        store.setDefault(ProxyPreferenceConstants.PROXY_OPTION, ProxyOption.NO_PROXY.name());
        store.setDefault(ProxyPreferenceConstants.PROXY_SERVER_TYPE, ProxyServerType.HTTP.name());
        store.setDefault(ProxyPreferenceConstants.PROXY_SERVER_ADDRESS, "");
        store.setDefault(ProxyPreferenceConstants.PROXY_SERVER_PORT, PROXY_SERVER_PORT_DEFAULT_VALUE);
        store.setDefault(ProxyPreferenceConstants.PROXY_USERNAME, "");
        store.setDefault(ProxyPreferenceConstants.PROXY_PASSWORD, "");
        store.setDefault(ProxyPreferenceConstants.PROXY_EXCEPTION_LIST, "");
        store.setDefault(ProxyPreferenceConstants.PROXY_PREFERENCE_SET, false);

        store.setDefault(ProxyPreferenceConstants.AUTH_PROXY_OPTION, ProxyOption.NO_PROXY.name());
        store.setDefault(ProxyPreferenceConstants.AUTH_PROXY_SERVER_TYPE, ProxyServerType.HTTP.name());
        store.setDefault(ProxyPreferenceConstants.AUTH_PROXY_SERVER_ADDRESS, "");
        store.setDefault(ProxyPreferenceConstants.AUTH_PROXY_SERVER_PORT, PROXY_SERVER_PORT_DEFAULT_VALUE);
        store.setDefault(ProxyPreferenceConstants.AUTH_PROXY_USERNAME, "");
        store.setDefault(ProxyPreferenceConstants.AUTH_PROXY_PASSWORD, "");
        store.setDefault(ProxyPreferenceConstants.AUTH_PROXY_EXCEPTION_LIST, "");
        store.setDefault(ProxyPreferenceConstants.AUTH_PROXY_PREFERENCE_SET, false);

        store.setDefault(ProxyPreferenceConstants.SYSTEM_PROXY_OPTION, ProxyOption.NO_PROXY.name());
        store.setDefault(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_TYPE, ProxyServerType.HTTP.name());
        store.setDefault(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_ADDRESS, "");
        store.setDefault(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_PORT, PROXY_SERVER_PORT_DEFAULT_VALUE);
        store.setDefault(ProxyPreferenceConstants.SYSTEM_PROXY_USERNAME, "");
        store.setDefault(ProxyPreferenceConstants.SYSTEM_PROXY_PASSWORD, "");
        store.setDefault(ProxyPreferenceConstants.SYSTEM_PROXY_EXCEPTION_LIST, "");
        store.setDefault(ProxyPreferenceConstants.SYSTEM_PROXY_PREFERENCE_SET, false);
    }
}
