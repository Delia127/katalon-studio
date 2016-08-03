package com.kms.katalon.composer.execution.handlers;

import java.io.IOException;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.CustomRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class CustomExecutionHandler extends AbstractExecutionHandler {

    private String menuItemId;

    @CanExecute
    public boolean canExecute() {
        return super.canExecute();
    }

    @Override
    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException,
            ExecutionException, InterruptedException {
        for (CustomRunConfigurationContributor customRunConfigContributor : RunConfigurationCollector.getInstance()
                .getAllCustomRunConfigurationContributors()) {
            if (ObjectUtils.equals(menuItemId,
                    StringConstants.CUSTOM_RUN_CONFIG_ID_PREFIX + customRunConfigContributor.getId())) {
                return customRunConfigContributor.getRunConfiguration(projectDir);
            }
        }

        return null;
    }

    @Execute
    public void execute(MMenuItem menuItem) {
        try {
            this.menuItemId = menuItem.getElementId();
            super.execute(getLaunchModeByMenuId(menuItem.getParent()));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private LaunchMode getLaunchModeByMenuId(MUIElement parentMenuItem) {
        String parentElementId = parentMenuItem.getElementId();
        if (StringConstants.CUSTOM_RUN_MENU_ID.equals(parentElementId)) {
            return LaunchMode.RUN;
        }

        return LaunchMode.DEBUG;
    }
}
