package com.kms.katalon.execution.preferences;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.network.ProxyOption;
import com.kms.katalon.core.network.ProxyServerType;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.constants.ProxyPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ProxyPreferences {
    @Deprecated
    public static boolean isProxyPreferencesSet() {
        IPreferenceStore store = getPreferenceStore();
        return store.getBoolean(ProxyPreferenceConstants.PROXY_PREFERENCE_SET);
    }

    @Deprecated
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

    @Deprecated
    public static void saveProxyInformation(ProxyInformation proxyInfo) throws IOException {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(ProxyPreferenceConstants.PROXY_OPTION, proxyInfo.getProxyOption());
        store.setValue(ProxyPreferenceConstants.PROXY_SERVER_TYPE, proxyInfo.getProxyServerType());
        store.setValue(ProxyPreferenceConstants.PROXY_SERVER_ADDRESS, proxyInfo.getProxyServerAddress());
        store.setValue(ProxyPreferenceConstants.PROXY_SERVER_PORT, proxyInfo.getProxyServerPort());
        store.setValue(ProxyPreferenceConstants.PROXY_USERNAME, proxyInfo.getUsername());
        store.setValue(ProxyPreferenceConstants.PROXY_PASSWORD, proxyInfo.getPassword());
        store.setValue(ProxyPreferenceConstants.PROXY_EXCEPTION_LIST, proxyInfo.getExceptionList());
        store.setValue(ProxyPreferenceConstants.PROXY_PREFERENCE_SET, true);
        ((ScopedPreferenceStore) store).save();
    }

    public static boolean isAuthProxyPreferencesSet() {
        IPreferenceStore store = getPreferenceStore();
        return store.getBoolean(ProxyPreferenceConstants.AUTH_PROXY_PREFERENCE_SET);
    }

    public static ProxyInformation getAuthProxyInformation() {
        if (!isAuthProxyPreferencesSet()) {
            return getProxyInformation();
        }

        IPreferenceStore store = getPreferenceStore();
        ProxyInformation proxyInfo = new ProxyInformation();
        proxyInfo.setProxyOption(StringUtils.defaultIfEmpty(store.getString(ProxyPreferenceConstants.AUTH_PROXY_OPTION),
                ProxyOption.NO_PROXY.name()));
        proxyInfo.setProxyServerType(StringUtils.defaultIfEmpty(
                store.getString(ProxyPreferenceConstants.AUTH_PROXY_SERVER_TYPE), ProxyServerType.HTTP.name()));
        proxyInfo.setProxyServerAddress(store.getString(ProxyPreferenceConstants.AUTH_PROXY_SERVER_ADDRESS));
        proxyInfo.setProxyServerPort(store.getInt(ProxyPreferenceConstants.AUTH_PROXY_SERVER_PORT));
        proxyInfo.setUsername(store.getString(ProxyPreferenceConstants.AUTH_PROXY_USERNAME));
        proxyInfo.setPassword(store.getString(ProxyPreferenceConstants.AUTH_PROXY_PASSWORD));
        proxyInfo.setExceptionList(store.getString(ProxyPreferenceConstants.AUTH_PROXY_EXCEPTION_LIST));
        return proxyInfo;
    }

    public static void saveAuthProxyInformation(ProxyInformation proxyInfo) throws IOException {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(ProxyPreferenceConstants.AUTH_PROXY_OPTION, proxyInfo.getProxyOption());
        store.setValue(ProxyPreferenceConstants.AUTH_PROXY_SERVER_TYPE, proxyInfo.getProxyServerType());
        store.setValue(ProxyPreferenceConstants.AUTH_PROXY_SERVER_ADDRESS, proxyInfo.getProxyServerAddress());
        store.setValue(ProxyPreferenceConstants.AUTH_PROXY_SERVER_PORT, proxyInfo.getProxyServerPort());
        store.setValue(ProxyPreferenceConstants.AUTH_PROXY_USERNAME, proxyInfo.getUsername());
        store.setValue(ProxyPreferenceConstants.AUTH_PROXY_PASSWORD, proxyInfo.getPassword());
        store.setValue(ProxyPreferenceConstants.AUTH_PROXY_EXCEPTION_LIST, proxyInfo.getExceptionList());
        store.setValue(ProxyPreferenceConstants.AUTH_PROXY_PREFERENCE_SET, true);
        ((ScopedPreferenceStore) store).save();
    }

    public static boolean isSystemProxyPreferencesSet() {
        IPreferenceStore store = getPreferenceStore();
        return store.getBoolean(ProxyPreferenceConstants.SYSTEM_PROXY_PREFERENCE_SET);
    }

    public static ProxyInformation getSystemProxyInformation() {
        if (!isSystemProxyPreferencesSet()) {
            ProxyInformation proxyInfo = getProxyInformation();
            proxyInfo.setApplyToDesiredCapabilities(true);
            return proxyInfo;
        }

        IPreferenceStore store = getPreferenceStore();
        ProxyInformation proxyInfo = new ProxyInformation();
        proxyInfo.setProxyOption(StringUtils.defaultIfEmpty(
                store.getString(ProxyPreferenceConstants.SYSTEM_PROXY_OPTION), ProxyOption.NO_PROXY.name()));
        proxyInfo.setProxyServerType(StringUtils.defaultIfEmpty(
                store.getString(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_TYPE), ProxyServerType.HTTP.name()));
        proxyInfo.setProxyServerAddress(store.getString(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_ADDRESS));
        proxyInfo.setProxyServerPort(store.getInt(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_PORT));
        proxyInfo.setUsername(store.getString(ProxyPreferenceConstants.SYSTEM_PROXY_USERNAME));
        proxyInfo.setPassword(store.getString(ProxyPreferenceConstants.SYSTEM_PROXY_PASSWORD));
        proxyInfo.setExceptionList(store.getString(ProxyPreferenceConstants.SYSTEM_PROXY_EXCEPTION_LIST));
        proxyInfo.setApplyToDesiredCapabilities(
                store.getBoolean(ProxyPreferenceConstants.SYSTEM_PROXY_APPLY_TO_DESIRED_CAPABILITIES));
        return proxyInfo;
    }

    public static void saveSystemProxyInformation(ProxyInformation proxyInfo) throws IOException {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(ProxyPreferenceConstants.SYSTEM_PROXY_OPTION, proxyInfo.getProxyOption());
        store.setValue(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_TYPE, proxyInfo.getProxyServerType());
        store.setValue(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_ADDRESS, proxyInfo.getProxyServerAddress());
        store.setValue(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_PORT, proxyInfo.getProxyServerPort());
        store.setValue(ProxyPreferenceConstants.SYSTEM_PROXY_USERNAME, proxyInfo.getUsername());
        store.setValue(ProxyPreferenceConstants.SYSTEM_PROXY_PASSWORD, proxyInfo.getPassword());
        store.setValue(ProxyPreferenceConstants.SYSTEM_PROXY_EXCEPTION_LIST, proxyInfo.getExceptionList());
        store.setValue(ProxyPreferenceConstants.SYSTEM_PROXY_APPLY_TO_DESIRED_CAPABILITIES,
                proxyInfo.isApplyToDesiredCapabilities());
        store.setValue(ProxyPreferenceConstants.SYSTEM_PROXY_PREFERENCE_SET, true);
        ((ScopedPreferenceStore) store).save();
    }

    public static ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
    }
}
