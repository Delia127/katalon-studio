package com.kms.katalon.composer.mobile.installer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class InstallationStep implements IRunnableWithProgress {
    private String title;

    private File logFile;

    public InstallationStep(String title, File logFile) {
        super();
        this.title = title;
        this.logFile = logFile != null ? logFile : createTempFile("");
    }

    public InstallationStep(String title) {
        this(title, createTempFile(""));
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

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        // TODO Auto-generated method stub
        
    }

}
