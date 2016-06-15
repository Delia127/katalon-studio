package com.kms.katalon.composer.folder.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.folder.constants.StringConstants;
import com.kms.katalon.composer.folder.dialogs.NewFolderDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class NewFolderHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    private ESelectionService selectionService;

    @CanExecute
    public static boolean canExecute() {
        try {
            if (ProjectController.getInstance().getCurrentProject() != null) {
                return true;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            if (selectedObjects != null && selectedObjects.length > 0 && selectedObjects[0] instanceof ITreeEntity) {
                FolderEntity parentFolder = null;
                // The entity which is right-clicked on
                ITreeEntity parentTreeEntity = (ITreeEntity) selectedObjects[0];
                Object selectedEntity = parentTreeEntity.getObject();
                if (selectedEntity != null) {
                    // Not allow creating Folder under Keywords and Reports
                    if (selectedEntity instanceof FolderEntity
                            && !(((FolderEntity) selectedEntity).getFolderType().equals(FolderType.KEYWORD) || ((FolderEntity) selectedEntity)
                                    .getFolderType().equals(FolderType.REPORT))) {
                        parentFolder = (FolderEntity) selectedEntity;
                    } else if (selectedEntity instanceof TestCaseEntity) {
                        parentFolder = ((TestCaseEntity) selectedEntity).getParentFolder();
                        parentTreeEntity = (ITreeEntity) parentTreeEntity.getParent();
                    } else if (selectedEntity instanceof TestSuiteEntity) {
                        parentFolder = ((TestSuiteEntity) selectedEntity).getParentFolder();
                        parentTreeEntity = (ITreeEntity) parentTreeEntity.getParent();
                    } else if (selectedEntity instanceof DataFileEntity) {
                        parentFolder = ((DataFileEntity) selectedEntity).getParentFolder();
                        parentTreeEntity = (ITreeEntity) parentTreeEntity.getParent();
                    } else if (selectedEntity instanceof WebElementEntity) {
                        parentFolder = ((WebElementEntity) selectedEntity).getParentFolder();
                        parentTreeEntity = (ITreeEntity) parentTreeEntity.getParent();
                    } else if (selectedEntity instanceof TestSuiteCollectionEntity) {
                        parentFolder = ((TestSuiteCollectionEntity) selectedEntity).getParentFolder();
                        parentTreeEntity = (ITreeEntity) parentTreeEntity.getParent();
                    }
                }

                if (parentFolder != null) {
                    String newDefaultName = StringConstants.HAND_NEW_FOLDER;

                    String suggestedName = FolderController.getInstance().getAvailableFolderName(parentFolder,
                            newDefaultName);

                    NewFolderDialog dialog = new NewFolderDialog(parentShell, parentFolder);
                    dialog.setName(suggestedName);
                    dialog.open();

                    if (dialog.getReturnCode() == Dialog.OK) {
                        FolderEntity newEntity = FolderController.getInstance().addNewFolder(parentFolder,
                                dialog.getName());

                        if (newEntity != null) {
                            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
                            eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, new FolderTreeEntity(newEntity,
                                    parentTreeEntity));
                        }
                    }
                }
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_FOLDER);
        }
    }
}
