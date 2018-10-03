package com.kms.katalon.composer.webservice.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;

public class ImportWsdlHandler {
    @Inject
    IEclipseContext context;

    @CanExecute
    public boolean canExecute() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        return currentProject != null && ProjectType.WEBSERVICE == currentProject.getType();
    }

    @Execute
    public void execute() {
        // TODO: Thanh will implement this
    }

}
