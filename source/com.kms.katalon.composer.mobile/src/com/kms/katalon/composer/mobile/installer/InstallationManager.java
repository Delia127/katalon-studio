package com.kms.katalon.composer.mobile.installer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;

public class InstallationManager {

    private ComponentInstallerDialog installationDialog;

    private Queue<InstallationStep> installationSteps;

    private String title = "Install Requested Components";
    
    private int totalSteps = 0;

    private int worked = 0;

    private List<File> trackedLogs;

    public InstallationManager(Shell shell) {
        this(shell, null);
    }
    
    public InstallationManager(Shell shell, String title) {
        setInstallationSteps(new LinkedList<>());
        trackedLogs = new ArrayList<>();
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
        try {
            getInstallationDialog().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    UISynchronizeService.syncExec(() -> monitor.beginTask(title, totalSteps));
                    while (!getInstallationSteps().isEmpty()) {
                        InstallationStep step = getInstallationSteps().poll();
                        try {
                            runStep(step);
                        } catch (RunInstallationStepException error) {
                            handleFailedStep(step, error);
                            break;
                        }
                    }
                    UISynchronizeService.syncExec(() -> monitor.done());
                };
            });
        } catch (InterruptedException | InvocationTargetException error) {
            getInstallationDialog().close();
            throw error;
        }
    }
    
    private void handleFailedStep(InstallationStep step, RunInstallationStepException error) {
        UISynchronizeService.syncExec(() -> {
            getInstallationDialog().appendWarning("\r\nFailed to run the installation step: " + step.getTitle() + "\r\n");
            getInstallationDialog().appendWarning(error.getTargetException().getMessage() + "\r\n");
            getInstallationDialog().setFailedMessage(error.getMessage());
        });
    }

    private void runStep(InstallationStep step) throws InvocationTargetException, InterruptedException {
        Thread logTrackingThread = null;
        Thread errorTrackingThread = null;
        try {
            notifyStartNextStep(step);
            logTrackingThread = startLogTrackingThread(step.getLogFile());
            errorTrackingThread = startErrorLogTrackingThread(step.getErrorLogFile());
            step.run(getInstallationDialog().getProgressMonitor());
            stopLogTrackingThread(logTrackingThread);
            stopLogTrackingThread(errorTrackingThread);
            handleStepResults(null, step);
            Thread.sleep(1000L); // wait for the last line of the current run log to be appended
        } catch (InvocationTargetException | InterruptedException error) {
            stopLogTrackingThread(logTrackingThread);
            stopLogTrackingThread(errorTrackingThread);
            throw error;
        }
        
    }

    private Thread startLogTrackingThread(File logFile) {
        if (trackedLogs.contains(logFile)) {
            return null;
        }
        trackedLogs.add(logFile);
        Thread trackingThread = new Thread(new Tailer(logFile, new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                UISynchronizeService.syncExec(() -> getInstallationDialog().appendInfo(line + "\r\n"));
            }
        }, 100L, true));
        trackingThread.start();
        return trackingThread;
    }

    private void stopLogTrackingThread(Thread trackingThread) {
        if (trackingThread != null && trackingThread.isAlive()) {
            trackingThread.interrupt();
        }
        trackingThread = null;
    }

    private Thread startErrorLogTrackingThread(File logFile) {
        if (trackedLogs.contains(logFile)) {
            return null;
        }
        trackedLogs.add(logFile);
        Thread trackingThread = new Thread(new Tailer(logFile, new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                UISynchronizeService.syncExec(() -> getInstallationDialog().appendError(line + "\r\n"));
            }
        }, 100L, true));
        trackingThread.start();
        return trackingThread;
    }

    private void notifyStartNextStep(InstallationStep step) {
        UISynchronizeService.syncExec(() -> {
            getInstallationDialog().getProgressMonitor()
                    .subTask(String.format(step.getTitle() + " (%d/%d)", worked, totalSteps));
            if (worked > 0) {
                getInstallationDialog().appendInfo("\r\n\r\n----------------------------------------------------\r\n\r\n");
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
