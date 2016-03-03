package com.kms.katalon.composer.execution.menu;

import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledMenuItemImpl;

@SuppressWarnings("restriction")
public class ExecutionHandledMenuItem extends HandledMenuItemImpl {
    public static final String DEFAULT_LABEL = " (default)";

    private boolean isDefault = false;

    public ExecutionHandledMenuItem(MHandledMenuItem handledMenuItem) {
        // Port the necessary fields from initial handled menu item
        if (handledMenuItem instanceof HandledMenuItemImpl) {
            HandledMenuItemImpl item = (HandledMenuItemImpl) handledMenuItem;
            setLabel(item.getLabel());
            setElementId(item.getElementId());
            setIconURI(item.getIconURI());
            setCommand(item.getCommand());
            setContributorURI(item.getContributorURI());
            setWbCommand(item.getWbCommand());
            setType(item.getType());
            setWidget(item.getWidget());
        }
    }

    @Override
    public String getLabel() {
        if (isDefault()) {
            return super.getLabel() + DEFAULT_LABEL;
        }
        return super.getLabel();
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
