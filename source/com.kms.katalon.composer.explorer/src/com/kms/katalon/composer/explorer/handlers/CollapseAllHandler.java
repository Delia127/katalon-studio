package com.kms.katalon.composer.explorer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;

public class CollapseAllHandler {

    @Inject
    private IEventBroker eventBroker;
    
    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }
    
    @Execute
    public void execute() {
        eventBroker.post(EventConstants.EXPLORER_COLLAPSE_ALL_ITEMS, null);
    }
}
