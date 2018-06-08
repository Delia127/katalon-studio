package com.kms.katalon.execution.launcher;

import java.io.IOException;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;

import com.google.common.base.Function;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.listener.LauncherEvent;
import com.kms.katalon.execution.launcher.listener.LauncherListener;
import com.kms.katalon.execution.launcher.listener.LauncherNotifiedObject;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.process.ILaunchProcess;
import com.kms.katalon.execution.launcher.process.LaunchProcessor;
import com.kms.katalon.execution.launcher.process.VerificationProcess;

import net.bytebuddy.asm.Advice.This;

public class VerificationScriptLauncher extends ConsoleLauncher {
    
    private Runnable processFinishedRunnable;
    
    public VerificationScriptLauncher(LauncherManager manager, 
            IRunConfiguration runConfig, 
            Runnable processFinishedRunnable) {
        
        super(manager, runConfig);
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
            
            return new VerificationProcess(systemProcess);
        } catch (IOException ex) {
            throw new ExecutionException(ex);
        }
    }
    
    @Override
    protected Process executeProcess() throws IOException, ExecutionException {
        return new LaunchProcessor(ClassPathResolver.getClassPaths(ProjectController.getInstance()
                .getCurrentProject()), runConfig.getAdditionalEnvironmentVariables()).execute(getRunConfig()
                .getExecutionSetting().getScriptFile());
    }
}
