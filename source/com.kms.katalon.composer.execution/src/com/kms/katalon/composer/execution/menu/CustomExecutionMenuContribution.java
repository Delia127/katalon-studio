package com.kms.katalon.composer.execution.menu;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.handlers.CustomExecutionHandler;
import com.kms.katalon.composer.execution.handlers.EmptyHandler;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.CustomRunConfigurationContributor;

public class CustomExecutionMenuContribution {

    private static final String EMPTY_HANDLER_LABEL = "(empty)";
    private static final String KATALON_COMPOSER_EXECUTION_BUNDLE_ID = "bundleclass://com.kms.katalon.composer.execution/";
    private static final String CUSTOM_EXECUTION_HANDLER_URI = KATALON_COMPOSER_EXECUTION_BUNDLE_ID
            + CustomExecutionHandler.class.getName();
    private static final String EMPTY_HANDLER_URI = KATALON_COMPOSER_EXECUTION_BUNDLE_ID + EmptyHandler.class.getName();

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            boolean isAdded = false;
            for (CustomRunConfigurationContributor customRunConfigContributor : RunConfigurationCollector.getInstance()
                    .getAllCustomRunConfigurationContributors()) {
                MDirectMenuItem customExecutionMenuItem = (MDirectMenuItem) MMenuFactory.INSTANCE
                        .createDirectMenuItem();
                customExecutionMenuItem.setLabel(customRunConfigContributor.getId());
                customExecutionMenuItem.setElementId(StringConstants.CUSTOM_RUN_CONFIG_ID_PREFIX
                        + customRunConfigContributor.getId());
                customExecutionMenuItem.setContributionURI(CUSTOM_EXECUTION_HANDLER_URI);
                if (customExecutionMenuItem != null) {
                    menuItems.add(customExecutionMenuItem);
                    isAdded = true;
                }
            }
            if (!isAdded) {
                MDirectMenuItem emptyMenuItem = (MDirectMenuItem) MMenuFactory.INSTANCE.createDirectMenuItem();
                emptyMenuItem.setLabel(EMPTY_HANDLER_LABEL);
                emptyMenuItem.setElementId(StringConstants.CUSTOM_RUN_CONFIG_ID_PREFIX + EMPTY_HANDLER_LABEL);
                emptyMenuItem.setContributionURI(EMPTY_HANDLER_URI);
                menuItems.add(emptyMenuItem);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
