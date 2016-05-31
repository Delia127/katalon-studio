package com.kms.katalon.composer.testcase.contribution;

import com.kms.katalon.composer.components.impl.menu.AbstractPropertiesMenuContribution;
import com.kms.katalon.composer.testcase.handlers.EditTestCasePropertiesHandler;

public class TestCasePropertiesDynamicMenuContribution extends AbstractPropertiesMenuContribution {

    @Override
    protected boolean canShow() {
        return EditTestCasePropertiesHandler.getInstance().canExecute();
    }

    @Override
    protected Class<?> getHandlerClass() {
        return EditTestCasePropertiesHandler.class;
    }

}
