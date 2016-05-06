package com.kms.katalon.composer.execution.menu;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledMenuItemImpl;
import org.eclipse.emf.common.util.EList;

@SuppressWarnings("restriction")
public class ExecutionHandledMenuItem extends HandledMenuItemImpl {
    private static final String DEFAULT_LABEL = " (default)";

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
            parameters = (EList<MParameter>) item.getParameters();
        }
    }

    @Override
    public String getLabel() {
        String defaultLabel = super.getLabel();
        return isDefault() ? defaultLabel + DEFAULT_LABEL : defaultLabel;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public ParameterizedCommand getParameterizedCommandFromMenuItem(ECommandService commandService) {
        Map<String, Object> parameters = new HashMap<>();
        for (MParameter param : getParameters()) {
            parameters.put(param.getName(), param.getValue());
        }
        return commandService.createCommand(getCommand().getElementId(), parameters);
    }
}
