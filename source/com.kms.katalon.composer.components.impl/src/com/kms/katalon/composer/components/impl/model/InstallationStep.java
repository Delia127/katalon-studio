package com.kms.katalon.composer.components.impl.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class InstallationStep implements IRunnableWithProgress {
    private String title;

    private File logFile;

    private File errorLogFile;

    public InstallationStep(String title, File logFile, File errorLogFile) {
        super();
        this.title = title;
        this.setLogFile(logFile != null ? logFile : createTempFile("InstallationLog"));
        this.setErrorLogFile(errorLogFile != null ? errorLogFile : createTempFile("InstallationErrorLog"));
    }

    public InstallationStep(String title, File logFile) {
        this(title, logFile, null);
    }

    public InstallationStep(String title) {
        this(title, null, null);
    }

    private static File createTempFile(String name) {
        try {
            return File.createTempFile(name, ".log");
        } catch (IOException e) {
            return null;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public File getErrorLogFile() {
        return errorLogFile;
    }

    public void setErrorLogFile(File errorLogFile) {
        this.errorLogFile = errorLogFile;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        // TODO Auto-generated method stub
        
    }

}
