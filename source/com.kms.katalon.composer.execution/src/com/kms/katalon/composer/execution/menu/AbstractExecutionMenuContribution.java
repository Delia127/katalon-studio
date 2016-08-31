package com.kms.katalon.composer.execution.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.execution.launcher.model.LaunchMode;

@SuppressWarnings("restriction")
public abstract class AbstractExecutionMenuContribution {
    @Inject
    protected ECommandService commandService;

    @AboutToShow
    public void aboutToShow(List<MMenuElement> items) {
        Map<String, Object> parameters = getParametersForCommand();
        ParameterizedCommand executionCommand = commandService.createCommand(getCommandId(), parameters);
        MHandledMenuItem executionMenuItem = MenuFactory.createPopupMenuItem(executionCommand, getMenuLabel(),
                ConstantsHelper.getApplicationURI());
        executionMenuItem.setIconURI(getIconUri());
        items.add(executionMenuItem);
    }

    protected Map<String, Object> getParametersForCommand() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(IdConstants.RUN_MODE_PARAMETER_ID, LaunchMode.RUN.toString());
        return parameters;
    }

    protected abstract String getIconUri();

    protected abstract String getMenuLabel();

    protected abstract String getCommandId();
}
