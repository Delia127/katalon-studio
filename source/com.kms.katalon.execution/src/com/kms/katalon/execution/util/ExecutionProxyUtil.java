package com.kms.katalon.execution.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.network.ProxyOption;
import com.kms.katalon.execution.constants.ProxyPreferenceConstants;
import com.kms.katalon.execution.preferences.ProxyPreferences;

import joptsimple.OptionSet;

public class ExecutionProxyUtil {

    @Deprecated
    public static ProxyInformation getProxyInformation() {
        return ProxyPreferences.getProxyInformation();
    }

    public static ProxyInformation getAuthProxyInformation() {
        return ProxyPreferences.getAuthProxyInformation();
    }

    public static ProxyInformation getSystemProxyInformation() {
        return ProxyPreferences.getSystemProxyInformation();
    }

    public static boolean checkMixedProxies(OptionSet options) {
        return isUseLegacyProxyConfig(options) && isUseNewProxyConfig(options);
    }

    public static boolean isUseLegacyProxyConfig(OptionSet options) {
        @SuppressWarnings("serial")
        final List<String> legacyProxyOptions = new ArrayList<String>() {
            {
                add(ProxyPreferenceConstants.PROXY_OPTION);
                add(ProxyPreferenceConstants.PROXY_SERVER_TYPE);
                add(ProxyPreferenceConstants.PROXY_SERVER_ADDRESS);
                add(ProxyPreferenceConstants.PROXY_SERVER_PORT);
                add(ProxyPreferenceConstants.PROXY_USERNAME);
                add(ProxyPreferenceConstants.PROXY_PASSWORD);
                add(ProxyPreferenceConstants.PROXY_EXCEPTION_LIST);
            }
        };
        return legacyProxyOptions.stream().anyMatch(prop -> options.has(prop));
    }

    public static boolean isUseNewProxyConfig(OptionSet options) {
        return isUseAuthProxyConfig(options) || isUseSystemProxyConfig(options);
    }

    public static boolean isUseAuthProxyConfig(OptionSet options) {
        @SuppressWarnings("serial")
        final List<String> authProxyOptions = new ArrayList<String>() {
            {
                add(ProxyPreferenceConstants.AUTH_PROXY_OPTION);
                add(ProxyPreferenceConstants.AUTH_PROXY_SERVER_TYPE);
                add(ProxyPreferenceConstants.AUTH_PROXY_SERVER_ADDRESS);
                add(ProxyPreferenceConstants.AUTH_PROXY_SERVER_PORT);
                add(ProxyPreferenceConstants.AUTH_PROXY_USERNAME);
                add(ProxyPreferenceConstants.AUTH_PROXY_PASSWORD);
                add(ProxyPreferenceConstants.AUTH_PROXY_EXCEPTION_LIST);
            }
        };
        return authProxyOptions.stream().anyMatch(prop -> options.has(prop));
    }

    public static boolean isUseSystemProxyConfig(OptionSet options) {
        @SuppressWarnings("serial")
        final List<String> systemProxyOptions = new ArrayList<String>() {
            {
                add(ProxyPreferenceConstants.SYSTEM_PROXY_OPTION);
                add(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_TYPE);
                add(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_ADDRESS);
                add(ProxyPreferenceConstants.SYSTEM_PROXY_SERVER_PORT);
                add(ProxyPreferenceConstants.SYSTEM_PROXY_USERNAME);
                add(ProxyPreferenceConstants.SYSTEM_PROXY_PASSWORD);
                add(ProxyPreferenceConstants.SYSTEM_PROXY_EXCEPTION_LIST);
                add(ProxyPreferenceConstants.SYSTEM_PROXY_APPLY_TO_DESIRED_CAPABILITIES);
            }
        };
        return systemProxyOptions.stream().anyMatch(prop -> options.has(prop));
    }

    public static void useLegacyProxyConfig() {
        IPreferenceStore store = ProxyPreferences.getPreferenceStore();
        store.setValue(ProxyPreferenceConstants.PROXY_PREFERENCE_SET, true);
        store.setValue(ProxyPreferenceConstants.SYSTEM_PROXY_PREFERENCE_SET, false);
        store.setValue(ProxyPreferenceConstants.AUTH_PROXY_PREFERENCE_SET, false);
    }

    public static void useNewProxyConfig() {
        IPreferenceStore store = ProxyPreferences.getPreferenceStore();
        store.setValue(ProxyPreferenceConstants.PROXY_PREFERENCE_SET, false);
        store.setValue(ProxyPreferenceConstants.SYSTEM_PROXY_PREFERENCE_SET, true);
        store.setValue(ProxyPreferenceConstants.AUTH_PROXY_PREFERENCE_SET, true);
    }

    public static boolean isConfigProxy() {
        return isConfigProxy(getProxyInformation())
                || isConfigProxy(getAuthProxyInformation())
                || isConfigProxy(getSystemProxyInformation());
    }

    public static boolean isConfigProxy(ProxyInformation proxyInfo) {
        return !ProxyOption.NO_PROXY.name().equalsIgnoreCase(proxyInfo.getProxyOption());
    }
}
