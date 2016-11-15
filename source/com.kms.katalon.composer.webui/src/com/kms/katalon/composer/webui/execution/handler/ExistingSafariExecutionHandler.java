package com.kms.katalon.composer.webui.execution.handler;

import java.io.IOException;

import org.eclipse.core.commands.ParameterizedCommand;

import com.kms.katalon.composer.execution.handlers.ExistingExecutionHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.execution.configuration.ExistingSafariRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;

public class ExistingSafariExecutionHandler extends ExistingExecutionHandler {
    private String port;
    
    @Override
    protected IRunConfiguration getRunConfigurationForExecution(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        return new ExistingSafariRunConfiguration(projectDir);
    }

    @Override
    public void execute(ParameterizedCommand command) {
        port = getPort(command);
        super.execute(command);
    }
    
    protected String getPort(ParameterizedCommand command) {
        return getParameter(command, IdConstants.EXISTING_SESSION_DRIVER_PORT_ID);
    }
    
    @Override
    protected void prepareData(IRunConfiguration runConfig) {
        super.prepareData(runConfig);
        ExistingSafariRunConfiguration safariRunConfig = (ExistingSafariRunConfiguration) runConfig;
        safariRunConfig.setPort(port);
    }
}
