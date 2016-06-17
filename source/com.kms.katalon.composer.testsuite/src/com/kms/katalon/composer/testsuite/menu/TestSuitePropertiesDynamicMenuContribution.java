package com.kms.katalon.composer.testsuite.menu;

import com.kms.katalon.composer.components.impl.menu.AbstractPropertiesMenuContribution;
import com.kms.katalon.composer.testsuite.handlers.EditTestSuitePropertiesHandler;

public class TestSuitePropertiesDynamicMenuContribution extends AbstractPropertiesMenuContribution {

    @Override
    protected boolean canShow() {
        return EditTestSuitePropertiesHandler.getInstance().canExecute();
    }

    @Override
    protected Class<?> getHandlerClass() {
        return EditTestSuitePropertiesHandler.class;
    }

}
