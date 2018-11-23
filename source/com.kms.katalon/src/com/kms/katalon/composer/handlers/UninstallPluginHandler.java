package com.kms.katalon.composer.handlers;

import java.io.File;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

@SuppressWarnings("restriction")
public class UninstallPluginHandler {

    @Inject
    private IEventBroker eventBroker;

    @CanExecute
    public boolean canExecute() {
        return StringUtils.isNoneEmpty(InstallPluginHandler.getPluginPath());
    }

    @Execute
    public void installPlugin() {
        eventBroker.post("KATALON_PLUGIN/UNINSTALL", 
                new Object[] { 
                        InternalPlatform.getDefault().getBundleContext(),
                        new File(InstallPluginHandler.getPluginPath()).toURI().toString()
                });
        InstallPluginHandler.resetPluginPath();
    }
}
