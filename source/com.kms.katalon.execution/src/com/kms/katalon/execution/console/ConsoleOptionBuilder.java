package com.kms.katalon.execution.console;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.exception.ExecutionException;

public class ConsoleOptionBuilder {
    public static Map<String, String> argsMap(RunConfigurationDescription description) throws ExecutionException {
        IRunConfigurationContributor contributor = RunConfigurationCollector.getInstance()
                .getRunContributor(description.getRunConfigurationId());
        if (contributor == null) {
            throw new ExecutionException(
                    MessageFormat.format(ExecutionMessageConstants.CONSOLE_RUN_CONFIGURATION_NOT_FOUND,
                            description.getRunConfigurationId()));
        }
        Map<String, String> argsMap = new HashMap<>();
        argsMap.put("browserType", contributor.getId());
        contributor.getConsoleOptions(description).forEach(opt -> {
            if (opt.hasArgument()) {
                argsMap.put(opt.getOption(), String.valueOf(opt.getValue()));
            }
        });
        argsMap.put("executionProfile", description.getProfileName());
        return argsMap;
    }
}
