package com.kms.katalon.execution.mobile.configuration;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.driver.MobileDriverConnector;

public abstract class MobileRunConfiguration extends AbstractRunConfiguration {
    protected MobileDriverConnector mobileDriverConnector;

    protected String projectDir;

    public MobileRunConfiguration(String projectDir, MobileDriverConnector mobileDriverConnector) throws IOException {
        super();
        this.mobileDriverConnector = mobileDriverConnector;
        this.projectDir = projectDir;
    }

    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        Map<String, IDriverConnector> driverCollector = new LinkedHashMap<String, IDriverConnector>();
        driverCollector.put(DriverFactory.MOBILE_DRIVER_PROPERTY, mobileDriverConnector);
        return driverCollector;
    }

    @Override
    public String getName() {
        return super.getName() + " - " + mobileDriverConnector.getDeviceId();
    }

    public void setDevice(MobileDeviceInfo device) {
        mobileDriverConnector.setDevice(device);
    }

    public MobileDeviceInfo getDevice() {
        return mobileDriverConnector.getDevice();
    }
}
