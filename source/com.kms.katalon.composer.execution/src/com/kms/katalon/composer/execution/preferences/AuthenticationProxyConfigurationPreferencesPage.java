package com.kms.katalon.composer.execution.preferences;

import java.io.IOException;

import com.kms.katalon.application.utils.ApplicationProxyUtil;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.execution.preferences.ProxyPreferences;

public class AuthenticationProxyConfigurationPreferencesPage extends AbstractProxyConfigurationPreferencesPage {

    @Override
    protected String getGuideMessage() {
        return MessageConstants.LBL_AUTHENTICATION_PROXY_GUIDE_MESSAGE;
    }

    @Override
    protected ProxyInformation getProxyInfo() {
        return ProxyPreferences.isAuthProxyPreferencesSet()
                ? ProxyPreferences.getAuthProxyInformation()
                : ApplicationProxyUtil.getAuthProxyInformation();
    }

    @Override
    protected void saveProxyInfo(ProxyInformation proxyInfo) throws IOException {
        ProxyPreferences.saveAuthProxyInformation(proxyInfo);
    }
}
