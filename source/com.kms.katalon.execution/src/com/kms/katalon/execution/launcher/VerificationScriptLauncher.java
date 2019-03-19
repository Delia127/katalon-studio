package com.kms.katalon.execution.launcher;

import java.io.IOException;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.process.ILaunchProcess;
import com.kms.katalon.execution.launcher.process.LaunchProcessor;
import com.kms.katalon.execution.launcher.process.VerificationProcess;

public class VerificationScriptLauncher extends ConsoleLauncher {
    
    private Runnable processFinishedRunnable;
    
    private String testObjectId;
    
    public VerificationScriptLauncher(String testObjectId,
            LauncherManager manager, 
            IRunConfiguration runConfig, 
            Runnable processFinishedRunnable) {
        
        super(manager, runConfig);
        this.testObjectId = testObjectId;
        this.processFinishedRunnable = processFinishedRunnable;
    }

    @Override
    protected ILaunchProcess launch() throws ExecutionException {
        try {
            Process systemProcess = executeProcess();
            if (systemProcess == null) {
                throw new ExecutionException(ExecutionMessageConstants.CONSOLE_CANNOT_START_EXECUTION);
            }
            
            Thread thread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        systemProcess.waitFor();
                    } catch (InterruptedException e) {
                       
                    } finally {
                        processFinishedRunnable.run();
                    }
                }
            });
            thread.start();
            return onCreateLaunchProcess(systemProcess);
        } catch (IOException ex) {
            throw new ExecutionException(ex);
        }
    }

    protected ILaunchProcess onCreateLaunchProcess(Process systemProcess) {
        return new VerificationProcess(testObjectId, systemProcess);
    }
    
    @Override
    protected Process executeProcess() throws IOException, ExecutionException {
        try {
            return new LaunchProcessor(ClassPathResolver.getClassPaths(ProjectController.getInstance()
                    .getCurrentProject()), runConfig.getAdditionalEnvironmentVariables()).execute(getRunConfig()
                    .getExecutionSetting().getScriptFile());
        }  catch (ControllerException e) {
            throw new ExecutionException(e);
        }
    }
}
