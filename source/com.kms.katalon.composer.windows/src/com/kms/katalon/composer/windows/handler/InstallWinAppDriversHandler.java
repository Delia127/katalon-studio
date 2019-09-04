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

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.execution.classpath.ClassPathResolver;

public class InstallWinAppDriversHandler {
    private static final String RESOURCES_FOLDER_NAME = "resources";

    private static final String RELATIVE_PATH_TO_WINAPPDRIVER = File.separator + "extension" + File.separator
            + "WinAppDriver" + File.separator + "WindowsApplicationDriver.msi";

    @CanExecute
    public boolean canExecute() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    @Execute
    public void execute(Shell shell) throws IOException, InterruptedException {
        runWinAppDriverInstaller();
    }

    public static void killProcessRunning() throws InterruptedException, IOException {
        killProcessOnWindows("WinAppDriver.exe");
    }

    private File getWinAppDriversResourcesDirectory() throws IOException {
        // check package
        Bundle bundleExec = Platform.getBundle(IdConstants.KATALON_WINDOWS_BUNDLE_ID);
        File bundleFile = FileLocator.getBundleFile(bundleExec);
        // check directory
        if (bundleFile.isDirectory()) {
            return new File(bundleFile + File.separator + RESOURCES_FOLDER_NAME);
        }
        return new File(ClassPathResolver.getConfigurationFolder() + File.separator + RESOURCES_FOLDER_NAME);
    }

    private void runWinAppDriverInstaller() throws IOException, InterruptedException {
        killProcessRunning();
        String winAppDriversAddonSetupPath = getWinAppDriversResourcesDirectory().getAbsolutePath()
                + RELATIVE_PATH_TO_WINAPPDRIVER;
        Desktop desktop = Desktop.getDesktop();
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        desktop.open(new File(winAppDriversAddonSetupPath));
    }
}
