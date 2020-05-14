package com.kms.katalon.execution.util.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.network.ProxyOption;
import com.kms.katalon.core.network.ProxyServerType;
import com.kms.katalon.core.util.internal.ProxyUtil;
import com.kms.katalon.execution.constants.ProxyPreferenceConstants;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.execution.util.ExecutionProxyUtil;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.bytebuddy.utility.RandomString;

public class ExecutionProxyUtilTest {

    private OptionParser parser = new OptionParser(false);

    @Spy
    private OptionSet options = parser.parse();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkMixedProxiesOnlyLegacyProxyTest() {
        // Given
        Mockito.doReturn(true).when(options).has(ProxyPreferenceConstants.PROXY_OPTION);

        // When
        boolean isMixedProxies = ExecutionProxyUtil.checkMixedProxies(options);

        // Then
        assertFalse("ExecutionProxyUtil.checkMixedProxies(...) must return false", isMixedProxies);
    }

    @Test
    public void checkMixedProxiesOnlyAuthProxyTest() {
        // Given
        Mockito.when(options.has(ProxyPreferenceConstants.AUTH_PROXY_OPTION)).thenReturn(true);

        // When
        boolean isMixedProxies = ExecutionProxyUtil.checkMixedProxies(options);

        // Then
        assertFalse("ExecutionProxyUtil.checkMixedProxies(...) must return false", isMixedProxies);
    }

    @Test
    public void checkMixedProxiesOnlySystemProxyTest() {
        // Given
        Mockito.when(options.has(ProxyPreferenceConstants.SYSTEM_PROXY_OPTION)).thenReturn(true);

        // When
        boolean isMixedProxies = ExecutionProxyUtil.checkMixedProxies(options);

        // Then
        assertFalse("ExecutionProxyUtil.checkMixedProxies(...) must return false", isMixedProxies);
    }

    @Test
    public void checkMixedProxiesOnlyNewProxyTest() {
        // Given
        Mockito.when(options.has(ProxyPreferenceConstants.AUTH_PROXY_OPTION)).thenReturn(true);
        Mockito.when(options.has(ProxyPreferenceConstants.SYSTEM_PROXY_OPTION)).thenReturn(true);

        // When
        boolean isMixedProxies = ExecutionProxyUtil.checkMixedProxies(options);

        // Then
        assertFalse("ExecutionProxyUtil.checkMixedProxies(...) must return false", isMixedProxies);
    }

    @Test
    public void checkMixedProxiesLegacyProxyAndAuthProxyTest() {
        // Given
        Mockito.when(options.has(ProxyPreferenceConstants.PROXY_OPTION)).thenReturn(true);
        Mockito.when(options.has(ProxyPreferenceConstants.AUTH_PROXY_OPTION)).thenReturn(true);

        // When
        boolean isMixedProxies = ExecutionProxyUtil.checkMixedProxies(options);

        // Then
        assertTrue("ExecutionProxyUtil.checkMixedProxies(...) must return true", isMixedProxies);
    }

    @Test
    public void checkMixedProxiesLegacyProxyAndSystemProxyTest() {
        // Given
        Mockito.when(options.has(ProxyPreferenceConstants.PROXY_OPTION)).thenReturn(true);
        Mockito.when(options.has(ProxyPreferenceConstants.AUTH_PROXY_OPTION)).thenReturn(true);

        // When
        boolean isMixedProxies = ExecutionProxyUtil.checkMixedProxies(options);

        // Then
        assertTrue("ExecutionProxyUtil.checkMixedProxies(...) must return true", isMixedProxies);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void isConfigProxyLegacyProxyTest() throws IOException {
        // Given
        ProxyInformation proxyInfo = generateManualProxyInfo();
        ProxyPreferences.saveProxyInformation(proxyInfo);

        // When
        boolean isConfigProxy = ExecutionProxyUtil.isConfigProxy();

        // Then
        assertTrue("Proxy must be configured", isConfigProxy);
    }

    @Test
    public void isConfigProxyAuthProxyTest() throws IOException {
        // Given
        ProxyInformation proxyInfo = generateManualProxyInfo();
        ProxyPreferences.saveAuthProxyInformation(proxyInfo);

        // When
        boolean isConfigProxy = ExecutionProxyUtil.isConfigProxy();

        // Then
        assertTrue("Proxy must be configured", isConfigProxy);
    }

    @Test
    public void isConfigProxySystemProxyTest() throws IOException {
        // Given
        ProxyInformation proxyInfo = generateManualProxyInfo();
        ProxyPreferences.saveSystemProxyInformation(proxyInfo);

        // When
        boolean isConfigProxy = ExecutionProxyUtil.isConfigProxy();

        // Then
        assertTrue("Proxy must be configured", isConfigProxy);
    }

    private ProxyInformation generateManualProxyInfo() {
        ProxyInformation proxyInfo = new ProxyInformation();
        proxyInfo.setProxyOption(ProxyOption.MANUAL_CONFIG.name());
        proxyInfo.setProxyServerAddress(RandomString.make(10));
        proxyInfo.setProxyServerPort(RandomString.make(10));
        proxyInfo.setProxyServerType(ProxyServerType.HTTP.name());
        proxyInfo.setExceptionList(RandomString.make(10));
        proxyInfo.setUsername(RandomString.make(10));
        proxyInfo.setPassword(RandomString.make(10));
        return proxyInfo;
    }
}
