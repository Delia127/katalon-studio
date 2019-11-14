package com.kms.katalon.composer.mobile.execution.handler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.installer.InstallationCommandStep;
import com.kms.katalon.composer.mobile.installer.InstallationManager;
import com.kms.katalon.core.util.ConsoleCommandExecutor;
import com.kms.katalon.core.util.internal.ExceptionsUtil;

public class IosInstallDependenciesHandler {

    private static final String INSTALL_HOMEBREW = "/usr/bin/ruby -e \"$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)\"";

    private static final String CHECK_FOR_NODE_INSTALLED = "brew info node | grep -i \"not installed\"";

    private static final String INSTALL_NODE = "brew install node && brew unlink node && brew link node";

    private static final String INSTALL_XCODE_COMMAND_LINE_TOOL = "xcode-select --install";

    private static final String INSTALL_APPIUM = "npm install -g appium";

    private static final String INSTALL_CARTHAGE = "brew install carthage";

    private static final String INSTALL_IOS_DEPLOY = "brew install ios-deploy && brew unlink ios-deploy && brew link --overwrite ios-deploy";

    private static final String INSTALL_USBMUXD = "brew install --HEAD usbmuxd && brew unlink usbmuxd && brew link usbmuxd";

    private static final String INSTALL_LIBIMOBILEDEVICE = "brew install --HEAD libimobiledevice && brew unlink libimobiledevice && brew link libimobiledevice";

    private static final String INSTALL_IOS_WEBKIT_DEBUG_PROXY = "brew install ios-webkit-debug-proxy";
    
    private static final String INSTALL_DEPENDENCIES_LOG_NAME = "IosInstallDependencies";

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute(Shell shell) {
        InstallationManager installationManager = new InstallationManager(shell, StringConstants.MSG_IOS_INSTALL_DEPENDENCIES);
        installationManager.getInstallationDialog().setDialogTitle(StringConstants.MSG_IOS_INSTALL_DEPENDENCIES_TITLE);
        installationManager.getInstallationDialog().setSuccessfulMessage(StringConstants.MSG_IOS_INSTALL_DEPENDENCIES_SUCCESSFULLY);
        File installationLog = getLogFile(INSTALL_DEPENDENCIES_LOG_NAME);

        appendStep(installationManager, installationLog, "Installing Homebrew...", INSTALL_HOMEBREW);
        
        if (!isNodeInstalled()) {
            appendStep(installationManager, installationLog, "Installing NodeJS...", INSTALL_NODE);
        }
        
        appendStep(installationManager, installationLog, "Installing Xcode Command Line Tools...", INSTALL_XCODE_COMMAND_LINE_TOOL);
        appendStep(installationManager, installationLog, "Installing Appium...", INSTALL_APPIUM);
        appendStep(installationManager, installationLog, "Installing Carthage...", INSTALL_CARTHAGE);
        appendStep(installationManager, installationLog, "Installing iOS-Deploy...", INSTALL_IOS_DEPLOY);
        appendStep(installationManager, installationLog, "Installing usbmuxd...", INSTALL_USBMUXD);
        appendStep(installationManager, installationLog, "Installing libimobiledevice...", INSTALL_LIBIMOBILEDEVICE);
        appendStep(installationManager, installationLog, "Installing iOS-Webkit-Debug-Proxy...", INSTALL_IOS_WEBKIT_DEBUG_PROXY);

        try {
            installationManager.startInstallation();
        } catch (InvocationTargetException error) {
            LoggerSingleton.logError(error);
            MultiStatusErrorDialog.showErrorDialog(StringConstants.MSG_IOS_INSTALL_DEPENDENCIES_FAILED,
                    error.getMessage(), ExceptionsUtil.getStackTraceForThrowable(error));
        } catch (InterruptedException error) {
            LoggerSingleton.logInfo(StringConstants.MSG_IOS_CANCELLED_DEPENDENCIES_INSTALLATION);
        }
    }

    private void appendStep(InstallationManager installationManager, File logFile, String title, String command) {
        InstallationCommandStep installHomebrewStep = new InstallationCommandStep(title, logFile, command);
        installationManager.appendStep(installHomebrewStep);
    }
    
    private boolean isNodeInstalled() {
        String result = null;
        try {
            result = ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(new String[] { "/bin/sh", "-c", CHECK_FOR_NODE_INSTALLED });
        } catch (IOException | InterruptedException error) {
            LoggerSingleton.logError(error);
            MultiStatusErrorDialog.showErrorDialog(StringConstants.MSG_IOS_FAILED_TO_CHECK_NODE_INSTALLED,
                    error.getMessage(), ExceptionsUtil.getStackTraceForThrowable(error));
            return false;
        }
        return StringUtils.isBlank(result);
    }

    private File getLogFile(String name) {
        try {
            return File.createTempFile(name, ".log");
        } catch (IOException e) {
            return null;
        }
    }
}
