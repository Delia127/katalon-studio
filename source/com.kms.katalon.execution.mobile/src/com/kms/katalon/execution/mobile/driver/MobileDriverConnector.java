package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.appium.constants.AppiumStringConstants;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.execution.configuration.AbstractDriverConnector;
import com.kms.katalon.execution.mobile.constants.MobilePreferenceConstants;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public abstract class MobileDriverConnector extends AbstractDriverConnector {
    private static final String APPIUM_LOG_FILE_NAME = "appium.log";

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
        systemProperties.put(StringConstants.CONF_APPIUM_LOG_FILE, APPIUM_LOG_FILE_NAME);
        systemProperties.put(StringConstants.CONF_APPIUM_DIRECTORY, getAppiumDirectory());
        setDeviceSystemProperties(systemProperties);
        return systemProperties;
    }

    private Object getAppiumDirectory() {
        return PreferenceStoreManager.getPreferenceStore(MobilePreferenceConstants.MOBILE_QUALIFIER).getString(
                MobilePreferenceConstants.MOBILE_APPIUM_DIRECTORY);
    }

    private void setDeviceSystemProperties(Map<String, Object> systemProperties) {
        if (device == null) {
            return;
        }
        systemProperties.putAll(device.getDeviceSystemProperties());
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

    public String getDefaultDeviceId() {
        return StringUtils.defaultString(
                (String) getUserConfigProperties().get(AppiumStringConstants.CONF_EXECUTED_DEVICE_ID), StringUtils.EMPTY);
    }

    public void updateDefaultDeviceId() {
        getUserConfigProperties().put(AppiumStringConstants.CONF_EXECUTED_DEVICE_ID, getDeviceId());
    }

}
