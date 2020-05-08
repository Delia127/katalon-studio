package com.kms.katalon.application.utils.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.Proxy;

import org.junit.Test;

import com.kms.katalon.application.utils.ApplicationProxyUtil;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.network.ProxyOption;
import com.kms.katalon.core.network.ProxyServerType;

import net.bytebuddy.utility.RandomString;

public class ApplicationProxyUtilTest {

    @SuppressWarnings("deprecation")
    @Test
    public void getAuthProxyInformationReturnNewProxyTest() throws IOException {
        // Given
        ProxyInformation legacyProxyInfo = generateManualProxyInfo();
        ApplicationProxyUtil.saveProxyInformation(legacyProxyInfo);

        ProxyInformation newAuthProxyInfo = generateManualProxyInfo();
        ApplicationProxyUtil.saveAuthProxyInformation(newAuthProxyInfo);

        // When
        ProxyInformation authProxyInfo = ApplicationProxyUtil.getAuthProxyInformation();

        // Then
        assertEquals(newAuthProxyInfo.getProxyOption(), authProxyInfo.getProxyOption());
        assertEquals(newAuthProxyInfo.getProxyServerType(), authProxyInfo.getProxyServerType());
        assertEquals(newAuthProxyInfo.getProxyServerAddress(), authProxyInfo.getProxyServerAddress());
        assertEquals(newAuthProxyInfo.getProxyServerPort(), authProxyInfo.getProxyServerPort());
        assertEquals(newAuthProxyInfo.getExceptionList(), authProxyInfo.getExceptionList());
        assertEquals(newAuthProxyInfo.getUsername(), authProxyInfo.getUsername());
        assertEquals(newAuthProxyInfo.getPassword(), authProxyInfo.getPassword());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void getSystemProxyInformationReturnNewProxyTest() throws IOException {
        // Given
        ProxyInformation legacyProxyInfo = generateManualProxyInfo();
        ApplicationProxyUtil.saveProxyInformation(legacyProxyInfo);

        ProxyInformation newSystemProxyInfo = generateManualProxyInfo();
        ApplicationProxyUtil.saveSystemProxyInformation(newSystemProxyInfo);

        // When
        ProxyInformation systemProxyInfo = ApplicationProxyUtil.getSystemProxyInformation();

        // Then
        assertEquals(newSystemProxyInfo.getProxyOption(), systemProxyInfo.getProxyOption());
        assertEquals(newSystemProxyInfo.getProxyServerType(), systemProxyInfo.getProxyServerType());
        assertEquals(newSystemProxyInfo.getProxyServerAddress(), systemProxyInfo.getProxyServerAddress());
        assertEquals(newSystemProxyInfo.getProxyServerPort(), systemProxyInfo.getProxyServerPort());
        assertEquals(newSystemProxyInfo.getExceptionList(), systemProxyInfo.getExceptionList());
        assertEquals(newSystemProxyInfo.getUsername(), systemProxyInfo.getUsername());
        assertEquals(newSystemProxyInfo.getPassword(), systemProxyInfo.getPassword());
    }

    @Test
    public void getAuthProxyTest() throws IOException {
        // Given
        ProxyInformation authProxyInfo = generateManualProxyInfo();
        authProxyInfo.setProxyServerAddress("127.0.0.1");
        authProxyInfo.setProxyServerPort(1234);
        ApplicationProxyUtil.saveAuthProxyInformation(authProxyInfo);

        // When
        Proxy authProxy = ApplicationProxyUtil.getAuthProxy();

        // Then
        String expectedAddress = String.format("%s @ /%s:%s", authProxyInfo.getProxyServerType(),
                authProxyInfo.getProxyServerAddress(), authProxyInfo.getProxyServerPort());
        assertEquals(expectedAddress, authProxy.toString());
    }

    @Test
    public void getSystemProxyTest() throws IOException {
        // Given
        ProxyInformation systemProxyInfo = generateManualProxyInfo();
        systemProxyInfo.setProxyServerAddress("127.0.0.1");
        systemProxyInfo.setProxyServerPort(1234);
        ApplicationProxyUtil.saveSystemProxyInformation(systemProxyInfo);

        // When
        Proxy systemProxy = ApplicationProxyUtil.getSystemProxy();

        // Then
        String expectedAddress = String.format("%s @ /%s:%s", systemProxyInfo.getProxyServerType(),
                systemProxyInfo.getProxyServerAddress(), systemProxyInfo.getProxyServerPort());
        assertEquals(expectedAddress, systemProxy.toString());
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
