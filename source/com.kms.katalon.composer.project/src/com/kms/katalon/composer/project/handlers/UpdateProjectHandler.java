package com.kms.katalon.composer.project.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.composer.project.views.NewProjectDialog;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class UpdateProjectHandler {

    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(Shell shell) {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            NewProjectDialog dialog = new NewProjectDialog(shell, projectEntity);
            dialog.open();
            if (dialog.getReturnCode() == Window.OK) {
                boolean isChanged = !dialog.getProjectName().equals(projectEntity.getName())
                        || !dialog.getProjectDescription().equals(projectEntity.getDescription());
                if (isChanged) {
                    ProjectController.getInstance().updateProject(dialog.getProjectName(),
                            dialog.getProjectDescription(), projectEntity.getLocation());
                    OpenProjectHandler.updateProjectTitle(projectEntity, modelService, application);
                }
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            MessageDialog.openError(shell, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_UPDATE_PROJ);
        }
    }
}
