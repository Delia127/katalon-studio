package com.kms.katalon.composer.execution.handlers;

import java.io.IOException;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.CustomRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;

public class CustomExecutionHandler extends AbstractExecutionHandler {

    private MApplicationElement menuItem;

    @Override
    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException,
            ExecutionException, InterruptedException {
        for (CustomRunConfigurationContributor customRunConfigContributor : RunConfigurationCollector.getInstance()
                .getAllCustomRunConfigurationContributors()) {
            if (menuItem.getElementId().equals(
                    StringConstants.CUSTOM_RUN_CONFIG_ID_PREFIX + customRunConfigContributor.getId())) {
                return customRunConfigContributor.getRunConfiguration(projectDir);
            }
        }
        
        return null;
    }

    @Execute
    public void execute(@Optional MMenuItem menuItem, ParameterizedCommand parameterizedCommand) {
        this.menuItem = menuItem;
        super.execute(parameterizedCommand);
    }
}
