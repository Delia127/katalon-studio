package com.kms.katalon.composer.keyword.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.keyword.git.CustomGitCloneWizard;
import com.kms.katalon.controller.ProjectController;

public class ImportGitHandler {
    @Inject
    IEventBroker eventBroker;
    
    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) throws Exception {
        WizardDialog dlg = new WizardDialog(parentShell, new CustomGitCloneWizard());
        dlg.open();
    }

}
