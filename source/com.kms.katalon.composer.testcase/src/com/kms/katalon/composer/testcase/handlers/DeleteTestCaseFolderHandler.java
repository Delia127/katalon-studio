package com.kms.katalon.composer.testcase.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.jobs.DeleteTestCaseFolderJob;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.folder.FolderEntity;

public class DeleteTestCaseFolderHandler {
    @Inject
    private IEventBroker eventBroker;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell activeShell;

    @Inject
    private UISynchronize sync;

    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_DELETE_TEST_CASE_FOLDER, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                // Do nothing
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof FolderTreeEntity) {
                    excute((FolderTreeEntity) object);
                }
            }
        });
    }

    protected void excute(FolderTreeEntity treeFolder) {
        FolderEntity folder;
        try {
            folder = (FolderEntity) treeFolder.getObject();
            if (folder == null) {
                return;
            }
            DeleteTestCaseFolderJob job = new DeleteTestCaseFolderJob(treeFolder, sync, activeShell);
            job.setUser(true);
            job.schedule();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, "Unable to delete folder.");
        }
    }
}
