package com.kms.katalon.composer.testsuite.collection.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.composer.testsuite.collection.dialog.NewTestSuiteCollectionDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.tracking.service.Trackings;

public class NewTestSuiteCollectionHandler extends TestSuiteTreeRootCatcher {
    @Inject
    private ESelectionService selectionService;

    @Inject
    private IEventBroker eventBroker;

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        FolderTreeEntity parentTreeFolder = getParentTestRunTreeFolder(selectionService, true);
        if (parentTreeFolder == null) {
            return;
        }

        try {
            FolderEntity parentFolder = parentTreeFolder.getObject();

            String suggestedName = EntityNameController.getInstance().getAvailableName(
                    StringConstants.HDL_NEW_TEST_SUITE_COLLECTION_NAME, parentFolder, false);
            NewTestSuiteCollectionDialog dialog = new NewTestSuiteCollectionDialog(parentShell, parentFolder,
                    suggestedName);

            if (dialog.open() != Dialog.OK) {
                return;
            }
            TestSuiteCollectionEntity testRunEntity = dialog.getEntity();
            if (testRunEntity == null) {
                return;
            }
            TestSuiteCollectionTreeEntity newTreeEntity = new TestSuiteCollectionTreeEntity(testRunEntity,
                    parentTreeFolder);
            
            Trackings.trackCreatingObject("testSuiteCollection");
            
            eventBroker.send(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, parentTreeFolder);
            eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, newTreeEntity);
            eventBroker.post(EventConstants.TEST_SUITE_COLLECTION_OPEN, testRunEntity.getIdForDisplay());

        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.HDL_MSG_UNABLE_TO_CREATE_TEST_SUITE_COLLECTION,
                    e.getMessage());
        }
    }

    @Inject
    @Optional
    private void execute(@UIEventTopic(EventConstants.TEST_SUITE_COLLECTION_NEW) Object eventData) {
        if (!canExecute()) {
            return;
        }
        execute(Display.getCurrent().getActiveShell());
    }

}
