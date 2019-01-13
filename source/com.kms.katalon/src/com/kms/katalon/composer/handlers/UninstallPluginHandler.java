package com.kms.katalon.composer.handlers;

import java.io.File;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.katalon.platform.api.PluginInstaller;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;

@SuppressWarnings("restriction")
public class UninstallPluginHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private PluginInstaller pluginInstaller;

    @CanExecute
    public boolean canExecute() {
        return StringUtils.isNoneEmpty(InstallPluginHandler.getPluginPath());
    }

    @Execute
    public void installPlugin() {

        Shell activeShell = Display.getCurrent().getActiveShell();
        try {
            Bundle bundle = pluginInstaller.uninstallPlugin(InternalPlatform.getDefault().getBundleContext(),
                    new File(InstallPluginHandler.getPluginPath()).toURI().toString());
            if (bundle != null && IdConstants.JIRA_PLUGIN_ID.equals(bundle.getSymbolicName())) {
                eventBroker.post(EventConstants.JIRA_PLUGIN_UNINSTALLED, null);
            }
            MessageDialog.openInformation(activeShell, GlobalStringConstants.INFO, "Plugin uninstalled sucessfully");
            InstallPluginHandler.resetPluginPath();
        } catch (BundleException e) {
            MessageDialog.openError(activeShell, GlobalStringConstants.ERROR, "Unable to uninstall plugin");
            LoggerSingleton.logError(e);
        }
    }
}
