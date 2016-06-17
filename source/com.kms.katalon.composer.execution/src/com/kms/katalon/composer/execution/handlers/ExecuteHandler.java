package com.kms.katalon.composer.execution.handlers;

import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.execution.menu.ExecutionHandledMenuItem;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.util.ExecutionUtil;

@SuppressWarnings("restriction")
public class ExecuteHandler extends AbstractExecutionHandler {
    
    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException {
        return null;
    }

    @Execute
    public void execute(MHandledToolItem toolItem) {
        IRunConfigurationContributor defaultRunContributor = ExecutionUtil.getDefaultExecutionConfiguration();
        if (defaultRunContributor == null) {
            return;
        }
        for (MMenuElement menuItem : toolItem.getMenu().getChildren()) {
            if (!(menuItem instanceof ExecutionHandledMenuItem)) {
                continue;
            }
            
            ExecutionHandledMenuItem handledMenuItem = (ExecutionHandledMenuItem) menuItem;
            if (handledMenuItem.getLabel().contains(defaultRunContributor.getId())
                    && handledMenuItem.getCommand() != null) {
                handlerService.executeHandler(handledMenuItem.getParameterizedCommandFromMenuItem(commandService));
                return;
            }
        }
    }
}


