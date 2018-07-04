package com.kms.katalon.execution.launcher;

import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.process.ILaunchProcess;
import com.kms.katalon.execution.launcher.process.RecordingProcess;

public class RecordingScriptLauncher extends VerificationScriptLauncher {
    
    private ILaunchProcess launchProcess;

    public RecordingScriptLauncher(LauncherManager manager, IRunConfiguration runConfig,
            Runnable processFinishedRunnable) {
        super(manager, runConfig, processFinishedRunnable);
    }
    
    @Override
    protected ILaunchProcess onCreateLaunchProcess(Process systemProcess) {
        launchProcess = new RecordingProcess(systemProcess);
        
        return launchProcess;
    }

    
    
}
