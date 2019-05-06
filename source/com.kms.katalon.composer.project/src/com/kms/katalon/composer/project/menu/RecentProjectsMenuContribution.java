package com.kms.katalon.composer.project.menu;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuSeparator;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.application.preference.ProjectSettingPreference;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class RecentProjectsMenuContribution implements EventHandler {

    @Inject
    private IEventBroker eventBroker;

    private static List<ProjectEntity> recentProjects = new ArrayList<ProjectEntity>();

    @PostConstruct
    public void init() {
        eventBroker.subscribe(EventConstants.WORKSPACE_CREATED, this);
        eventBroker.subscribe(EventConstants.PROJECT_CREATED, this);
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, this);
        eventBroker.subscribe(EventConstants.PROJECT_UPDATED, this);
    }

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            // Add a separator at top of items
            menuItems.add(newMenuSeparator());
            
            ProjectParameterizedCommandBuilder commandBuilder = new ProjectParameterizedCommandBuilder();
            for (ProjectEntity project : recentProjects) {
                // Add temp command to avoid warning message
                MCommand command = MCommandsFactory.INSTANCE.createCommand();
                command.setCommandName("Temp");

                String labelName = project.getName() + "\t" + getLocationStringLabel(project.getFolderLocation());

                // Create menu item
                MHandledMenuItem recentProjectMenuItem = MMenuFactory.INSTANCE.createHandledMenuItem();
                recentProjectMenuItem.setLabel(labelName);
                recentProjectMenuItem.setContributorURI(ConstantsHelper.getApplicationURI());
                recentProjectMenuItem.setCommand(command);
                recentProjectMenuItem.setTooltip(StringUtils.EMPTY);

                // Create parameterized command
                recentProjectMenuItem.setWbCommand(commandBuilder.createRecentProjectParameterizedCommand(project));

                menuItems.add(recentProjectMenuItem);
            }

            if (!recentProjects.isEmpty()) {
                // Add a separator under these items
                menuItems.add(newMenuSeparator());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private MMenuSeparator newMenuSeparator() {
        return MMenuFactory.INSTANCE.createMenuSeparator();
    }

    private String getLocationStringLabel(String location) {
        if (location.length() > 60) {
            return location.substring(0, 60) + "...";
        } else {
            return location;
        }
    }

    @Override
    public void handleEvent(Event event) {
        try {
            recentProjects.clear();
            ProjectSettingPreference projectPreferences = new ProjectSettingPreference();
            recentProjects.addAll(projectPreferences.getRecentProjects());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
