package com.kms.katalon.composer.project.handlers;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class CleanProjectHandler {

    @CanExecute
    public boolean canExecute() {
        return !LauncherManager.getInstance().isAnyLauncherRunning();
    }

    @Execute
    public void execute() {
        final File tempDir = new File(ProjectController.getInstance().getTempDir());

        if (tempDir.exists()) {
            Job job = new Job(StringConstants.HAND_TEMP_CLEANER) {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        monitor.beginTask(StringConstants.HAND_CLEANING_TEMP_FILES, getElementsCount(tempDir));
                        deleteFileRecursively(tempDir, monitor);
                        return Status.OK_STATUS;
                    } finally {
                        monitor.done();
                    }
                }
            };

            job.setUser(true);
            job.schedule();
        }
    }

    private void deleteFileRecursively(File file, IProgressMonitor monitor) {
        if (file.getAbsolutePath().equalsIgnoreCase(ProjectController.getInstance().getNonremovableTempDir())) {
            return;
        }
        
        if (monitor.isCanceled()) {
            return;
        }

        String fileName = file.getName();
        if (fileName.length() > 60) {
            fileName = fileName.substring(0, 60) + "...";
        }
        monitor.subTask(StringConstants.HAND_CLEANING_ITEM + fileName);
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                deleteFileRecursively(childFile, monitor);
            }
        }

        file.delete();
        monitor.worked(1);
    }

    private int getElementsCount(File file) {
        int total = 1;
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                total += getElementsCount(childFile);
            }
        }
        return total;
    }
}
