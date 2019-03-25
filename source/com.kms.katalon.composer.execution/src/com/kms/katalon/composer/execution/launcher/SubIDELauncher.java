package com.kms.katalon.composer.execution.launcher;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.launcher.ReportableLauncher;
import com.kms.katalon.execution.launcher.SubLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class SubIDELauncher extends IDELauncher implements SubLauncher {

    private RunConfigurationDescription runConfigDescription;

    public SubIDELauncher(IRunConfiguration runConfig, LaunchMode mode,
            RunConfigurationDescription runConfigDescription) {
        this(null, runConfig, mode, runConfigDescription);
    }
    
    @Override
    public String getName() {
        TestSuiteExecutedEntity testSuiteEntity = (TestSuiteExecutedEntity) getExecutedEntity();
        String name = testSuiteEntity.getSourceId() + " - " + getRunConfig().getName() + " - " + getId();
        int previousRerunTimes = testSuiteEntity.getRerunSetting().getPreviousRerunTimes();
        if (previousRerunTimes > 0) {
            name += String.format(" - Re-run %d", previousRerunTimes);
        }
        return name;
    }

    public SubIDELauncher(LauncherManager manager, IRunConfiguration runConfig, LaunchMode mode,
            RunConfigurationDescription runConfigDescription) {
        super(manager, runConfig, mode);
        this.runConfigDescription = runConfigDescription;
    }

    protected void postExecutionComplete() {
        updateReport();
    }

    @Override
    public ReportableLauncher clone(IRunConfiguration newConfig) {
        return new SubIDELauncher(getManager(), newConfig, getMode(), runConfigDescription);
    }

    @Override
    protected void sendUpdateJobViewerEvent() {
        // Remove updating job process because this is sub-launcher
    }

    @Override
    protected void sendUpdateLogViewerEvent(String message) {
        // Remove updating job process because this is sub-launcher
    }

    @Override
    public RunConfigurationDescription getRunConfigurationDescription() {
        return runConfigDescription;
    }
}
