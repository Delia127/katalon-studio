package com.kms.katalon.execution.preferences.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.network.ProxyOption;
import com.kms.katalon.core.network.ProxyServerType;
import com.kms.katalon.execution.preferences.ProxyPreferences;

import net.bytebuddy.utility.RandomString;

public class ProxyPreferencesTest {

    @SuppressWarnings("deprecation")
    @Test
    public void getAuthProxyInformationReturnLegacyProxyTest() throws IOException {
        // Given
        ProxyInformation legacyProxyInfo = generateManualProxyInfo();
        ProxyPreferences.saveProxyInformation(legacyProxyInfo);
        
        //When
        ProxyInformation authProxyInfo = ProxyPreferences.getAuthProxyInformation();
        
        // Then
        assertEquals(legacyProxyInfo.getProxyOption(), authProxyInfo.getProxyOption());
        assertEquals(legacyProxyInfo.getProxyServerType(), authProxyInfo.getProxyServerType());
        assertEquals(legacyProxyInfo.getProxyServerAddress(), authProxyInfo.getProxyServerAddress());
        assertEquals(legacyProxyInfo.getProxyServerPort(), authProxyInfo.getProxyServerPort());
        assertEquals(legacyProxyInfo.getExceptionList(), authProxyInfo.getExceptionList());
        assertEquals(legacyProxyInfo.getUsername(), authProxyInfo.getUsername());
        assertEquals(legacyProxyInfo.getPassword(), authProxyInfo.getPassword());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void getSystemProxyInformationReturnLegacyProxyTest() throws IOException {
        // Given
        ProxyInformation legacyProxyInfo = generateManualProxyInfo();
        ProxyPreferences.saveProxyInformation(legacyProxyInfo);
        
        //When
        ProxyInformation systemProxyInfo = ProxyPreferences.getSystemProxyInformation();
        
        // Then
        assertEquals(legacyProxyInfo.getProxyOption(), systemProxyInfo.getProxyOption());
        assertEquals(legacyProxyInfo.getProxyServerType(), systemProxyInfo.getProxyServerType());
        assertEquals(legacyProxyInfo.getProxyServerAddress(), systemProxyInfo.getProxyServerAddress());
        assertEquals(legacyProxyInfo.getProxyServerPort(), systemProxyInfo.getProxyServerPort());
        assertEquals(legacyProxyInfo.getExceptionList(), systemProxyInfo.getExceptionList());
        assertEquals(legacyProxyInfo.getUsername(), systemProxyInfo.getUsername());
        assertEquals(legacyProxyInfo.getPassword(), systemProxyInfo.getPassword());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void getAuthProxyInformationReturnNewProxyTest() throws IOException {
        // Given
        ProxyInformation legacyProxyInfo = generateManualProxyInfo();
        ProxyPreferences.saveProxyInformation(legacyProxyInfo);

        ProxyInformation newAuthProxyInfo = generateManualProxyInfo();
        ProxyPreferences.saveAuthProxyInformation(newAuthProxyInfo);
        
        //When
        ProxyInformation authProxyInfo = ProxyPreferences.getSystemProxyInformation();
        
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
        ProxyPreferences.saveProxyInformation(legacyProxyInfo);

        ProxyInformation newSystemProxyInfo = generateManualProxyInfo();
        ProxyPreferences.saveSystemProxyInformation(newSystemProxyInfo);
        
        //When
        ProxyInformation systemProxyInfo = ProxyPreferences.getSystemProxyInformation();
        
        // Then
        assertEquals(newSystemProxyInfo.getProxyOption(), systemProxyInfo.getProxyOption());
        assertEquals(newSystemProxyInfo.getProxyServerType(), systemProxyInfo.getProxyServerType());
        assertEquals(newSystemProxyInfo.getProxyServerAddress(), systemProxyInfo.getProxyServerAddress());
        assertEquals(newSystemProxyInfo.getProxyServerPort(), systemProxyInfo.getProxyServerPort());
        assertEquals(newSystemProxyInfo.getExceptionList(), systemProxyInfo.getExceptionList());
        assertEquals(newSystemProxyInfo.getUsername(), systemProxyInfo.getUsername());
        assertEquals(newSystemProxyInfo.getPassword(), systemProxyInfo.getPassword());
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
