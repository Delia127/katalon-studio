package com.kms.katalon.composer.objectrepository.menu;

import com.kms.katalon.composer.components.impl.menu.AbstractPropertiesMenuContribution;
import com.kms.katalon.composer.objectrepository.handler.EditTestObjectPropertiesHandler;

public class TestObjectPropertiesDynamicMenuContribution extends AbstractPropertiesMenuContribution {

    @Override
    protected boolean canShow() {
        return EditTestObjectPropertiesHandler.canExecute();
    }

    @Override
    protected Class<?> getHandlerClass() {
        return EditTestObjectPropertiesHandler.class;
    }

}
