 
package com.kms.katalon.composer.webui.execution.handler;

import static org.eclipse.core.runtime.Platform.getOS;

import java.io.IOException;

import org.eclipse.core.runtime.Platform;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.configuration.EdgeChromiumRunConfiguration;

public class EdgeChromiumExecutionHandler extends AbstractExecutionHandler {
    
    @Override
    public boolean canExecute() {
        String os = getOS();
        return (super.canExecute() && (os.equals(Platform.OS_WIN32) || os.equals(Platform.OS_MACOSX)));
    }

    @Override
    protected IRunConfiguration getRunConfigurationForExecution(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        return new EdgeChromiumRunConfiguration(projectDir);
    }

}