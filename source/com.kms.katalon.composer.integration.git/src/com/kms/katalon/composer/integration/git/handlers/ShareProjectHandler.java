package com.kms.katalon.composer.integration.git.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.egit.ui.internal.sharing.SharingWizard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.git.constants.GitStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class ShareProjectHandler {

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }
    
    @Execute
    public void execute(Shell shell) {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            IProject groovyProject = GroovyUtil.getGroovyProject(projectEntity);
            final SharingWizard wizard = new SharingWizard();
            wizard.init(PlatformUI.getWorkbench(), groovyProject);
            WizardDialog wizardDialog = new WizardDialog(shell, wizard);
            wizardDialog.setHelpAvailable(false);
            wizardDialog.open();
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            MessageDialog.openError(shell, GitStringConstants.ERROR,
                    GitStringConstants.HAND_ERROR_MSG_UNABLE_TO_SHARE_PROJ);
        }
    }
}
