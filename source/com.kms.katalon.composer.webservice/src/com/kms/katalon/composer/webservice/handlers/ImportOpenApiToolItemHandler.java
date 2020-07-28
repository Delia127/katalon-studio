package com.kms.katalon.composer.webservice.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class ImportOpenApiToolItemHandler {

    @CanExecute
    public boolean canExecute() {
        return (ProjectController.getInstance().getCurrentProject() != null)
                && !LauncherManager.getInstance().isAnyLauncherRunning();
    }

}
