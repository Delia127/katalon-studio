package com.kms.katalon.composer.execution.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
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
        try {
            new ProgressMonitorDialog(shell).run(true, false,
                    new IRunnableWithProgress() {

                        @Override
                        public void run(IProgressMonitor monitor) throws InvocationTargetException,
                                InterruptedException {
                            try {
                                monitor.beginTask(ComposerExecutionMessageConstants.HAND_OPEN_GENERATE_COMMAND_DIALOG, 3);
                                dialog.initListMobileDevices();
                                monitor.worked(2);
                                monitor.subTask(ComposerExecutionMessageConstants.HAND_CREATING_DIALOG);
                                TimeUnit.SECONDS.sleep(1);
                                monitor.worked(1);
                                monitor.subTask(ComposerExecutionMessageConstants.HAND_OPEN_DIALOG);
                            } catch (Exception e) {
                                LoggerSingleton.logError(e);
                            } finally {
                                monitor.done();
                            }
                        }
                    });
        } catch (InvocationTargetException | InterruptedException e) {
            LoggerSingleton.logError(e);
        }
        dialog.open();
    }

    private boolean isProjectOpened() {
        return pController.getCurrentProject() != null;
    }
}
