package com.kms.katalon.composer.project.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.composer.components.impl.util.EntityIndexingUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.ComposerProjectMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;

public class IndexProjectHandler extends AbstractHandler {

    private static final String INDEXING_PROJECT = ComposerProjectMessageConstants.HAND_INDEXING_PROJECT;

    private Job indexingProjectJob;

    @Override
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Override
    public void execute() {
        if (indexingProjectJob == null) {
            indexingProjectJob = new Job(INDEXING_PROJECT) {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        monitor.beginTask(INDEXING_PROJECT, 3);
                        EntityIndexingUtil entityIndexingUtil = EntityIndexingUtil
                                .getInstance(ProjectController.getInstance().getCurrentProject());
                        monitor.worked(1);
                        entityIndexingUtil.doIndex();
                        monitor.worked(2);
                        return Status.OK_STATUS;
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                        return Status.CANCEL_STATUS;
                    } finally {
                        monitor.done();
                    }
                }
            };
            indexingProjectJob.setUser(true);
        }

        if (indexingProjectJob.getState() == Job.RUNNING || indexingProjectJob.getState() == Job.WAITING) {
            return;
        }

        if (indexingProjectJob.getState() == Job.SLEEPING) {
            indexingProjectJob.wakeUp();
            return;
        }

        indexingProjectJob.schedule();
    }

    @Inject
    @Optional
    public void subscribeIndexingEvent(@UIEventTopic(EventConstants.PROJECT_INDEX) Object eventData) {
        execute();
    }

    @Inject
    @Optional
    public void subscribeOpenedProjectEvent(@UIEventTopic(EventConstants.PROJECT_OPENED) Object eventData) {
        execute();
    }

    @Inject
    @Optional
    public void subscribeCreatedItemEvent(@UIEventTopic(EventConstants.EXPLORER_SET_SELECTED_ITEM) Object eventData) {
        execute();
    }

    @Inject
    @Optional
    public void subscribeCopyPastedItemEvent(
            @UIEventTopic(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM) Object eventData) {
        execute();
    }

    @Inject
    @Optional
    public void subscribeCutPastedItemEvent(
            @UIEventTopic(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM) Object eventData) {
        execute();
    }

    @Inject
    @Optional
    public void subscribeDeletedItemEvent(
            @UIEventTopic(EventConstants.EXPLORER_DELETED_SELECTED_ITEM) Object eventData) {
        execute();
    }

    @Inject
    @Optional
    public void subscribeRefreshAllItemEvent(
            @UIEventTopic(EventConstants.EXPLORER_REFRESH_ALL_ITEMS) Object eventData) {
        execute();
    }

}
