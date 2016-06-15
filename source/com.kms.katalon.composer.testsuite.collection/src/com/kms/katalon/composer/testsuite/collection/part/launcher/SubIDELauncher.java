package com.kms.katalon.composer.testsuite.collection.part.launcher;

import com.kms.katalon.composer.execution.launcher.IDELauncher;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class SubIDELauncher extends IDELauncher {
    
    public SubIDELauncher(IRunConfiguration runConfig, LaunchMode mode) {
        super(null, runConfig, mode);
    }

    public SubIDELauncher(LauncherManager manager, IRunConfiguration runConfig, LaunchMode mode) {
        super(manager, runConfig, mode);
    }

    protected void postExecutionComplete() {
        updateReport();
    }

    @Override
    protected void sendUpdateJobViewerEvent() {
        // Remove updating job process because this is sub-launcher
    }

    @Override
    protected void sendUpdateLogViewerEvent(String message) {
        // Remove updating job process because this is sub-launcher
    }
}
