package com.kms.katalon.execution.util;

import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class ExecutionProfileStore {

    private ExecutionProfileEntity selectedProfile;
    
    private static ExecutionProfileStore instance;
    
    private ExecutionProfileStore() {}
    
    public ExecutionProfileEntity getSelectedProfile() {
        return selectedProfile;
    }
    
    public void setSelectedProfile(ExecutionProfileEntity selectedProfile) {
        this.selectedProfile = selectedProfile;
    }
    
    public static ExecutionProfileStore getInstance() {
        if (instance == null) {
            instance = new ExecutionProfileStore();
        }
        return instance;
    }
}
