package com.kms.katalon.execution.launcher;

import java.io.IOException;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.process.ILaunchProcess;
import com.kms.katalon.execution.launcher.process.NonStreamHandledProcess;

public class SubConsoleLauncher extends ConsoleLauncher implements SubLauncher {

    private RunConfigurationDescription runConfigurationDescription;

    public SubConsoleLauncher(LauncherManager manager, IRunConfiguration runConfig,
            RunConfigurationDescription runConfigurationDescription) {
        super(manager, runConfig);
        this.runConfigurationDescription = runConfigurationDescription;
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

    @Override
    public RunConfigurationDescription getRunConfigurationDescription() {
        return runConfigurationDescription;
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
    
    @Override
    public ReportableLauncher clone(IRunConfiguration runConfig) {
        return new SubConsoleLauncher(getManager(), runConfig, getRunConfigurationDescription());
    }
}
