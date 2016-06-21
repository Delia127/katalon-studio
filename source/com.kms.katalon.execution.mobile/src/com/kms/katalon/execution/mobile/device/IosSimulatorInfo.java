package com.kms.katalon.execution.mobile.device;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.appium.constants.AppiumStringConstants;

public class IosSimulatorInfo extends IosDeviceInfo {
    private static final String SIMULATOR_SUFFIX = " (Simulator)";

    private static final String NOT_AVAILABLE_FOR_SIMULATOR = "Not available for Simulator";

    public IosSimulatorInfo(String deviceId, String deviceName, String deviceOSVersion)
            throws IOException, InterruptedException {
        super(deviceId);
        this.deviceName = deviceName;
        this.deviceOSVersion = deviceOSVersion; 
    }

    @Override
    protected void initDeviceInfos(String deviceId) throws IOException, InterruptedException {
        // Leave blank because don't need to collect info for simulator
    }

    @Override
    public String getDisplayName() {
        return deviceName + SIMULATOR_SUFFIX;
    }

    @Override
    public String getDeviceModel() {
        return NOT_AVAILABLE_FOR_SIMULATOR;
    }

    @Override
    public String getDeviceOSVersion() {
        return deviceOSVersion;
    }
    
    public Map<String, String> getDeviceSystemProperties() {
        Map<String, String> systemProperties = new HashMap<String, String>();
        systemProperties.put(AppiumStringConstants.CONF_EXECUTED_DEVICE_NAME, getDeviceName());
        systemProperties.put(AppiumStringConstants.CONF_EXECUTED_DEVICE_MANUFACTURER, getDeviceManufacturer());
        systemProperties.put(AppiumStringConstants.CONF_EXECUTED_DEVICE_MODEL, getDeviceModel());
        systemProperties.put(AppiumStringConstants.CONF_EXECUTED_DEVICE_OS, getDeviceOS());
        systemProperties.put(AppiumStringConstants.CONF_EXECUTED_DEVICE_OS_VERSON, getDeviceOSVersion());
        return systemProperties;
    }
}
