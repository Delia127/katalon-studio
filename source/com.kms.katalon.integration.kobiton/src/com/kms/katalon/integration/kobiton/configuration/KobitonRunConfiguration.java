package com.kms.katalon.integration.kobiton.configuration;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.configuration.RemoteWebRunConfiguration;
import com.kms.katalon.integration.kobiton.driver.KobitonDriverConnector;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;

public class KobitonRunConfiguration extends RemoteWebRunConfiguration {
    
    public static final String KOBITON_DEVICE_PROPERTY = "kobitonDevice";

    KobitonDriverConnector kobitonDriverConnetor;

    public KobitonRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new KobitonDriverConnector(projectDir));
        kobitonDriverConnetor = (KobitonDriverConnector) remoteDriverConnector;
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
        kobitonDriverConnetor.setApiKey(apiKey);
    }

    public String getUserName() {
        return kobitonDriverConnetor.getUserName();
    }

    public void setUserName(String userName) {
        kobitonDriverConnetor.setUserName(userName);
    }

    @Override
    public String getName() {
        return kobitonDriverConnetor.getDriverType().toString() + " - "
                + kobitonDriverConnetor.getKobitonDevice().getDisplayString();
    }

    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        Map<String, IDriverConnector> driverCollector = new LinkedHashMap<String, IDriverConnector>();
        driverCollector.put(RunConfiguration.REMOTE_DRIVER_PROPERTY, kobitonDriverConnetor);
        return driverCollector;
    }
}
