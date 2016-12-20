package com.kms.katalon.execution.launcher;

import java.io.IOException;

import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.process.ILaunchProcess;
import com.kms.katalon.execution.launcher.process.NonStreamHandledProcess;

public class SubConsoleLauncher extends ConsoleLauncher {

    public SubConsoleLauncher(LauncherManager manager, IRunConfiguration runConfig) {
        super(manager, runConfig);
    }
    
    @Override
    protected ILaunchProcess launch() throws ExecutionException {
        try {
            Process systemProcess = executeProcess();
            return new NonStreamHandledProcess(systemProcess);
        } catch (IOException ex) {
            throw new ExecutionException(ex);
        }
    }
}
