package com.kms.katalon.composer.handlers;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleException;


@SuppressWarnings("restriction")
public class InstallPluginHandler {

    @Inject
    IEventBroker eventBroker;
    
    @Execute
    public void loadPlugin() throws BundleException {
        eventBroker.send("KATALON_PLUGIN/INSTALL", new Object[] {
                InternalPlatform.getDefault().getBundleContext(),
                new File("/Users/duyanhluong/Documents/Work/code/katalon-slack-plugin/target/testplugin-1.0-SNAPSHOT.jar").toURI().toString()
        });
    }
}
