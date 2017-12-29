package com.kms.katalon.composer.execution.menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledMenuItemImpl;
import org.eclipse.emf.common.util.EList;

import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

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

    public boolean isDefault() {
//        ExecutionDefaultSettingStore store = ExecutionDefaultSettingStore.getStore();
//        if (store == null) {
//            return false;
//        }
//        String defaultItemLabel = store.getExecutionConfiguration();
//        String defaultItemLabel = getPreferenceStore().getString("command");
//        return getDefaultLabel().equals(defaultItemLabel);
        String commandName = getPreferenceStore().getString("command");
        if (StringUtils.isBlank(commandName)) {
            commandName = "com.kms.katalon.composer.webui.execution.command.chrome";
        }
//        System.out.println("command name: " + getCommand().getCommandName());
        return getCommand().getElementId().equals(commandName);
    }

    @Override
    public String getTooltip() {
        return "";
    }

    public ParameterizedCommand getParameterizedCommandFromMenuItem(ECommandService commandService) {
        ParameterizedCommand parameterizedCommand = getWbCommand();
        if (parameterizedCommand != null) {
            return parameterizedCommand;
        }
        Map<String, Object> parameters = new HashMap<>();
        for (MParameter param : getParameters()) {
            parameters.put(param.getName(), param.getValue());
        }
        return commandService.createCommand(getCommand().getElementId(), parameters);
    }
    
    private ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore("execution_browser");
    }
}
