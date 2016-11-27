package com.kms.katalon.composer.execution.menu;

import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;

public class ExistingExecutionHandledMenuItem extends ExecutionHandledMenuItem {

    public ExistingExecutionHandledMenuItem(MHandledMenuItem item) {
        super(item);
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
