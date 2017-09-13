package com.kms.katalon.execution.console;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;

public class ConsoleOptionBuilder {

    public static String from(RunConfigurationDescription description) throws ExecutionException {
        IRunConfigurationContributor contributor = RunConfigurationCollector.getInstance()
                .getRunContributor(description.getRunConfigurationId());
        if (contributor == null) {
            throw new ExecutionException("Run configuration not found.");
        }
        StringBuilder consoleOptionBuider = new StringBuilder();
        consoleOptionBuider.append(String.format("-%s=\"%s\"", "browserType", contributor.getId()));
        contributor.getConsoleOptions(description).forEach(opt -> {
            consoleOptionBuider.append(String.format(" -%s=\"%s\"", opt.getOption(), String.valueOf(opt.getValue())));
        });
        return consoleOptionBuider.toString();
    }
}
