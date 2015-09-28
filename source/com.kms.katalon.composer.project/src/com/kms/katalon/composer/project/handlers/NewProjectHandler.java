package com.kms.katalon.composer.project.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.composer.project.views.NewProjectDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

@SuppressWarnings("restriction")
public class NewProjectHandler {

	@Inject
	private IEventBroker eventBroker;

	@Execute
	public void execute(Shell shell) {
		NewProjectDialog dialog = new NewProjectDialog(shell);
		dialog.open();

		ProjectEntity newProject = null;
		try {
			if (dialog.getReturnCode() == Window.OK) {
				newProject = ProjectController.getInstance().addNewProject(dialog.getProjectName(),
						dialog.getProjectDescription(), dialog.getProjectLocation());
			}
			// Open created project
			if (newProject != null) {
				eventBroker.send(EventConstants.PROJECT_CREATED, newProject);
				eventBroker.post(EventConstants.PROJECT_OPEN, newProject.getId());
				LauncherManager.refresh();
				eventBroker.post(EventConstants.JOB_REFRESH, null);
				eventBroker.post(EventConstants.CONSOLE_LOG_REFRESH, null);				
			}
		} catch (Exception ex) {
			LoggerSingleton.getInstance().getLogger().error(ex);
			MessageDialog.openError(shell, StringConstants.ERROR_TITLE, 
					StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_NEW_PROJ);
		}
	}
}
