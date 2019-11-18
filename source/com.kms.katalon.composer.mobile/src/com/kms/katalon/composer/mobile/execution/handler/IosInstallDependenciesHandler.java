package com.kms.katalon.composer.mobile.execution.handler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SystemUtils;
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

    private static final String CHECK_FOR_MODULE_IS_INSTALLED = "which {0} | grep -i \"{0}\"";

    private static final String INSTALL_NODE = "brew install node && brew unlink node && brew link node";

    private static final String INSTALL_XCODE_COMMAND_LINE_TOOL = "brew list appium && xcode-select --install";

    private static final String INSTALL_APPIUM = "npm install -g appium";

    private static final String INSTALL_CARTHAGE = "brew install carthage";

    private static final String INSTALL_IOS_DEPLOY = "brew install ios-deploy && brew unlink ios-deploy && brew link --overwrite ios-deploy";

    private static final String INSTALL_USBMUXD = "brew install --HEAD usbmuxd && brew unlink usbmuxd && brew link usbmuxd";

    private static final String INSTALL_LIBIMOBILEDEVICE = "brew install --HEAD libimobiledevice && brew unlink libimobiledevice && brew link libimobiledevice";

    private static final String INSTALL_IOS_WEBKIT_DEBUG_PROXY = "brew install ios-webkit-debug-proxy";

    private static final String INSTALL_DEPENDENCIES_LOG_NAME = "IosInstallDependencies";

    @CanExecute
    public boolean canExecute() {
        return SystemUtils.IS_OS_MAC;
    }

    @Execute
    public void execute(Shell shell) {
        InstallationManager installationManager = new InstallationManager(shell,
                StringConstants.MSG_IOS_INSTALL_DEPENDENCIES);
        installationManager.getInstallationDialog().setDialogTitle(StringConstants.MSG_IOS_INSTALL_DEPENDENCIES_TITLE);
        installationManager.getInstallationDialog()
                .setSucceededMessage(StringConstants.MSG_IOS_INSTALL_DEPENDENCIES_SUCCESSFULLY);
        File installationLog = getLogFile(INSTALL_DEPENDENCIES_LOG_NAME);
        File installationErrorLog = getLogFile(INSTALL_DEPENDENCIES_LOG_NAME + "Error");

        if (!isBrewInstalled()) {
            appendStep(installationManager, installationLog, installationErrorLog, "Installing Homebrew...", INSTALL_HOMEBREW);
        }

        if (!isNodeInstalled()) {
            appendStep(installationManager, installationLog, installationErrorLog, "Installing NodeJS...", INSTALL_NODE);
        }

        appendStep(installationManager, installationLog, installationErrorLog, "Installing Xcode Command Line Tools...",
                INSTALL_XCODE_COMMAND_LINE_TOOL);
        appendStep(installationManager, installationLog, installationErrorLog, "Installing Appium...", INSTALL_APPIUM);
        appendStep(installationManager, installationLog, installationErrorLog, "Installing Carthage...", INSTALL_CARTHAGE);
        appendStep(installationManager, installationLog, installationErrorLog, "Installing iOS-Deploy...", INSTALL_IOS_DEPLOY);
        appendStep(installationManager, installationLog, installationErrorLog, "Installing usbmuxd...", INSTALL_USBMUXD);
        appendStep(installationManager, installationLog, installationErrorLog, "Installing libimobiledevice...", INSTALL_LIBIMOBILEDEVICE);
        appendStep(installationManager, installationLog, installationErrorLog, "Installing iOS-Webkit-Debug-Proxy...",
                INSTALL_IOS_WEBKIT_DEBUG_PROXY);

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

    private void appendStep(InstallationManager installationManager, File logFile, File errorLogFile, String title, String command) {
        InstallationCommandStep installHomebrewStep = new InstallationCommandStep(title, logFile, errorLogFile, command);
        installationManager.appendStep(installHomebrewStep);
    }

    private boolean isNodeInstalled() {
        return isModuleInstalled("node");
    }

    private boolean isBrewInstalled() {
        return isModuleInstalled("brew");
    }

    private boolean isModuleInstalled(String moduleName) {
        String result = null;
        try {
            result = ConsoleCommandExecutor.runConsoleCommandAndCollectFirstResult(new String[] { "/bin/sh", "-c",
                    MessageFormat.format(CHECK_FOR_MODULE_IS_INSTALLED, moduleName) });
        } catch (IOException | InterruptedException error) {
            LoggerSingleton.logError(error);
            MultiStatusErrorDialog.showErrorDialog(MessageFormat.format(StringConstants.MSG_IOS_FAILED_TO_CHECK_MODULE_IS_INSTALLED, moduleName),
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
