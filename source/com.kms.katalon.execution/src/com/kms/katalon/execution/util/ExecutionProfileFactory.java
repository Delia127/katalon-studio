package com.kms.katalon.execution.util;

import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class ExecutionProfileFactory {

    private ExecutionProfileEntity selectedProfile;
    
    private static ExecutionProfileFactory instance;
    
    private ExecutionProfileFactory() {}
    
    public ExecutionProfileEntity getSelectedProfile() {
        return selectedProfile;
    }
    
    public void setSelectedProfile(ExecutionProfileEntity selectedProfile) {
        this.selectedProfile = selectedProfile;
    }
    
    public static ExecutionProfileFactory getInstance() {
        if (instance == null) {
            instance = new ExecutionProfileFactory();
        }
        return instance;
    }
}
