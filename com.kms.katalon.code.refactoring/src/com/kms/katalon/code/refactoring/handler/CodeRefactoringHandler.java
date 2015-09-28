package com.kms.katalon.code.refactoring.handler;

import javax.inject.Inject;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.code.refactoring.setting.CodeRefactoringSettingStore;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class CodeRefactoringHandler implements EventHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private UISynchronize sync;

    private boolean userConfirmed;

    @Inject
    public void subscribeEventBrokerListeners() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, this);
        eventBroker.subscribe(EventConstants.PROJECT_CREATED, this);
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case EventConstants.PROJECT_OPENED: {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        while (Job.getJobManager().currentJob() != null) {
                            // wait for "Open project" job complete
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException ex) {
                                //Do nothing
                            }
                        }
                        
                        try {
                            //wait for current progress bar disappear.
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            //Do nothing
                        }
                        
                        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
                        String projectDir = projectEntity.getFolderLocation();
                        boolean isMigrated = CodeRefactoringSettingStore.isMigrated(projectDir);
                        if (!isMigrated) {
                            openConfirmationDialog();
                            if (!userConfirmed) return;
                            CodeRefactoringJob refactoringJob = new CodeRefactoringJob("Code refactoring",
                                    projectEntity);
                            refactoringJob.setUser(true);
                            refactoringJob.schedule();
                        }
                    }
                });
                thread.start();

                break;
            }
            case EventConstants.PROJECT_CREATED: {
                ProjectEntity projectEntity = (ProjectEntity) event
                        .getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                String projectDir = projectEntity.getFolderLocation();
                CodeRefactoringSettingStore.saveMigrated(projectDir);
                break;
            }
            default:
                return;
        }
    }

    private void openConfirmationDialog() {
        userConfirmed = false;
        sync.syncExec(new Runnable() {
            @Override
            public void run() {
                userConfirmed = MessageDialog
                        .openConfirm(
                                null,
                                "Confirmation",
                                "System has detected that your current project's using the old version of Katalon(older than 3.0.1.128).\n"
                                        + "Please note that our libraries prefix now start with com.kms.katalon instead of com.kms.qautomate.\n"
                                        + "To continue working with our version, system needs to modify your script. Do you want to continue?");
            }
        });

    }
}
