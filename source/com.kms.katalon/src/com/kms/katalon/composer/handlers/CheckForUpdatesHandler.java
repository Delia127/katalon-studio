package com.kms.katalon.composer.handlers;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.composer.components.impl.handler.CommandCaller;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.update.UpdateComponent;
import com.kms.katalon.composer.update.UpdaterLauncher;
import com.kms.katalon.composer.update.dialogs.InstallUpdateConfirmationDialog;
import com.kms.katalon.composer.update.dialogs.NewUpdateDialog;
import com.kms.katalon.composer.update.jobs.CheckForUpdatesJob;
import com.kms.katalon.composer.update.jobs.CheckForUpdatesJob.CheckForUpdateResult;
import com.kms.katalon.composer.update.jobs.DownloadUpdateJob;
import com.kms.katalon.composer.update.models.LastestVersionInfo;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;

public class CheckForUpdatesHandler implements UpdateComponent {

    private static final long DIALOG_CLOSED_DELAY_MILLIS = 500L;

    @Execute
    public void execute() {
        checkForUpdate(false);
    }

    public void checkForUpdate(boolean silenceMode) {
        CheckForUpdatesJob job = new CheckForUpdatesJob(silenceMode);
        job.setUser(!silenceMode);
        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                if (!job.getResult().isOK()) {
                    return;
                }
                CheckForUpdateResult newUpdateResult = job.getUpdateResult();
                switch (newUpdateResult.getUpdateResult()) {
                    case NEW_UPDATE_FOUND:
                        Executors.newSingleThreadExecutor().submit(() -> {
                            try {
                                // Wait for Checking for Update dialog closes
                                TimeUnit.MILLISECONDS.sleep(DIALOG_CLOSED_DELAY_MILLIS);
                            } catch (InterruptedException ignored) {}
                            UISynchronizeService.syncExec(() -> openNewUpdateDialog(newUpdateResult));
                        });
                        break;
                    case UP_TO_DATE:
                        if (!silenceMode) {
                            Executors.newSingleThreadExecutor().submit(() -> {
                                try {
                                    // Wait for Checking for Update dialog closes
                                    TimeUnit.MILLISECONDS.sleep(DIALOG_CLOSED_DELAY_MILLIS);
                                } catch (InterruptedException ignored) {}
                                UISynchronizeService.syncExec(() -> showUpToDateDialog());
                            });
                        }
                        break;
                    case APPLIED_LEGACY_MECHANISM:
                        Executors.newSingleThreadExecutor().submit(() -> {
                            try {
                                // Wait for Checking for Update dialog closes
                                TimeUnit.MILLISECONDS.sleep(DIALOG_CLOSED_DELAY_MILLIS);
                            } catch (InterruptedException ignored) {}
                            if (VersionUtil.hasNewVersion()) {
                                UISynchronizeService.syncExec(() -> {
                                    boolean wantDownload = MessageDialog.openConfirm(null,
                                            MessageConstants.DIA_UPDATE_NEW_VERSION_TITLE,
                                            MessageConstants.DIA_UPDATE_NEW_VERSION_MESSAGE);
                                    if (wantDownload) {
                                        VersionUtil.gotoDownloadPage();
                                    }
                                });
                                return;
                            }
                            if (!silenceMode) {
                                UISynchronizeService.syncExec(() -> showUpToDateDialog());
                            }
                        });
                        break;
                    default:
                        break;
                }
            }

            private void showUpToDateDialog() {
                MessageDialog.openInformation(Display.getCurrent().getActiveShell(), StringConstants.INFO,
                        MessageConstants.HAND_MSG_UP_TO_DATE);
            }
        });
        job.schedule();
    }

    private void openNewUpdateDialog(CheckForUpdateResult newUpdateResult) {
        NewUpdateDialog newUpdateDialog = new NewUpdateDialog(Display.getCurrent().getActiveShell(), newUpdateResult);
        switch (newUpdateDialog.open()) {
            case NewUpdateDialog.DOWNLOAD_ID: {
                DownloadUpdateJob updateJob = new DownloadUpdateJob(newUpdateResult);
                updateJob.setUser(true);
                updateJob.addJobChangeListener(new JobChangeAdapter() {
                    @Override
                    public void done(IJobChangeEvent event) {
                        if (event.getResult().isOK()) {
                            Thread thread = new Thread(() -> {
                                try {
                                    // Wait for Download Update dialog closes
                                    TimeUnit.MILLISECONDS.sleep(DIALOG_CLOSED_DELAY_MILLIS);
                                } catch (InterruptedException ignored) {}
                                UISynchronizeService.syncExec(() -> onNewUpdateAlreadyDialog(newUpdateResult));
                            });
                            thread.start();
                        }
                    }
                });

                updateJob.schedule();
                return;
            }
            case NewUpdateDialog.IGNORE_UPDATE_ID: {
                try {
                    LastestVersionInfo localLatestVersion = getUpdateManager().getLocalLatestVersion();
                    localLatestVersion.setLatestVersion(newUpdateResult.getLatestVersionInfo().getLatestVersion());
                    localLatestVersion.setLatestVersionIgnored(true);
                    getUpdateManager().saveLocalLatestVersionInfo(localLatestVersion);
                } catch (IOException e) {
                    LoggerSingleton.logError(e);
                }
                return;
            }
            case NewUpdateDialog.REMIND_LATER_ID: {
                return;
            }
        }
    }

    private void onNewUpdateAlreadyDialog(CheckForUpdateResult newUpdateResult) {
        InstallUpdateConfirmationDialog dialog = new InstallUpdateConfirmationDialog(
                Display.getCurrent().getActiveShell());
        if (dialog.open() == InstallUpdateConfirmationDialog.OK) {
            try {
                Thread t = new Thread(() -> {
                    // start Katalon Updater
                    UpdaterLauncher updaterLauncher;
                    try {
                        updaterLauncher = new UpdaterLauncher(newUpdateResult.getLatestVersionInfo().getLatestVersion(),
                                newUpdateResult.getCurrentAppInfo().getVersion());

                        updaterLauncher.startUpdaterLauncher();
                    } catch (IOException | InterruptedException e) {

                    }
                });
                t.setDaemon(true);
                t.start();

                // quit Katalon
                new CommandCaller().call(IdConstants.QUIT_COMMAND_ID);
            } catch (CommandException e) {
                MessageDialog.openError(null, GlobalStringConstants.ERROR,
                        MessageConstants.HAND_MSG_UNABLE_TO_INSTALL_NEW_UPDATE);
                LoggerSingleton.logError(e);
            }
            return;
        }
    }
}
