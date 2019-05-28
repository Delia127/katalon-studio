package com.kms.katalon.execution.launcher;

import java.io.IOException;
import java.util.List;

import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.process.ConsoleProcess;
import com.kms.katalon.execution.launcher.process.ILaunchProcess;
import com.kms.katalon.execution.launcher.process.LaunchProcessor;
import com.kms.katalon.tracking.service.Trackings;

public class ConsoleLauncher extends ReportableLauncher implements IConsoleLauncher {
    public ConsoleLauncher(LauncherManager manager, IRunConfiguration runConfig) {
        super(manager, runConfig);
    }

    @Override
    public ReportableLauncher clone(IRunConfiguration runConfig) {
        return new ConsoleLauncher(getManager(), runConfig);
    }

    @Override
    protected ILaunchProcess launch() throws ExecutionException {
        try {
            Process systemProcess = executeProcess();
            if (systemProcess == null) {
                throw new ExecutionException(ExecutionMessageConstants.CONSOLE_CANNOT_START_EXECUTION);    
            }
            return new ConsoleProcess(systemProcess);
        } catch (IOException ex) {
            throw new ExecutionException(ex);
        }
    }

    protected Process executeProcess() throws IOException, ExecutionException {
        try {
            return new LaunchProcessor(ClassPathResolver.getClassPaths(ProjectController.getInstance()
                    .getCurrentProject()), runConfig.getAdditionalEnvironmentVariables(),
                    runConfig.getVmArgs()).execute(getRunConfig()
                    .getExecutionSetting().getScriptFile());
        } catch (ControllerException e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public synchronized void addLogRecords(List<XmlLogRecord> records) {
        super.addLogRecords(records);

        // No need to remember log records in console mode
        clearRecords();
    }

    @Override
    public String getStatusMessage(int consoleWidth) {
        return getDefaultStatusMessage(consoleWidth);
    }
    
    @Override
    protected void postExecutionComplete() {
        super.postExecutionComplete();
        if (getExecutedEntity() instanceof TestSuiteExecutedEntity) {
            try {
                String result = getExecutionResult();
                Trackings.trackExecuteTestSuiteInConsoleMode(!ActivationInfoCollector.isActivated(),
                        runConfig.getName(), result, getEndTime().getTime() - getStartTime().getTime());
            } catch (Exception ignored) {
                System.out.println("ahihi");
            }
        }
    }
    
    protected String getExecutionResult() throws Exception {
        String resultExecution = null;
        if (getResult().getNumFailures() > 0) {
            resultExecution = TestStatusValue.FAILED.toString();
        } else if (getResult().getNumErrors() > 0) {
            resultExecution = TestStatusValue.ERROR.toString();
        } else {
            resultExecution = TestStatusValue.PASSED.toString();
        }
        return resultExecution;
    }

    
    @Override
    protected void onStartExecution() {
    	super.onStartExecution();
    }
}
