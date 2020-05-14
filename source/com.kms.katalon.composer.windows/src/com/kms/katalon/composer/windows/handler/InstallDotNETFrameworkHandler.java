package com.kms.katalon.composer.windows.handler;

import static com.kms.katalon.core.util.internal.ProcessUtil.killProcessOnWindows;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.execution.classpath.ClassPathResolver;

public class InstallDotNETFrameworkHandler {
    private static final String RESOURCES_FOLDER_NAME = "resources";

    private static final String DOT_NET_FRAMEWORK_INSTALLER_FILE = "NDP452-KB2901954-Web.exe";

    private static final String RELATIVE_PATH_TO_DOT_NET_FRAMEWORK = File.separator + "extensions" + File.separator
            + "DotNETFramework" + File.separator + DOT_NET_FRAMEWORK_INSTALLER_FILE;

    @CanExecute
    public boolean canExecute() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    @Execute
    public void execute(Shell shell) {
        try {
            runDotNETFrameworkInstaller();
        } catch (IOException | InterruptedException exception) {
            LoggerSingleton.logError(exception);
        }
    }

    private void runDotNETFrameworkInstaller() throws IOException, InterruptedException {
        killRunningProcess();
        String dotNETFrameworkInstallerPath = getDotNETFrameworkResourcesDirectory().getAbsolutePath()
                + RELATIVE_PATH_TO_DOT_NET_FRAMEWORK;
        Desktop desktop = Desktop.getDesktop();
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        desktop.open(new File(dotNETFrameworkInstallerPath));
    }

    public static void killRunningProcess() {
        try {
            killProcessOnWindows(DOT_NET_FRAMEWORK_INSTALLER_FILE);
        } catch (IOException | InterruptedException exception) {
            LoggerSingleton.logError(exception);
        }
    }

    private File getDotNETFrameworkResourcesDirectory() throws IOException {
        Bundle bundleExec = Platform.getBundle(IdConstants.KATALON_WINDOWS_BUNDLE_ID);
        File bundleFile = FileLocator.getBundleFile(bundleExec);
        if (bundleFile.isDirectory()) {
            return new File(bundleFile + File.separator + RESOURCES_FOLDER_NAME);
        }
        return new File(ClassPathResolver.getConfigurationFolder() + File.separator + RESOURCES_FOLDER_NAME);
    }
}
