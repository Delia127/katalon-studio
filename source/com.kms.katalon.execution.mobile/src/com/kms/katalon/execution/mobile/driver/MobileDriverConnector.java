package com.kms.katalon.execution.mobile.driver;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.keyword.MobileDriverFactory;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.AbstractDriverConnector;

public abstract class MobileDriverConnector extends AbstractDriverConnector {
    protected MobileDevice device;
    private String configurationFolder;
    
    public MobileDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
        configurationFolder = configurationFolderPath;
    }

    public MobileDevice getDevice() {
        return device;
    }

    public void setDevice(MobileDevice device) {
        this.device = device;
        if (device != null) {
            driverProperties.put(StringConstants.CONF_EXECUTED_DEVICE_ID, device.getId());
        }
    }
    
    @Override
    public Map<String, Object> getSystemProperties() {
       Map<String, Object> systemProperties = super.getSystemProperties();
       String projectDir = configurationFolder.replace(File.separator
               + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME, "");
       systemProperties.put(MobileDriverFactory.APPIUM_LOG_PROPERTY, projectDir + File.separator + "appium.log");
       return systemProperties;
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

    public String getDeviceId() {
        return (String) driverProperties.get(StringConstants.CONF_EXECUTED_DEVICE_ID);
    }

    public void setDeviceId(String deviceId) {
        driverProperties.put(StringConstants.CONF_EXECUTED_DEVICE_ID, deviceId);
    }
}
