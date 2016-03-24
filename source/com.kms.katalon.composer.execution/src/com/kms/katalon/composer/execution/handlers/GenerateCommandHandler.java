package com.kms.katalon.composer.execution.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.execution.dialog.GenerateCommandDialog;
import com.kms.katalon.controller.ProjectController;

public class GenerateCommandHandler {

    private ProjectController pController = ProjectController.getInstance();

    @CanExecute
    public boolean canExecute() {
        return isProjectOpened();
    }

    @Execute
    public void execute() {
        if (!isProjectOpened()) {
            return;
        }
        GenerateCommandDialog dialog = new GenerateCommandDialog(Display.getCurrent().getActiveShell(),
                pController.getCurrentProject());
        dialog.open();
    }

    private boolean isProjectOpened() {
        return pController.getCurrentProject() != null;
    }
}
