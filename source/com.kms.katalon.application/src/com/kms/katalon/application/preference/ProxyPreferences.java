package com.kms.katalon.application.preference;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.application.constants.ProxyPreferenceConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.network.ProxyOption;
import com.kms.katalon.core.network.ProxyServerType;
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
        proxyInfo.setProxyOption(StringUtils.defaultIfEmpty(store.getString(ProxyPreferenceConstants.PROXY_OPTION),
                ProxyOption.NO_PROXY.name()));
        proxyInfo.setProxyServerType(StringUtils.defaultIfEmpty(
                store.getString(ProxyPreferenceConstants.PROXY_SERVER_TYPE), ProxyServerType.HTTP.name()));
        proxyInfo.setProxyServerAddress(store.getString(ProxyPreferenceConstants.PROXY_SERVER_ADDRESS));
        proxyInfo.setProxyServerPort(store.getInt(ProxyPreferenceConstants.PROXY_SERVER_PORT));
        proxyInfo.setUsername(store.getString(ProxyPreferenceConstants.PROXY_USERNAME));
        proxyInfo.setPassword(store.getString(ProxyPreferenceConstants.PROXY_PASSWORD));
        proxyInfo.setExceptionList(store.getString(ProxyPreferenceConstants.PROXY_EXCEPTION_LIST));
        return proxyInfo;
    }

    public static void saveProxyInformation(ProxyInformation proxyInfo) throws IOException {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(ProxyPreferenceConstants.PROXY_OPTION, proxyInfo.getProxyOption());
        store.setValue(ProxyPreferenceConstants.PROXY_SERVER_TYPE, proxyInfo.getProxyServerType());
        store.setValue(ProxyPreferenceConstants.PROXY_SERVER_ADDRESS, proxyInfo.getProxyServerAddress());
        store.setValue(ProxyPreferenceConstants.PROXY_SERVER_PORT, proxyInfo.getProxyServerPort());
        store.setValue(ProxyPreferenceConstants.PROXY_USERNAME, proxyInfo.getUsername());
        store.setValue(ProxyPreferenceConstants.PROXY_PASSWORD, proxyInfo.getPassword());
        store.setValue(ProxyPreferenceConstants.PROXY_PREFERENCE_SET, true);
        ((ScopedPreferenceStore) store).save();
    }

    private static ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_EXECUTION_BUNDLE_ID);
    }
}
