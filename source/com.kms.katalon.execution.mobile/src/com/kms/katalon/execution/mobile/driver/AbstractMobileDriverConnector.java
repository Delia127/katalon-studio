package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.execution.entity.AbstractDriverConnector;

public abstract class AbstractMobileDriverConnector extends AbstractDriverConnector {
    protected String deviceName;

    public AbstractMobileDriverConnector(String projectDir) throws IOException {
        super(projectDir);
    }

    public AbstractMobileDriverConnector(String projectDir, String customProfileName) throws IOException {
        super(projectDir, customProfileName);
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
    protected String getSettingFileName() {
        return StringConstants.MOBILE_PROPERTY_FILE_NAME;
    }

    @Override
    protected void loadDriverProperties() {
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
}
