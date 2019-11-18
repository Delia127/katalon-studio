package com.kms.katalon.composer.mobile.execution.handler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.installer.InstallationCommandStep;
import com.kms.katalon.composer.components.impl.installer.InstallationManager;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.dialog.IosIdentitySelectionDialog;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.identity.IosIdentityInfo;

public class IosInstallWebDriverAgent {

    private static final String WEB_DRIVER_AGENT_FOLDER = "/usr/local/lib/node_modules/appium/node_modules/appium-webdriveragent";

    private static final String RUN_WEB_DRIVER_AGENT_BOOTSTRAP = "sh ./Scripts/bootstrap.sh -d";

    private static final String WEB_DRIVER_AGENT_LIB_TARGET_NAME = "WebDriverAgentLib";

    private static final String WEB_DRIVER_AGENT_RUNNER_TARGET_NAME = "WebDriverAgentRunner";

    private static final String INSTALL_WEB_DRIVER_AGENT_LOG_NAME = "InstallWebDriverAgent";

    private IosIdentitySelectionDialog identitySelectionDialog;

    @CanExecute
    public boolean canExecute() {
        return SystemUtils.IS_OS_MAC;
    }

    @Execute
    public void execute(Shell shell) {
        IosIdentityInfo identity = getDevelopmentIdentity(shell);
        if (identity == null) {
            return;
        }

        installWebDriverAgent(shell, identity);
    }

    private IosIdentityInfo getDevelopmentIdentity(Shell shell) {
        identitySelectionDialog = new IosIdentitySelectionDialog(shell);
        if (identitySelectionDialog.open() != Window.OK) {
            return null;
        }

        IosIdentityInfo identity = identitySelectionDialog.getIdentity();
        if (identity == null) {
            return null;
        }

        return identity;
    }

    private void installWebDriverAgent(Shell shell, IosIdentityInfo identity) {
        try {
            InstallationManager installationManager = new InstallationManager(shell,
                    StringConstants.MSG_IOS_INSTALL_WEB_DRIVER_AGENT);
            installationManager.getInstallationDialog()
                    .setDialogTitle(StringConstants.MSG_IOS_INSTALL_WEB_DRIVER_AGENT_TITLE);
            installationManager.getInstallationDialog()
                    .setSucceededMessage(StringConstants.MSG_IOS_INSTALL_WEB_DRIVER_AGENT_SUCCESSFULLY);
            File installationLog = getLogFile(INSTALL_WEB_DRIVER_AGENT_LOG_NAME);
            File installationErrorLog = getLogFile(INSTALL_WEB_DRIVER_AGENT_LOG_NAME + "Error");

            appendStep(installationManager, installationLog, installationErrorLog,
                    StringConstants.MSG_IOS_RUN_BOOTSTRAP, RUN_WEB_DRIVER_AGENT_BOOTSTRAP, WEB_DRIVER_AGENT_FOLDER);
            appendStep(installationManager, installationLog, installationErrorLog,
                    StringConstants.MSG_IOS_BUILD_WEB_DRIVER_AGENT_LIB,
                    generateBuildCommand(WEB_DRIVER_AGENT_LIB_TARGET_NAME, identity.getId()), WEB_DRIVER_AGENT_FOLDER);
            appendStep(installationManager, installationLog, installationErrorLog,
                    StringConstants.MSG_IOS_BUILD_WEB_DRIVER_AGENT_RUNNER,
                    generateBuildCommand(WEB_DRIVER_AGENT_RUNNER_TARGET_NAME, identity.getId()),
                    WEB_DRIVER_AGENT_FOLDER);

            installationManager.startInstallation();
        } catch (IOException | InvocationTargetException error) {
            LoggerSingleton.logError(error);
            MultiStatusErrorDialog.showErrorDialog(StringConstants.MSG_IOS_INSTALL_WEB_DRIVER_AGENT_FAILED,
                    error.getMessage(), ExceptionsUtil.getStackTraceForThrowable(error));
        } catch (InterruptedException error) {
            LoggerSingleton.logInfo(StringConstants.MSG_IOS_CANCELLED_WEB_DRIVER_AGENT_INSTALLATION);
        }
    }

    private void appendStep(InstallationManager installationManager, File logFile, File errorLogFile, String title,
            String command, String workingDirectory) throws IOException, InterruptedException {
        InstallationCommandStep installationStep = new InstallationCommandStep(title, logFile, errorLogFile, command,
                workingDirectory);
        Map<String, String> envs = IosDeviceInfo.getIosAdditionalEnvironmentVariables();
        installationStep.setEnvironments(envs);
        installationManager.appendStep(installationStep);
    }

    private String generateBuildCommand(String target, String teamId) {
        return String.format("xcodebuild build -target %s -destination generic/platform=iOS DEVELOPMENT_TEAM=\"%s\"",
                target, teamId);
    }

    private File getLogFile(String name) {
        try {
            return File.createTempFile(name, ".log");
        } catch (IOException e) {
            return null;
        }
    }
}
