package com.kms.katalon.integration.kobiton.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.integration.kobiton.constants.IntegrationKobitonMessages;
import com.kms.katalon.integration.kobiton.constants.KobitonStringConstants;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;

public class KobitonDriverConnector extends RemoteWebDriverConnector {
    private KobitonDevice kobitonDevice;

    private String apiKey;

    private String userName;

    public KobitonDriverConnector(String projectDir) throws IOException {
        super(projectDir);
        remoteWebDriverConnectorType = RemoteWebDriverConnectorType.Appium;
    }

    @Override
    public String getRemoteServerUrl() {
        if (apiKey == null || userName == null || kobitonDevice == null) {
            throw new IllegalArgumentException(IntegrationKobitonMessages.MSG_ERR_MISSING_EXECUTION_INFO);
        }
        return buildKobitonUrl();
    }

    private String buildKobitonUrl() {
        return KobitonStringConstants.KOBITON_SCHEME_HTTP + KobitonStringConstants.KOBITON_SCHEME_SEPARATOR + userName
                + KobitonStringConstants.KOBITON_SERVER_URL_AUTHENTICATE_SEPARATOR + apiKey
                + KobitonStringConstants.KOBITON_SERVER_URL_SEPARATOR + KobitonStringConstants.KOBITON_HOST
                + KobitonStringConstants.KOBITON_SERVER_REMOTE_HUB;
    }

    @Override
    public DriverType getDriverType() {
        return WebUIDriverType.KOBITON_WEB_DRIVER;
    }

    public KobitonDevice getKobitonDevice() {
        return kobitonDevice;
    }

    public void setKobitonDevice(KobitonDevice kobitonDevice) {
        this.kobitonDevice = kobitonDevice;
        setMobileDriverType(getMobileDriverType(kobitonDevice));
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public Map<String, Object> getUserConfigProperties() {
        Map<String, Object> configProperties = super.getUserConfigProperties();
        configProperties.putAll(kobitonDevice.toDesireCapabilitiesMap());
        return configProperties;
    }
    
    @Override
    public Map<String, Object> getSystemProperties() {
        Map<String, Object> systemProperties = super.getSystemProperties();
        systemProperties.putAll(kobitonDevice.getSystemPropertiesMap());
        return systemProperties;
    }

    public static MobileDriverType getMobileDriverType(KobitonDevice kobitonDevice) {
        if (kobitonDevice == null || kobitonDevice.getCapabilities() == null) {
            return null;
        }
        if (KobitonDevice.PLATFORM_NAME_IOS.equals(kobitonDevice.getCapabilities().getPlatformName())) {
            return MobileDriverType.IOS_DRIVER;
        }
        if (KobitonDevice.PLATFORM_NAME_ANDROID.equals(kobitonDevice.getCapabilities().getPlatformName())) {
            return MobileDriverType.ANDROID_DRIVER;
        }
        return null;
    }
}
