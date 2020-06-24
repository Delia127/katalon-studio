package com.kms.katalon.platform.internal;

import com.katalon.platform.api.service.ControllerManager;
import com.kms.katalon.platform.internal.controller.ControllerManagerImpl;

public class PlatformServiceProvider {
    
    private static PlatformServiceProvider instance;
    
    public static PlatformServiceProvider getInstance() {
        if (instance == null) {
            instance = new PlatformServiceProvider();
        }
        return instance;
    }
    
    private final ControllerManager controllerManager = new ControllerManagerImpl();
    
    private PlatformServiceProvider() {
        //Diable default constructor
    }
    
    public ControllerManager getControllerManager() {
        return controllerManager;
    }
}
