package com.kms.katalon.composer.windows.socket;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class WindowsStartRecordingPayload {
    @SerializedName("AppPath")
    private String appPath;

    @SerializedName("DesiredCapabilities")
    private Map<String, Object> desiredCapabilities;

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public Map<String, Object> getDesiredCapabilities() {
        return desiredCapabilities;
    }

    public void setDesiredCapabilities(Map<String, Object> desiredCapabilities) {
        this.desiredCapabilities = desiredCapabilities;
    }
}
