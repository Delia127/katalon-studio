package com.kms.katalon.composer.webui.execution.handler;

import static org.eclipse.core.runtime.Platform.getOS;

import java.io.IOException;

import org.eclipse.core.runtime.Platform;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.configuration.EdgeRunConfiguration;

public class EdgeExecutionHandler extends AbstractExecutionHandler {

    @Override
    public boolean canExecute() {
        return (super.canExecute() && getOS().equals(Platform.OS_WIN32));
    }
    
	protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException {
		return new EdgeRunConfiguration(projectDir);
	}
}