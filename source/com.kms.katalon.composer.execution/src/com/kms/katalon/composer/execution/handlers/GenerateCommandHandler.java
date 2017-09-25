package com.kms.katalon.composer.execution.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.execution.dialog.GenerateCommandDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;

public class GenerateCommandHandler {

    private ProjectController pController = ProjectController.getInstance();

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void registerEvent() {
        eventBroker.subscribe(EventConstants.KATALON_GENERATE_COMMAND, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                execute();
            }
        });
    }

    @CanExecute
    public boolean canExecute() {
        return isProjectOpened();
    }

    @Execute
    public void execute() {
        if (!isProjectOpened()) {
            return;
        }
        Shell shell = Display.getCurrent().getActiveShell();
        final GenerateCommandDialog dialog = new GenerateCommandDialog(shell,
                pController.getCurrentProject());
        dialog.open();
    }

    private boolean isProjectOpened() {
        return pController.getCurrentProject() != null;
    }
}
