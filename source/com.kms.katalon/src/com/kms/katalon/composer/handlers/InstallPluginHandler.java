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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.katalon.platform.internal.api.PluginInstaller;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;

@SuppressWarnings("restriction")
public class InstallPluginHandler {

    private static final String[] FILTER_NAMES = { "Jar file (*.jar)" };

    private static final String[] FILTER_EXTS = { "*.jar" };

    private static String pluginPath = "";

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private PluginInstaller pluginInstaller;

    @CanExecute
    public boolean canExecute() {
        return StringUtils.isEmpty(pluginPath);
    }

    @Execute
    public void loadPlugin() throws BundleException {
        Shell activeShell = Display.getCurrent().getActiveShell();
        FileDialog dialog = new FileDialog(activeShell);
        dialog.setFilterNames(FILTER_NAMES);
        dialog.setFilterExtensions(FILTER_EXTS);

        String filePath = dialog.open();
        if (StringUtils.isNotEmpty(filePath)) {
            try {
                Bundle bundle = pluginInstaller.installPlugin(InternalPlatform.getDefault().getBundleContext(),
                        new File(filePath).toURI().toString());
                if (bundle != null && bundle.getSymbolicName().equals(IdConstants.JIRA_PLUGIN_ID)) {
                    eventBroker.post(EventConstants.JIRA_PLUGIN_INSTALLED, null);
                }	

                MessageDialog.openInformation(activeShell, GlobalStringConstants.INFO, "Plugin successfully installed");
                pluginPath = filePath;
            } catch (BundleException e) {
                MessageDialog.openError(activeShell, GlobalStringConstants.ERROR, "Unable to install plugin");
                LoggerSingleton.logError(e);
            }
        }
    }

    public static String getPluginPath() {
        return pluginPath;
    }

    public static void resetPluginPath() {
        pluginPath = "";
    }
}
