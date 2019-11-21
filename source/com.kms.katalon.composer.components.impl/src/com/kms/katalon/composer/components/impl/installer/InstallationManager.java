package com.kms.katalon.composer.components.impl.installer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.ComponentInstallerDialog;
import com.kms.katalon.composer.components.impl.exception.RunInstallationStepException;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;

public class InstallationManager {
    private ComponentInstallerDialog installationDialog;

    private Queue<InstallationStep> installationSteps;

    private String title = "Component Installer";

    private int totalSteps = 0;

    private int worked = 0;

    private Map<File, Thread> trackingThreads;

    public InstallationManager(Shell shell) {
        this(shell, null);
    }

    public InstallationManager(Shell shell, String title) {
        setInstallationSteps(new LinkedList<>());
        trackingThreads = new HashMap<>();
        if (title != null) {
            this.title = title;
        }
        setInstallationDialog(new ComponentInstallerDialog(shell));
    }

    public void appendStep(InstallationStep step) {
        getInstallationSteps().add(step);
    }

    public void startInstallation() throws InvocationTargetException, InterruptedException {
        worked = 0;
        totalSteps = getInstallationSteps().size();
        getInstallationDialog().run(true, true, new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                UISynchronizeService.syncExec(() -> monitor.beginTask(title, totalSteps));
                while (!getInstallationSteps().isEmpty()) {
                    InstallationStep step = getInstallationSteps().poll();
                    try {
                        runStep(step);
                    } catch (RunInstallationStepException error) {
                        Thread.sleep(1000L); // wait for the last line of the current run log to be appended
                        handleFailedStep(step, error);
                        break;
                    }
                }
                stopAllTrackingThreads();
                UISynchronizeService.syncExec(() -> monitor.done());
            };
        });
    }

    private void handleFailedStep(InstallationStep step, RunInstallationStepException error) throws InterruptedException {
        UISynchronizeService.syncExec(() -> {
            getInstallationDialog()
                    .appendWarning("\r\nFailed to run the installation step: " + step.getTitle() + "\r\n");
            getInstallationDialog().appendWarning(error.getTargetException().getMessage() + "\r\n");
            getInstallationDialog().setFailureMessage(error.getMessage());
        });
    }

    private void runStep(InstallationStep step) throws InvocationTargetException, InterruptedException {
        notifyStartNextStep(step);
        startLogTrackingThread(step.getLogFile());
        startErrorLogTrackingThread(step.getErrorLogFile());
        Thread.sleep(1000L); // wait for the log file to be tracked

        step.run(getInstallationDialog().getProgressMonitor());
        Thread.sleep(1000L); // wait for the last line of the current run log to be appended

        handleStepResults(null, step);

    }

    private Thread startLogTrackingThread(File logFile) {
        if (trackingThreads.containsKey(logFile)) {
            return null;
        }
        Thread trackingThread = new Thread(new CustomTailer(logFile, new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                UISynchronizeService.syncExec(() -> getInstallationDialog().appendInfo(line + "\r\n"));
            }
        }, 100L, true));
        trackingThreads.put(logFile, trackingThread);
        trackingThread.start();
        return trackingThread;
    }

    private Thread startErrorLogTrackingThread(File errorLogFile) {
        if (trackingThreads.containsKey(errorLogFile)) {
            return null;
        }
        Thread trackingThread = new Thread(new CustomTailer(errorLogFile, new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                UISynchronizeService.syncExec(() -> getInstallationDialog().appendError(line + "\r\n"));
            }
        }, 100L, true));
        trackingThreads.put(errorLogFile, trackingThread);
        trackingThread.start();
        return trackingThread;
    }

    private void stopAllTrackingThreads() throws InterruptedException {
        trackingThreads.forEach((logFile, trackingThread) -> {
            if (trackingThread != null && trackingThread.isAlive()) {
                trackingThread.interrupt();
            }
            trackingThread = null;
        });
        trackingThreads.clear();
    }

    private void notifyStartNextStep(InstallationStep step) {
        UISynchronizeService.syncExec(() -> {
            getInstallationDialog().getProgressMonitor()
                    .subTask(String.format(step.getTitle() + " (%d/%d)", worked, totalSteps));
            if (worked > 0) {
                getInstallationDialog()
                        .appendInfo("\r\n\r\n----------------------------------------------------\r\n\r\n");
            }
            getInstallationDialog().appendInfo(step.getTitle() + "\r\n\r\n");
        });
    }

    private void handleStepResults(List<String> results, InstallationStep step) throws InterruptedException {
        if (results != null) {
            LoggerSingleton.logInfo(String.join("\r\n", results));
        }
        worked++;
        UISynchronizeService.syncExec(() -> {
            getInstallationDialog().getProgressMonitor().worked(1);
            getInstallationDialog().getProgressMonitor()
                    .subTask(String.format(step.getTitle() + " (%d/%d)", worked, totalSteps));
        });
        if (getInstallationDialog().getProgressMonitor().isCanceled()) {
            throw new InterruptedException("User cancelled installation.");
        }
    }

    public ComponentInstallerDialog getInstallationDialog() {
        return installationDialog;
    }

    public void setInstallationDialog(ComponentInstallerDialog installationDialog) {
        this.installationDialog = installationDialog;
    }

    private Queue<InstallationStep> getInstallationSteps() {
        return installationSteps;
    }

    private void setInstallationSteps(Queue<InstallationStep> installationSteps) {
        this.installationSteps = installationSteps;
    }
}
