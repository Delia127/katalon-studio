package com.kms.katalon.composer.execution.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.session.ExecutionSession;
import com.kms.katalon.execution.session.ExecutionSessionSocketServer;

@SuppressWarnings("restriction")
public abstract class AbstractExecutionMenuContribution {

    private static final String EXISTING_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.execution.command.existing"; //$NON-NLS-1$

    private static final int DEFAULT_MAX_TITLE_WIDTH = 20;

    @Inject
    protected ECommandService commandService;

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        List<ExecutionSession> exisingDrivers = ExecutionSessionSocketServer.getInstance()
                .getAllAvailableExecutionSessionByDriverTypeName(getDriverTypeName());
        if (exisingDrivers.isEmpty()) {
            aboutToShowDefault(menuItems);
        } else {
            aboutToShowWithExisting(menuItems, exisingDrivers);
        }
    }

    private void aboutToShowWithExisting(List<MMenuElement> menuItems, List<ExecutionSession> exisingDrivers) {
        MMenu executionMenu = MenuFactory.createPopupMenu(getMenuLabel(), ConstantsHelper.getApplicationURI());
        executionMenu.setIconURI(getIconUri());
        executionMenu.setTooltip(null);

        MHandledMenuItem defaultMenuItem = createDefaultMenuItem();
        defaultMenuItem.setLabel(ComposerExecutionMessageConstants.LBL_EXECUTION_NEW_SESSION);

        List<MMenuElement> executionMenuItems = executionMenu.getChildren();
        executionMenuItems.add(new ExecutionHandledMenuItem(defaultMenuItem));
        executionMenuItems.add(MMenuFactory.INSTANCE.createMenuSeparator());

        for (ExecutionSession executionSession : exisingDrivers) {
            Map<String, Object> parameters = createParametersForExistingSession(executionSession);
            ParameterizedCommand executionCommand = commandService.createCommand(getExistingExecutionCommandId(),
                    parameters);
            String executionTitle = executionSession.getTitle();
            if (executionTitle.isEmpty()) {
                executionTitle = ComposerExecutionMessageConstants.LBL_EXECUTION_EXISTING_SESSION_BLANK_TITLE;
            }
            MHandledMenuItem executionMenuItem = MenuFactory.createPopupMenuItem(executionCommand,
                    StringUtils.abbreviate(executionTitle, DEFAULT_MAX_TITLE_WIDTH),
                    ConstantsHelper.getApplicationURI());
            executionMenuItem.setIconURI(getIconUri());
            executionMenuItem.setTooltip(null);
            executionMenuItems.add(new ExecutionHandledMenuItem(executionMenuItem));
        }
        menuItems.add(executionMenu);
    }

    protected String getExistingExecutionCommandId() {
        return EXISTING_EXECUTION_COMMAND_ID;
    }

    public void aboutToShowDefault(List<MMenuElement> items) {
        items.add(createDefaultMenuItem());
    }

    protected LaunchMode getLaunchMode() {
        return LaunchMode.RUN;
    }

    private Map<String, Object> createParametersForExistingSession(ExecutionSession executionSession) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(IdConstants.EXISTING_SESSION_SESSION_ID_ID, executionSession.getSessionId());
        parameters.put(IdConstants.EXISTING_SESSION_SERVER_URL_ID, executionSession.getRemoteUrl());
        parameters.put(IdConstants.EXISTING_SESSION_DRIVER_NAME_ID, executionSession.getDriverTypeName());
        parameters.put(IdConstants.RUN_MODE_PARAMETER_ID, getLaunchMode().toString());
        return parameters;
    }

    private MHandledMenuItem createDefaultMenuItem() {
        Map<String, Object> parameters = getParametersForCommand();
        ParameterizedCommand executionCommand = commandService.createCommand(getCommandId(), parameters);
        MHandledMenuItem executionMenuItem = MenuFactory.createPopupMenuItem(executionCommand, getMenuLabel(),
                ConstantsHelper.getApplicationURI());
        executionMenuItem.setTooltip(null);
        executionMenuItem.setIconURI(getIconUri());
        return new ExecutionHandledMenuItem(executionMenuItem);
    }

    protected Map<String, Object> getParametersForCommand() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(IdConstants.RUN_MODE_PARAMETER_ID, getLaunchMode().toString());
        return parameters;
    }

    protected abstract String getIconUri();

    protected abstract String getDriverTypeName();

    protected abstract String getCommandId();

    protected abstract String getMenuLabel();
}
