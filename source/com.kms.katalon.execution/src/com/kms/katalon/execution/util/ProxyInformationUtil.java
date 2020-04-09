package com.kms.katalon.execution.util;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.network.ProxyOption;
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
        LogUtil.logInfo("INFO: > Legacy Proxy:");
        printProxyInfo(ExecutionProxyUtil.getProxyInformation());
    }

    public static void printAuthProxyInfo() {
        LogUtil.logInfo("INFO: > Authentication Proxy:");
        printProxyInfo(ExecutionProxyUtil.getAuthProxyInformation());
    }

    public static void printSystemProxyInfo() {
        LogUtil.logInfo("INFO: > System Proxy:");
        printProxyInfo(ExecutionProxyUtil.getSystemProxyInformation());
    }

    public static void printProxyInfo(ProxyInformation proxyInfo) {
        LogUtil.logInfo("INFO: Proxy option: " + proxyInfo.getProxyOption());
        if (ProxyOption.MANUAL_CONFIG.name().equalsIgnoreCase(proxyInfo.getProxyOption())) {
            LogUtil.logInfo("INFO: Proxy server type: " + proxyInfo.getProxyServerType());
            LogUtil.logInfo("INFO: Proxy server address: " + proxyInfo.getProxyServerAddress());
            LogUtil.logInfo("INFO: Proxy server port: " + proxyInfo.getProxyServerPort());
            if (StringUtils.isNotBlank(proxyInfo.getExceptionList())) {
                LogUtil.logInfo("INFO: Proxy exception list: " + proxyInfo.getExceptionList());
            }
            if (StringUtils.isNotBlank(proxyInfo.getUsername())) {
                LogUtil.logInfo("INFO: Proxy username: " + proxyInfo.getUsername());
                LogUtil.logInfo("INFO: Proxy password: " + "********");
            }
        }
    }
}
