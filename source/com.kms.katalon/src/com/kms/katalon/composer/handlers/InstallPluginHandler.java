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
import org.osgi.framework.BundleException;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.PluginProjectEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.constants.GlobalStringConstants;

@SuppressWarnings("restriction")
public class InstallPluginHandler {

    private static final String[] FILTER_NAMES = { "Jar file (*.jar)" };

    private static final String[] FILTER_EXTS = { "*.jar" };

    private static String pluginPath = "";

    @Inject
    private IEventBroker eventBroker;

    @CanExecute
    public boolean canExecute() {
        return StringUtils.isEmpty(pluginPath);
    }

    @Execute
    public void loadPlugin() throws BundleException {
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell());
        dialog.setFilterNames(FILTER_NAMES);
        dialog.setFilterExtensions(FILTER_EXTS);

        String filePath = dialog.open();
        if (StringUtils.isNotEmpty(filePath)) {
            eventBroker.send("KATALON_PLUGIN/INSTALL", new Object[] { InternalPlatform.getDefault().getBundleContext(),
                    new File(filePath).toURI().toString() });
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), GlobalStringConstants.INFO,
                    "Plugin installed sucessfully");
            pluginPath = filePath;
            ProjectEntity project = ProjectController.getInstance().getCurrentProject();
            if(project != null){
                PluginProjectEntity currentPluginProjectEntity = new PluginProjectEntity();
                currentPluginProjectEntity.setFileLocation(project.getLocation());
                currentPluginProjectEntity.setFolderLocation(project.getFolderLocation());
                currentPluginProjectEntity.setID(project.getId());
                currentPluginProjectEntity.setName(project.getName());
                eventBroker.send("KATALON_PLUGIN/CURRENT_PROJECT_CHANGED", currentPluginProjectEntity);
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
