package com.kms.katalon.execution.util;

import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.logging.LogUtil;

public class ProxyInformationUtil {

    public static void printCurrentProxyInformation() {
        if (!ExecutionProxyUtil.isConfigProxy()) {
            return;
        }

        LogUtil.logInfo("INFO: [Proxy Configurations]");
        boolean isUseAuthProxy = ProxyPreferences.isAuthProxyPreferencesSet();
        boolean isUseSystemProxy = ProxyPreferences.isSystemProxyPreferencesSet();

        boolean isUseNewProxyConfig = isUseAuthProxy || isUseSystemProxy;
        if (isUseNewProxyConfig) {
            printAuthProxyInfo();
            printSystemProxyInfo();
        } else {
            printLegacyProxyInfo();
        }
        LogUtil.logInfo("\n");
    }

    @SuppressWarnings("deprecation")
    public static void printLegacyProxyInfo() {
        String rawLegacyProxyInfo = getRawProxyInfo(ExecutionProxyUtil.getProxyInformation());
        LogUtil.logInfo("INFO: > Legacy Proxy: " + rawLegacyProxyInfo);
    }

    public static void printAuthProxyInfo() {
        String rawAuthProxyInfo = getRawProxyInfo(ExecutionProxyUtil.getAuthProxyInformation());
        LogUtil.logInfo("INFO: > Authentication Proxy: " + rawAuthProxyInfo);
    }

    public static void printSystemProxyInfo() {
        String rawSystemProxyInfo = getRawProxyInfo(ExecutionProxyUtil.getSystemProxyInformation());
        LogUtil.logInfo("INFO: > System Proxy: " + rawSystemProxyInfo);
    }

    public static String getRawProxyInfo(ProxyInformation proxyInfo) {
        return getRawProxyInfo(proxyInfo, false);
    }

    public static String getRawProxyInfo(ProxyInformation proxyInfo, boolean isSystemProxy) {
        String rawProxyInfo = "{ "
                + "proxyOption=" + proxyInfo.getProxyOption() + ", "
                + "proxyServerType=" + proxyInfo.getProxyServerType() + ", "
                + "proxyServerAddress=" + proxyInfo.getProxyServerAddress() + ", "
                + "proxyServerPort=" + proxyInfo.getProxyServerPort() + ", "
                + "username=" + proxyInfo.getUsername() + ", "
                + "password=" + "********" + ", "
                + "executionList=\"" + proxyInfo.getExceptionList() + "\""
                + (isSystemProxy ? (", isApplyToDesiredCapabilities=" + proxyInfo.isApplyToDesiredCapabilities()) : "")
                + " }";
        return rawProxyInfo;
    }
}
