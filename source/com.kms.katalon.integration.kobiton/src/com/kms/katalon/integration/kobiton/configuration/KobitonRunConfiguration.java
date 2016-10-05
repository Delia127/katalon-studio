package com.kms.katalon.integration.kobiton.configuration;

import java.io.IOException;

import com.kms.katalon.execution.webui.configuration.RemoteWebRunConfiguration;
import com.kms.katalon.integration.kobiton.driver.KobitonDriverConnector;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;

public class KobitonRunConfiguration extends RemoteWebRunConfiguration {
    KobitonDriverConnector kobitonDriverConnetor;

    public KobitonRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new KobitonDriverConnector(projectDir));
        kobitonDriverConnetor = (KobitonDriverConnector) webUiDriverConnector;
    }

    public KobitonDevice getKobitonDevice() {
        return kobitonDriverConnetor.getKobitonDevice();
    }

    public void setKobitonDevice(KobitonDevice kobitonDevice) {
        kobitonDriverConnetor.setKobitonDevice(kobitonDevice);
    }

    public String getApiKey() {
        return kobitonDriverConnetor.getApiKey();
    }

    public void setApiKey(String apiKey) {
        kobitonDriverConnetor.setToken(apiKey);
    }

    public String getUserName() {
        return kobitonDriverConnetor.getUserName();
    }

    public void setUserName(String userName) {
        kobitonDriverConnetor.setUserName(userName);
    }

    @Override
    public String getName() {
        return kobitonDriverConnetor.getDriverType().toString() + " - " + kobitonDriverConnetor.getKobitonDevice().getDisplayString();
    }
}
