package com.kms.katalon.execution.launcher.listener;

public class LauncherNotifiedObject {
    private String launcherId;

    private Object object;
    
    public LauncherNotifiedObject(String launcherId, Object notifiedObject) {
        setLauncherId(launcherId);
        setNotifiedObject(notifiedObject);
    }

    public String getLauncherId() {
        return launcherId;
    }

    private void setLauncherId(String launcherId) {
        this.launcherId = launcherId;
    }

    public Object getObject() {
        return object;
    }

   private void setNotifiedObject(Object notifiedObject) {
        this.object = notifiedObject;
    }
}
