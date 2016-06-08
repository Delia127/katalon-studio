package com.kms.katalon.composer.testdata.menu;

import com.kms.katalon.composer.components.impl.menu.AbstractPropertiesMenuContribution;
import com.kms.katalon.composer.testdata.handlers.EditTestDataPropertiesHandler;

public class TestDataPropertiesDynamicMenuContribution extends AbstractPropertiesMenuContribution {

    @Override
    protected boolean canShow() {
        return EditTestDataPropertiesHandler.getInstance().canExecute();
    }

    @Override
    protected Class<?> getHandlerClass() {
        return EditTestDataPropertiesHandler.class;
    }

}
