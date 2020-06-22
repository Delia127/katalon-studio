package com.kms.katalon.composer.windows.record.model;

import com.google.gson.annotations.SerializedName;

public class WindowsStartAppPayload {

    @SerializedName("AppTitle")
    private String appTitle;

    @SerializedName("AppPath")
    private String appPath;

    @SerializedName("Success")
    private boolean Success;

    @SerializedName("Message")
    private String Message;

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
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }
}
