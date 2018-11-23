package com.kms.katalon.execution.webservice.contribution;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.BasicRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.exception.ExecutionException;

public class WebServiceConfigurationContributor implements IRunConfigurationContributor {

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        return Collections.emptyList();
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        // Do nothing   
    }

    @Override
    public String getId() {
        return "API/Web Service";
    }

    @Override
    public int getPreferredOrder() {
        return -1;
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        return new BasicRunConfiguration();
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptions(RunConfigurationDescription description) {
        return Collections.emptyList();
    }

}