package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.execution.entity.AbstractDriverConnector;

public abstract class AbstractMobileDriverConnector extends AbstractDriverConnector {
    protected String deviceName;

    public AbstractMobileDriverConnector(String projectDir, String deviceName) throws IOException {
        super(projectDir);
        this.deviceName = deviceName;
    }

    public AbstractMobileDriverConnector(String projectDir, String customProfileName, String deviceName)
            throws IOException {
        super(projectDir, customProfileName); 
        this.deviceName = deviceName;
    }

    @Override
    public Map<String, Object> getExecutionSettingPropertyMap() {
        Map<String, Object> propertyMap = super.getExecutionSettingPropertyMap();
        propertyMap.put(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_DEVICE_NAME, deviceName);
        return propertyMap;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    @Override
    protected String getSettingFileName() {
        return StringConstants.MOBILE_PROPERTY_FILE_NAME;
    }

}
