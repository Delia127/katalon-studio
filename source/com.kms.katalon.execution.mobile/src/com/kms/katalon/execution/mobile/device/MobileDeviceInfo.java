package com.kms.katalon.execution.mobile.device;

public abstract class MobileDeviceInfo {
    protected String deviceId;
    
    public MobileDeviceInfo(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public abstract String getDeviceName();

    public abstract String getDeviceManufacturer();

    public abstract String getDeviceModel();
    
    public abstract String getDeviceOS();
    
    public abstract String getDeviceOSVersion();
}
