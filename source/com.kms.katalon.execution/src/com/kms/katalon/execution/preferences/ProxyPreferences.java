package com.kms.katalon.execution.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.constants.ProxyPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ProxyPreferences {
    public static boolean isProxyPreferencesSet() {
        IPreferenceStore store = getPreferenceStore();
        return store.getBoolean(ProxyPreferenceConstants.PROXY_PREFERENCE_SET);
    }
    
    public static ProxyInformation getProxyInformation() {
        IPreferenceStore store = getPreferenceStore();
        ProxyInformation proxyInfo = new ProxyInformation();
        proxyInfo.setProxyOption(store.getString(ProxyPreferenceConstants.PROXY_OPTION));
        proxyInfo.setProxyServerType(store.getString(ProxyPreferenceConstants.PROXY_SERVER_TYPE));
        proxyInfo.setProxyServerAddress(store.getString(ProxyPreferenceConstants.PROXY_SERVER_ADDRESS));
        proxyInfo.setProxyServerPort(store.getInt(ProxyPreferenceConstants.PROXY_SERVER_PORT));
        proxyInfo.setUsername(store.getString(ProxyPreferenceConstants.PROXY_USERNAME));
        proxyInfo.setPassword(store.getString(ProxyPreferenceConstants.PROXY_PASSWORD));
        return proxyInfo;
    }

    public static void saveProxyInformation(ProxyInformation proxyInfo) {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(ProxyPreferenceConstants.PROXY_OPTION, proxyInfo.getProxyOption());
        store.setValue(ProxyPreferenceConstants.PROXY_SERVER_TYPE, proxyInfo.getProxyServerType());
        store.setValue(ProxyPreferenceConstants.PROXY_SERVER_ADDRESS, proxyInfo.getProxyServerAddress());
        store.setValue(ProxyPreferenceConstants.PROXY_SERVER_PORT, proxyInfo.getProxyServerPort());
        store.setValue(ProxyPreferenceConstants.PROXY_USERNAME, proxyInfo.getUsername());
        store.setValue(ProxyPreferenceConstants.PROXY_PASSWORD, proxyInfo.getPassword());
        store.setValue(ProxyPreferenceConstants.PROXY_PREFERENCE_SET, true);
    }

    private static ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
    }
}
