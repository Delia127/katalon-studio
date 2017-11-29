package com.kms.katalon.execution.mobile.device;

public class AndroidSDKDownloadMessage {

    public static final String EVENT_NAME = "ANDROID_SDK_DOWNLOAD_EVENT";

    private final String message;

    private final int workingProgress;

    public String getMessage() {
        return message;
    }

    public int getWorkingProgess() {
        return workingProgress;
    }
    
    private AndroidSDKDownloadMessage(String message, int workingProgress) {
        this.message = message;
        this.workingProgress = workingProgress;
    }
    
    public static AndroidSDKDownloadMessage create(String message, int workingProgress) {
        return new AndroidSDKDownloadMessage(message, workingProgress);
    }
}
