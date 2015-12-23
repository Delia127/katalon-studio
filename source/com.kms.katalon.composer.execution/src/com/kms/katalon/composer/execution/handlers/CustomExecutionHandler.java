package com.kms.katalon.composer.execution.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.CustomRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class CustomExecutionHandler {

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    private CustomRunConfigurationContributor getRunConfigurationContributor(MMenuItem menuItem) {
        if (menuItem != null) {
            for (CustomRunConfigurationContributor customRunConfigContributor : RunConfigurationCollector.getInstance()
                    .getAllCustomRunConfigurationContributors()) {
                if (menuItem.getElementId().equals(
                        StringConstants.CUSTOM_RUN_CONFIG_ID_PREFIX + customRunConfigContributor.getId())) {
                    return customRunConfigContributor;
                }
            }
        }
        return null;
    }

    @Execute
    public void execute(@Optional MMenuItem menuItem) {
        CustomRunConfigurationContributor customRunConfigurationContributor = getRunConfigurationContributor(menuItem);
        Entity entity = AbstractExecutionHandler.getExecutionTarget();
        if (customRunConfigurationContributor != null && (entity != null)) {
            try {
                if (entity instanceof TestCaseEntity) {
                    TestCaseEntity testCase = (TestCaseEntity) entity;
                    AbstractExecutionHandler.executeTestCase(testCase, LaunchMode.RUN,
                            customRunConfigurationContributor.getRunConfiguration(testCase, null));
                } else if (entity instanceof TestSuiteEntity) {
                    TestSuiteEntity testSuite = (TestSuiteEntity) entity;
                    AbstractExecutionHandler.executeTestSuite(testSuite, LaunchMode.RUN,
                            customRunConfigurationContributor.getRunConfiguration(testSuite, null), 0, null);
                }
            } catch (ExecutionException e) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR, e.getMessage());
            } catch (SWTException e) {
                // Ignore it
            } catch (Exception e) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR,
                        "Unable to execute test script. (Root cause: " + e.getMessage() + " )");
                LoggerSingleton.logError(e);
            }
        }
    }
}
