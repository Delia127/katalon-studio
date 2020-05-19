package com.kms.katalon.composer.windows.record.model;

import com.google.gson.annotations.SerializedName;

public class WindowsAppClosedPayload {

    @SerializedName("AppTitle")
    private String appTitle;

    @SerializedName("AppPath")
    private String appPath;

    @SerializedName("Message")
    private String message;

    @SerializedName("Unexpectedly")
    private boolean unexpectedly;

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isUnexpectedly() {
        return unexpectedly;
    }

    public void setUnexpectedly(boolean unexpectedly) {
        this.unexpectedly = unexpectedly;
    }
}
