package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.keyword.MobileDriverFactory;
import com.kms.katalon.execution.configuration.AbstractDriverConnector;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;

public abstract class MobileDriverConnector extends AbstractDriverConnector {
    protected MobileDeviceInfo device;

    public MobileDriverConnector(String configurationFolderPath) throws IOException {
        this(configurationFolderPath, null);
    }

    public MobileDriverConnector(String configurationFolderPath, MobileDeviceInfo device) throws IOException {
        super(configurationFolderPath);
        setDevice(device);
    }
    
    public String getDeviceId() {
        if (device == null) {
            return "";
        }
        return device.getDeviceId();
    }

    public MobileDeviceInfo getDevice() {
        return device;
    }

    public void setDevice(MobileDeviceInfo device) {
        this.device = device;
    }

    @Override
    public Map<String, Object> getSystemProperties() {
        Map<String, Object> systemProperties = super.getSystemProperties();
        systemProperties.put(MobileDriverFactory.APPIUM_LOG_PROPERTY, "appium.log");
        setDeviceSystemProperties(systemProperties);
        return systemProperties;
    }

    private void setDeviceSystemProperties(Map<String, Object> systemProperties) {
        if (device == null) {
            return;
        }
        systemProperties.put(StringConstants.CONF_EXECUTED_DEVICE_ID, device.getDeviceId());
        systemProperties.put(StringConstants.CONF_EXECUTED_DEVICE_NAME, device.getDeviceName());
        systemProperties.put(StringConstants.CONF_EXECUTED_DEVICE_MANUFACTURER, device.getDeviceManufacturer());
        systemProperties.put(StringConstants.CONF_EXECUTED_DEVICE_MODEL, device.getDeviceModel());
        systemProperties.put(StringConstants.CONF_EXECUTED_DEVICE_OS, device.getDeviceOS());
        systemProperties.put(StringConstants.CONF_EXECUTED_DEVICE_OS_VERSON, device.getDeviceOSVersion());
    }

    @Override
    public String getSettingFileName() {
        return StringConstants.MOBILE_PROPERTY_FILE_NAME + "." + getDriverType().toString().toLowerCase();
    }

    @Override
    protected void loadDriverProperties() throws IOException {
        super.loadDriverProperties();
    }

    @Override
    public String toString() {
        Map<String, Object> tempMap = new LinkedHashMap<String, Object>(getUserConfigProperties());
        return tempMap.toString();
    }
}
