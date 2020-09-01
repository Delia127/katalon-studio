package com.kms.katalon.composer.platform.internal;

import com.katalon.platform.api.service.UIServiceManager;
import com.kms.katalon.composer.platform.internal.ui.UIServiceManagerImpl;

public class PlatformUIServiceProvider {

    private static PlatformUIServiceProvider instance;

    public static PlatformUIServiceProvider getInstance() {
        if (instance == null) {
            instance = new PlatformUIServiceProvider();
        }
        return instance;
    }

    private final UIServiceManager uiServiceManager = new UIServiceManagerImpl();

    private PlatformUIServiceProvider() {
        // Diable default constructor
    }

    public UIServiceManager getUiServiceManager() {
        return uiServiceManager;
    }
}
