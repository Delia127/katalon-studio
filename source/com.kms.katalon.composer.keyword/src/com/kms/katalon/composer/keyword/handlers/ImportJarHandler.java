package com.kms.katalon.composer.keyword.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.ProjectController;

public class ImportJarHandler {
    @Inject
    IEventBroker eventBroker;

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            FileDialog dialog = new FileDialog(parentShell);
            String[] jarExtension = { "*.jar" };
            dialog.setFilterExtensions(jarExtension);
            String path = dialog.open();
            if (path == null) {
                return;
            }
            File file = new File(path);
            StringBuilder content = new StringBuilder();
            readFileToStringBuilder(file, content);
            
        } catch (IOException | URISyntaxException e) {
            //should show dialog error.
            LoggerSingleton.logError(e);
        }

    }
    
    public static void readFileToStringBuilder(File jarFile, StringBuilder sb) throws IOException, URISyntaxException {
        if (jarFile.isFile()) {
            JarFile jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.endsWith("class")) {
                    StringBuilderWriter sbWriter = new StringBuilderWriter(new StringBuilder());
                    IOUtils.copy(jar.getInputStream(jarEntry), sbWriter);
                    sbWriter.flush();
                    sbWriter.close();
                    sb.append(sbWriter.getBuilder());
                    break;
                }
            }
            jar.close();
        }
    }
}
