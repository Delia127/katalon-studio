package com.kms.katalon.composer.mobile.execution.handler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.dialog.IosIdentitySelectionDialog;
import com.kms.katalon.core.util.ConsoleCommandExecutor;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.identity.IosIdentityInfo;

public class IosInstallWebDriverAgent {

    private static final String WEB_DRIVER_AGENT_FOLDER = "/usr/local/lib/node_modules/appium/node_modules/appium-webdriveragent";

    private static final String CD_WEB_DRIVER_AGENT_FOLDER = "cd " + WEB_DRIVER_AGENT_FOLDER;

    private static final String[] INSTALL_WEB_DRIVER_AGENT_DEPENDENCIES = new String[] { "/bin/sh", "-c",
            CD_WEB_DRIVER_AGENT_FOLDER + " && /bin/sh ./Scripts/bootstrap.sh -d" };

    private static final String WEB_DRIVER_AGENT_LIB_TARGET_NAME = "WebDriverAgentLib";

    private static final String WEB_DRIVER_AGENT_RUNNER_TARGET_NAME = "WebDriverAgentRunner";

    private static final long DIALOG_CLOSED_DELAY_MILLIS = 500L;

    private IosIdentitySelectionDialog identitySelectionDialog;

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute(Shell shell) {
        identitySelectionDialog = new IosIdentitySelectionDialog(shell);
        if (identitySelectionDialog.open() != Window.OK) {
            return;
        }

        IosIdentityInfo identity = identitySelectionDialog.getIdentity();
        if (identity == null) {
            return;
        }

        Job installDependencies = new Job(StringConstants.MSG_IOS_INSTALL_WEB_DRIVER_AGENT) {

            private int totalWork = 3;

            private int worked = 0;

            private void notifyStartNextSubtask(IProgressMonitor monitor, String taskName) {
                monitor.subTask(String.format(taskName + " (%d/%d)", worked, totalWork));
            }

            private void updateProgressAndCheckForCanceled(IProgressMonitor monitor) throws InterruptedException {
                worked++;
                monitor.worked(1);
                if (monitor.isCanceled()) {
                    throw new InterruptedException("User canceled install WebDriverAgent");
                }
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(StringConstants.MSG_IOS_INSTALL_WEB_DRIVER_AGENT, totalWork);
                try {
                    Map<String, String> iosEnvs = IosDeviceInfo.getIosAdditionalEnvironmentVariables();
                    List<String> results;

                    notifyStartNextSubtask(monitor, "Installing WebDriverAgent dependencies...");
                    results = ConsoleCommandExecutor
                            .runConsoleCommandAndCollectResults(INSTALL_WEB_DRIVER_AGENT_DEPENDENCIES, iosEnvs, true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                    notifyStartNextSubtask(monitor, "Building WebDriverAgentLib...");
                    results = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(
                            getBuildCommand(WEB_DRIVER_AGENT_LIB_TARGET_NAME, identity.getId()), iosEnvs, true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                    notifyStartNextSubtask(monitor, "Building WebDriverAgentRunner...");
                    results = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(
                            getBuildCommand(WEB_DRIVER_AGENT_RUNNER_TARGET_NAME, identity.getId()), iosEnvs, true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                } catch (InterruptedException e) {
                    return Status.CANCEL_STATUS;
                } catch (Exception e) {
                    return new Status(Status.ERROR, "com.kms.katalon",
                            StringConstants.MSG_IOS_INSTALL_WEB_DRIVER_AGENT_FAILED,
                            new Exception(ExceptionsUtil.getStackTraceForThrowable(e)));
                } finally {
                    monitor.done();
                }
                return Status.OK_STATUS;
            }
        };

        installDependencies.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                IStatus result = installDependencies.getResult();
                if (!result.isOK()) {
                    if (result == Status.CANCEL_STATUS) {
                        return;
                    }
                    Throwable error = result.getException();
                    LoggerSingleton.logError(StringConstants.MSG_IOS_INSTALL_WEB_DRIVER_AGENT_FAILED);
                    LoggerSingleton.logError(error);
                    UISynchronizeService.syncExec(() -> {
                        MultiStatusErrorDialog.showErrorDialog(StringConstants.MSG_IOS_INSTALL_WEB_DRIVER_AGENT_FAILED,
                                error.getMessage(), ExceptionsUtil.getStackTraceForThrowable(error));
                    });
                    return;
                }

                Executors.newSingleThreadExecutor().submit(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DIALOG_CLOSED_DELAY_MILLIS);
                    } catch (InterruptedException ignored) {}
                    UISynchronizeService.syncExec(() -> {
                        MessageDialog.openInformation(shell, StringConstants.INFO,
                                StringConstants.MSG_IOS_INSTALL_WEB_DRIVER_AGENT_SUCCESSFULLY);
                    });
                });
            }
        });

        installDependencies.setUser(true);
        installDependencies.schedule();
    }

    private String[] getBuildCommand(String target, String teamId) {
        String buildCommand = String.format(
                "xcodebuild build -target %s -destination generic/platform=iOS DEVELOPMENT_TEAM=\"%s\"", target,
                teamId);
        return new String[] { "/bin/sh", "-c", CD_WEB_DRIVER_AGENT_FOLDER + " && " + buildCommand };
    }

}
