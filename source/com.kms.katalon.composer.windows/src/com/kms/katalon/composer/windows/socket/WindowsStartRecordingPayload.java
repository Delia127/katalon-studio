package com.kms.katalon.composer.windows.socket;

import com.google.gson.annotations.SerializedName;

public class WindowsStartRecordingPayload {
    @SerializedName("AppPath")
    private String appPath;

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }
}
