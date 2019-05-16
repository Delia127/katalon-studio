package com.kms.katalon.composer.webui.execution.handler;

import java.io.IOException;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.configuration.ChromeRunConfiguration;

public class ChromeExecutionHandler extends AbstractExecutionHandler {

    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException {
        return new ChromeRunConfiguration(projectDir);
    }
}
