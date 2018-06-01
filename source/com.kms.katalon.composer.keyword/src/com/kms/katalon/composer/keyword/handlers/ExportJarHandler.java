package com.kms.katalon.composer.keyword.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;

public class ExportJarHandler {
    @Inject
    IEventBroker eventBroker;

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            //TODO
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

    }

//    @Inject
//    @Optional
//    private void execute(@UIEventTopic(EventConstants.FOLDER_EXPORT) Object eventData) {
//        if (!canExecute()) {
//            return;
//        }
//    }

}
