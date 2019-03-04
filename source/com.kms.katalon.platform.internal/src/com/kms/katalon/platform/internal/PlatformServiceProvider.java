package com.kms.katalon.platform.internal;

import com.katalon.platform.api.service.ControllerManager;
import com.katalon.platform.api.service.UIServiceManager;
import com.kms.katalon.platform.internal.controller.ControllerManagerImpl;
import com.kms.katalon.platform.internal.ui.UIServiceManagerImpl;

public class PlatformServiceProvider {
    
    private static PlatformServiceProvider instance;
    
    public static PlatformServiceProvider getInstance() {
        if (instance == null) {
            instance = new PlatformServiceProvider();
        }
        return instance;
    }
    
    private final ControllerManager controllerManager = new ControllerManagerImpl();
    
    private final UIServiceManager uiServiceManager = new UIServiceManagerImpl();
    
    private PlatformServiceProvider() {
        //Diable default constructor
    }
    
    public ControllerManager getControllerManager() {
        return controllerManager;
    }

    public UIServiceManager getUiServiceManager() {
        return uiServiceManager;
    }
}
