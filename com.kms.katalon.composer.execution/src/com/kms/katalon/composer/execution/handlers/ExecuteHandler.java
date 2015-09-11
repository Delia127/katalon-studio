package com.kms.katalon.composer.execution.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.entity.IRunConfiguration;
import com.kms.katalon.execution.util.ExecutionUtil;

@SuppressWarnings("restriction")
public class ExecuteHandler extends AbstractExecutionHandler {
    @Inject
    protected IEclipseContext context;
    
    protected IRunConfiguration getRunConfigurationForExecution(TestCaseEntity testCase) throws Exception {
        return null;
    }

    protected IRunConfiguration getRunConfigurationForExecution(TestSuiteEntity testSuite) throws Exception {
        return null;
    }

    @Override
    public void execute() {
        HandledToolItemImpl toolItem = (HandledToolItemImpl) modelService.find(IdConstants.EXECUTION_TOOL_ITEM_ID, application);
        if (toolItem == null || toolItem.getMenu() == null) {
            return;
        }
        IRunConfigurationContributor defaultRunContributor = ExecutionUtil.getDefaultExecutionConfiguration();
        if (defaultRunContributor == null) {
            return;
        }
        for (MMenuElement menuItem : toolItem.getMenu().getChildren()) {
            if (menuItem instanceof MHandledMenuItem) {
                MHandledMenuItem handledMenuItem = (MHandledMenuItem) menuItem;
                if (handledMenuItem.getLabel().equals(defaultRunContributor.getId())
                        && handledMenuItem.getWbCommand() != null) {
                    handlerService.executeHandler(handledMenuItem.getWbCommand());
                    break;
                }
            }
        }
        toolItem.getMenu();
    }
}
