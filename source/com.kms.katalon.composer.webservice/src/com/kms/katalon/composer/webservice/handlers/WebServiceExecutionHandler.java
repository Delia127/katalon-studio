package com.kms.katalon.composer.webservice.handlers;

import java.io.IOException;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.execution.configuration.BasicRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webservice.configuration.WebServiceRunConfiguration;

public class WebServiceExecutionHandler extends AbstractExecutionHandler {

    @Override
    protected IRunConfiguration getRunConfigurationForExecution(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        return new WebServiceRunConfiguration();
    }

}
