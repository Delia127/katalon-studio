package com.kms.katalon.execution.configuration.contributor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.CustomRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.exception.ExecutionException;

public class CustomRunConfigurationContributor implements IRunConfigurationContributor {
    private String name;

    public CustomRunConfigurationContributor(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir) throws IOException, ExecutionException {
        return new CustomRunConfiguration(projectDir, name);
    }

    @Override
    public int getPreferredOrder() {
        return -1;
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        return Collections.emptyList();
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        // Do nothing
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptions(RunConfigurationDescription description) {
        return Collections.emptyList();
    }
}
