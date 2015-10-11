package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.execution.configuration.AbstractDriverConnector;

public abstract class MobileDriverConnector extends AbstractDriverConnector {
    protected String deviceName;

    public MobileDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }
    
    @Override
    public Map<String, Object> getExecutionSettingPropertyMap() {
        Map<String, Object> propertyMap = super.getExecutionSettingPropertyMap();
        propertyMap.put(StringConstants.CONF_EXECUTED_DEVICE_NAME, deviceName);
        return propertyMap;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String getSettingFileName() {
        return StringConstants.MOBILE_PROPERTY_FILE_NAME + "." + getDriverType().toString().toLowerCase();
    }

    @Override
    protected void loadDriverProperties() throws IOException {
        super.loadDriverProperties();
        deviceName = (driverProperties.get(StringConstants.CONF_EXECUTED_DEVICE_NAME) instanceof String) ? (String) driverProperties
                .get(StringConstants.CONF_EXECUTED_DEVICE_NAME) : null;
        driverProperties.remove(StringConstants.CONF_EXECUTED_DEVICE_NAME);
    }

    @Override
    public void saveDriverProperties() throws IOException {
        if (deviceName != null) {
            driverProperties.put(StringConstants.CONF_EXECUTED_DEVICE_NAME, deviceName);
        }
        super.saveDriverProperties();
    }
    
    @Override
    public String toString() {
        Map<String, Object> tempMap = new HashMap<String, Object>(getDriverProperties());
        tempMap.put(StringConstants.CONF_EXECUTED_DEVICE_NAME, deviceName);
        return tempMap.toString();
    }
}
