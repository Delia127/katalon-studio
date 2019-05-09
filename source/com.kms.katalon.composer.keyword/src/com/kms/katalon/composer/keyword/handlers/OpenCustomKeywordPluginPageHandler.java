package com.kms.katalon.composer.keyword.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.controller.ProjectController;

public class OpenCustomKeywordPluginPageHandler {
    
    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        Program.launch("https://store.katalon.com/");
    }
}
