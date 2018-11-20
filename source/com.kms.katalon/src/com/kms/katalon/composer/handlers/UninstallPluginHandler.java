package com.kms.katalon.composer.handlers;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import com.kms.katalon.platform.KatalonPlatformActivator;

public class UninstallPluginHandler {
    @Inject
    IEclipseContext context;

    @Execute
    public void installPlugin() {
        try {
            KatalonPlatformActivator.disablePlugin(context, InternalPlatform.getDefault().getBundleContext(),
                    new File("/Users/duyanhluong/Documents/Work/code/katalon-slack-plugin/target/classes").toURI()
                            .toString());
        } catch (BundleException e) {
            MessageDialog.openError(null, "Error", e.getMessage());
        }
    }
}
