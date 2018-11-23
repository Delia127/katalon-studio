package com.kms.katalon.composer.handlers;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

@SuppressWarnings("restriction")
public class UninstallPluginHandler {

    @Inject
    IEventBroker eventBroker;

    @Execute
    public void installPlugin() {
        eventBroker.post("KATALON_PLUGIN/UNINSTALL",
                new Object[] { InternalPlatform.getDefault().getBundleContext(),
                        new File("/Users/duyanhluong/Documents/Work/code/katalon-slack-plugin/target/testplugin-1.0-SNAPSHOT.jar").toURI()
                                .toString() });
    }
}
