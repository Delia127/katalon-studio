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

public class InstallWinAppDriversHandler {
    private static final String RESOURCES_FOLDER_NAME = "resources";

    private static final String RELATIVE_PATH_TO_WINAPPDRIVER = File.separator + "extensions" + File.separator
            + "WinAppDriver" + File.separator + "WindowsApplicationDriver.msi";

    @CanExecute
    public boolean canExecute() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    @Execute
    public void execute(Shell shell) throws IOException, InterruptedException {
        try {
            runWinAppDriverInstaller();
        } catch (IOException | InterruptedException e) {
            LoggerSingleton.logError(e);
        }
    }

    public static void killRunningProcess() throws InterruptedException, IOException {
        try {
            killProcessOnWindows("WinAppDriver.exe");
        } catch (IOException | InterruptedException e) {
            LoggerSingleton.logError(e);
        }
    }

    private File getWinAppDriversResourcesDirectory() throws IOException {
        Bundle bundleExec = Platform.getBundle(IdConstants.KATALON_WINDOWS_BUNDLE_ID);
        File bundleFile = FileLocator.getBundleFile(bundleExec);
        if (bundleFile.isDirectory()) {
            return new File(bundleFile + File.separator + RESOURCES_FOLDER_NAME);
        }
        return new File(ClassPathResolver.getConfigurationFolder() + File.separator + RESOURCES_FOLDER_NAME);
    }

    private void runWinAppDriverInstaller() throws IOException, InterruptedException {
        killRunningProcess();
        String winAppDriversAddonSetupPath = getWinAppDriversResourcesDirectory().getAbsolutePath()
                + RELATIVE_PATH_TO_WINAPPDRIVER;
        Desktop desktop = Desktop.getDesktop();
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        desktop.open(new File(winAppDriversAddonSetupPath));
    }
}
