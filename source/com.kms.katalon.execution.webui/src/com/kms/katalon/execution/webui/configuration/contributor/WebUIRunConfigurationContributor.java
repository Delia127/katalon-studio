package com.kms.katalon.execution.webui.configuration.contributor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.exception.ExecutionException;

public abstract class WebUIRunConfigurationContributor implements IRunConfigurationContributor {
    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        return Collections.emptyList();
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        // Do nothing
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir,
            RunConfigurationDescription runConfigurationDescription) throws IOException, ExecutionException,
            InterruptedException {
        return getRunConfiguration(projectDir);
    }
}
