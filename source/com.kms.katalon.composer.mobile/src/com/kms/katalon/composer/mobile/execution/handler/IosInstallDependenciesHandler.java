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
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.core.util.ConsoleCommandExecutor;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;

public class IosInstallDependenciesHandler {

    private static final String[] INSTALL_HOMEBREW = new String[] { "/bin/sh", "-c",
            "/usr/bin/ruby -e \"$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)\"" };

    private static final String[] INSTALL_NODE = new String[] { "/bin/sh", "-c",
            "brew install node && brew unlink node && brew link node" };

    private static final String[] INSTALL_XCODE_COMMAND_LINE_TOOL = new String[] { "/bin/sh", "-c",
            "xcode-select --install" };

    private static final String[] INSTALL_APPIUM = new String[] { "/bin/sh", "-c", "npm install -g appium" };

    private static final String[] INSTALL_CARTHAGE = new String[] { "/bin/sh", "-c", "brew install carthage" };

    private static final String[] INSTALL_IOS_DEPLOY = new String[] { "/bin/sh", "-c",
            "brew install ios-deploy && brew unlink ios-deploy && brew link --overwrite ios-deploy" };

    private static final String[] INSTALL_USBMUXD = new String[] { "/bin/sh", "-c",
            "brew install --HEAD usbmuxd && brew unlink usbmuxd && brew link usbmuxd" };

    private static final String[] INSTALL_LIBIMOBILEDEVICE = new String[] { "/bin/sh", "-c",
            "brew install --HEAD libimobiledevice && brew unlink libimobiledevice && brew link libimobiledevice" };

    private static final String[] INSTALL_IOS_WEBKIT_DEBUG_PROXY = new String[] { "/bin/sh", "-c",
            "brew install ios-webkit-debug-proxy" };

    private static final long DIALOG_CLOSED_DELAY_MILLIS = 500L;

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute(Shell shell) {
        Job installDependencies = new Job(StringConstants.MSG_IOS_INSTALL_DEPENDENCIES) {

            private int totalWork = 9;

            private int worked = 0;

            private void notifyStartNextSubtask(IProgressMonitor monitor, String taskName) {
                monitor.subTask(String.format(taskName + " (%d/%d)", worked, totalWork));
            }

            private void updateProgressAndCheckForCanceled(IProgressMonitor monitor) throws InterruptedException {
                worked++;
                monitor.worked(1);
                if (monitor.isCanceled()) {
                    throw new InterruptedException("User canceled install dependencies");
                }
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(StringConstants.MSG_IOS_INSTALL_DEPENDENCIES, totalWork);
                try {

                    Map<String, String> iosEnvs = IosDeviceInfo.getIosAdditionalEnvironmentVariables();
                    List<String> results;

                    notifyStartNextSubtask(monitor, "Installing Homebrew...");
                    results = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(INSTALL_HOMEBREW, iosEnvs,
                            true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                    notifyStartNextSubtask(monitor, "Installing NodeJS...");
                    results = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(INSTALL_NODE, iosEnvs, true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                    notifyStartNextSubtask(monitor, "Installing Xcode command line tool...");
                    results = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(INSTALL_XCODE_COMMAND_LINE_TOOL,
                            iosEnvs, true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                    notifyStartNextSubtask(monitor, "Installing Appium...");
                    results = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(INSTALL_APPIUM, iosEnvs, true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                    notifyStartNextSubtask(monitor, "Installing Carthage...");
                    results = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(INSTALL_CARTHAGE, iosEnvs,
                            true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                    notifyStartNextSubtask(monitor, "Installing iOS-Deploy...");
                    results = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(INSTALL_IOS_DEPLOY, iosEnvs,
                            true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                    notifyStartNextSubtask(monitor, "Installing usbmuxd...");
                    results = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(INSTALL_USBMUXD, iosEnvs, true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                    notifyStartNextSubtask(monitor, "Install libimobiledevice...");
                    results = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(INSTALL_LIBIMOBILEDEVICE,
                            iosEnvs, true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                    notifyStartNextSubtask(monitor, "Installing iOS-Webkit-Debug-Proxy...");
                    results = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(INSTALL_IOS_WEBKIT_DEBUG_PROXY,
                            iosEnvs, true);
                    LoggerSingleton.logInfo(String.join("\r\n", results));
                    updateProgressAndCheckForCanceled(monitor);

                } catch (InterruptedException e) {
                    return Status.CANCEL_STATUS;
                } catch (Exception e) {
                    return new Status(Status.ERROR, "com.kms.katalon",
                            StringConstants.MSG_IOS_INSTALL_DEPENDENCIES_FAILED,
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
                    LoggerSingleton.logError(StringConstants.MSG_IOS_INSTALL_DEPENDENCIES_FAILED);
                    LoggerSingleton.logError(error);
                    UISynchronizeService.syncExec(() -> {
                        MultiStatusErrorDialog.showErrorDialog(StringConstants.MSG_IOS_INSTALL_DEPENDENCIES_FAILED,
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
                                StringConstants.MSG_IOS_INSTALL_DEPENDENCIES_SUCCESSFULLY);
                    });
                });
            }
        });

        installDependencies.setUser(true);
        installDependencies.schedule();
    }

}
