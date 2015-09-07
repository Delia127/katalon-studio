package com.kms.katalon.composer.project.menu;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

@SuppressWarnings("restriction")
public class RecentProjectsMenuContribution {
	@Inject
	private ECommandService commandService;

	@Inject EModelService modelService;
	
	@AboutToShow
	public void aboutToShow(List<MMenuElement> menuItems) {
		try {
			for (ProjectEntity project : ProjectController.getInstance().getRecentProjects()) {
				 //Add temp command to avoid warning message
				 MCommand command = MCommandsFactory.INSTANCE.createCommand();
				 command.setCommandName("Temp");
				
				 // Create menu item
				 MHandledMenuItem recentProjectMenuItem =
				 MMenuFactory.INSTANCE.createHandledMenuItem();
				 recentProjectMenuItem.setLabel(project.getName());
				 recentProjectMenuItem.setContributorURI(ConstantsHelper.getApplicationURI());
				 recentProjectMenuItem.setCommand(command);
				
				 // Create parameterized command
				 Command recentProjectCommand =
				 commandService.getCommand(IdConstants.OPEN_RECENT_PROJECT_COMMAND_ID);
				 List<Parameterization> parameterization = new
				 ArrayList<Parameterization>();
				 IParameter param =
				 recentProjectCommand.getParameter(IdConstants.OPEN_RECENT_PROJECT_COMMAND_PARAMETER_ID);
				 Parameterization params = new Parameterization(param,
				 project.getId());
				 parameterization.add(params);
				 ParameterizedCommand parameterizedCommand = new
				 ParameterizedCommand(recentProjectCommand,
				 parameterization.toArray(new
				 Parameterization[parameterization.size()]));
				 recentProjectMenuItem.setWbCommand(parameterizedCommand);
				
				 menuItems.add(recentProjectMenuItem);
			}
		} catch (Exception e) {
			LoggerSingleton.getInstance().getLogger().error(e);
		}

	}
}