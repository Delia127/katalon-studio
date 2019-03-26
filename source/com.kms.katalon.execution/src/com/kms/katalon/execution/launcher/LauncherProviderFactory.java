package com.kms.katalon.execution.launcher;

import com.kms.katalon.execution.launcher.provider.IDELauncherProvider;

public class LauncherProviderFactory {

    private static LauncherProviderFactory instance;
    
    private IDELauncherProvider ideLauncherProvider;
    
    private LauncherProviderFactory() {
    }
    
    public static LauncherProviderFactory getInstance() {
        if (instance == null) {
            instance = new LauncherProviderFactory();
        }
        return instance;
    }

    public IDELauncherProvider getIdeLauncherProvider() {
        return ideLauncherProvider;
    }

    public void setIdeLauncherProvider(IDELauncherProvider ideLauncherProvider) {
        this.ideLauncherProvider = ideLauncherProvider;
    }
}
