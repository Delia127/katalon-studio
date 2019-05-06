package com.kms.katalon.composer.project.menu;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.kms.katalon.composer.project.constants.ImageConstants;
import com.kms.katalon.composer.project.sample.SampleLocalProject;
import com.kms.katalon.composer.project.sample.SampleProjectType;
import com.kms.katalon.composer.project.sample.SampleRemoteProject;
import com.kms.katalon.composer.project.sample.SampleRemoteProjectProvider;
import com.kms.katalon.composer.project.template.SampleProjectProvider;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class SampleProjectMenuContribution implements EventHandler {

    private static List<ProjectEntity> recentProjects = new ArrayList<ProjectEntity>();

    private static final List<String> SAMPLE_LOCAL_PROJECTS;

    static {
        SAMPLE_LOCAL_PROJECTS = new ArrayList<>();
        SAMPLE_LOCAL_PROJECTS.add(SampleProjectProvider.SAMPLE_WEB_UI);
        SAMPLE_LOCAL_PROJECTS.add(SampleProjectProvider.SAMPLE_MOBILE);
        SAMPLE_LOCAL_PROJECTS.add(SampleProjectProvider.SAMPLE_WEB_SERVICE);
    }

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            // Add a separator at top of items
            menuItems.add(newMenuSeparator());

            List<SampleRemoteProject> remoteProjects = SampleRemoteProjectProvider.getCachedProjects();
           
            ProjectParameterizedCommandBuilder commandBuilder = new ProjectParameterizedCommandBuilder();
            if (remoteProjects.size() <= 0) {
                List<SampleLocalProject> localProjects = SampleProjectProvider.getInstance().getSampleProjects();
                for (SampleLocalProject project : localProjects) {
                    // Create menu item
                    MHandledMenuItem newLocalProjectMenuItem = MMenuFactory.INSTANCE.createHandledMenuItem();
                    newLocalProjectMenuItem.setLabel(project.getName());
                    newLocalProjectMenuItem.setContributorURI(ConstantsHelper.getApplicationURI());
                    newLocalProjectMenuItem.setCommand(newTempCommand());
                    newLocalProjectMenuItem.setTooltip(StringUtils.EMPTY);
                    newLocalProjectMenuItem.setIconURI(getIconURIForProject(project.getType()));

                    // Create parameterized command
                    newLocalProjectMenuItem
                            .setWbCommand(commandBuilder.createSampleLocalProjectParameterizedCommand(project));

                    menuItems.add(newLocalProjectMenuItem);
                }
            } else {
                for (SampleRemoteProject project : remoteProjects) {
                    // Create menu item
                    MHandledMenuItem remoteProjectMenuItem = MMenuFactory.INSTANCE.createHandledMenuItem();
                    remoteProjectMenuItem.setLabel(project.getName());
                    remoteProjectMenuItem.setContributorURI(ConstantsHelper.getApplicationURI());
                    remoteProjectMenuItem.setCommand(newTempCommand());
                    remoteProjectMenuItem.setTooltip(StringUtils.EMPTY);
                    remoteProjectMenuItem.setIconURI(getIconURIForProject(project.getType()));

                    // Create parameterized command
                    remoteProjectMenuItem.setWbCommand(commandBuilder.createRemoteProjectParameterizedCommand(project));

                    menuItems.add(remoteProjectMenuItem);
                }
            }

            if (!recentProjects.isEmpty()) {
                // Add a separator under these items
                menuItems.add(newMenuSeparator());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private MCommand newTempCommand() {
        MCommand command = MCommandsFactory.INSTANCE.createCommand();
        //Avoid warning message
        command.setCommandName("Temp");
        return command;
    }
    
    public String getIconURIForProject(SampleProjectType projectType) {
        switch (projectType) {
            case MOBILE:
                return ImageConstants.URL_SAMPLE_MOBILE_16.toString();
            case WS:
                return ImageConstants.URL_SAMPLE_WS_16.toString();
            default:
                return ImageConstants.URL_SAMPLE_WEB_16.toString();
        }
    }

    private MMenuSeparator newMenuSeparator() {
        return MMenuFactory.INSTANCE.createMenuSeparator();
    }

    @Override
    public void handleEvent(Event event) {
        try {
            recentProjects.clear();
            recentProjects = new ProjectSettingPreference().getRecentProjects();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
