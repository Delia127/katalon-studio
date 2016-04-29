package com.kms.katalon.execution.launcher;

import java.io.IOException;
import java.util.List;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.process.ConsoleProcess;
import com.kms.katalon.execution.launcher.process.ILaunchProcess;
import com.kms.katalon.execution.launcher.process.LaunchProcessor;

public class ConsoleLauncher extends ReportableLauncher {
    public ConsoleLauncher(IRunConfiguration runConfig) {
        super(runConfig);
    }

    @Override
    public ReportableLauncher clone(IRunConfiguration runConfig) {
        return new ConsoleLauncher(runConfig);
    }

    @Override
    protected ILaunchProcess launch() throws ExecutionException {
        try {
            Process systemProcess = new LaunchProcessor(ClassPathResolver.getClassPaths(ProjectController.getInstance()
                    .getCurrentProject()), runConfig.getAdditionalEnvironmentVariables()).execute(getRunConfig()
                    .getExecutionSetting().getScriptFile());
            return new ConsoleProcess(systemProcess);
        } catch (IOException ex) {
            throw new ExecutionException(ex);
        }
    }

    @Override
    public synchronized void addLogRecords(List<XmlLogRecord> records) {
        super.addLogRecords(records);

        // No need to remember log records in console mode
        records.clear();
    }
}
