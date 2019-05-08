package com.kms.katalon.composer.webui.execution.handler;

import java.io.IOException;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.webui.configuration.SafariRunConfiguration;

public class SafariExecutionHandler extends AbstractExecutionHandler {
    
    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException {
        return new SafariRunConfiguration(projectDir);
    }

    @Override
    public void execute(LaunchMode launchMode) throws Exception {
        super.execute(launchMode);
    }

}