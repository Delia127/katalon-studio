package com.kms.katalon.composer.project.handlers;

import java.io.FileNotFoundException;
import javax.xml.bind.MarshalException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.composer.project.dialog.NewProjectDialog;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

@SuppressWarnings("restriction")
public class NewProjectHandler {
	public static final String TEMPL_CUSTOM_KW_PKG_REL_PATH = "Keywords/com/example";

	@Execute
	public void execute(Shell shell) {
		NewProjectDialog dialog = new NewProjectDialog(shell);
		dialog.open();
	}

	public static ProjectEntity createNewProject(String projectName, String projectLocation, String projectDescription)
			throws Exception {
		try {
			ProjectEntity newProject = ProjectController.getInstance().addNewProject(projectName, projectDescription,
					projectLocation);
			// EntityTrackingHelper.trackProjectCreated();
			return newProject;
		} catch (MarshalException ex) {
			if (!(ex.getLinkedException() instanceof FileNotFoundException)) {
				throw ex;
			}
			LoggerSingleton.getInstance().getLogger().error(ex);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
					StringConstants.HAND_ERROR_MSG_NEW_PROJ_LOCATION_INVALID);
		}
		return null;
	}
}
