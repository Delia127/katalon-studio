package com.kms.katalon.composer.execution.menu;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledMenuItemImpl;
import org.eclipse.emf.common.util.EList;

import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;

@SuppressWarnings("restriction")
public class ExecutionHandledMenuItem extends HandledMenuItemImpl {
    public static final String DEFAULT_LABEL = " (default)";

    public ExecutionHandledMenuItem(MHandledMenuItem item) {
        // Port the necessary fields from initial handled menu item
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

    @Override
    public String getLabel() {
        String defaultLabel = getDefaultLabel();
        return isDefault() ? defaultLabel + DEFAULT_LABEL : defaultLabel;
    }

    private String getDefaultLabel() {
        return label == null ? "" : label;
    }

    protected boolean isDefault() {
        String defaultItemLabel = getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER)
                .getString(ExecutionPreferenceConstants.EXECUTION_DEFAULT_CONFIGURATION);
        return getDefaultLabel().equals(defaultItemLabel);
    }

    @Override
    public String getTooltip() {
        return getLabel();
    }

    public ParameterizedCommand getParameterizedCommandFromMenuItem(ECommandService commandService) {
        Map<String, Object> parameters = new HashMap<>();
        for (MParameter param : getParameters()) {
            parameters.put(param.getName(), param.getValue());
        }
        return commandService.createCommand(getCommand().getElementId(), parameters);
    }
}
