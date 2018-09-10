package com.kms.katalon.composer.keyword.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.folder.dialogs.NewFolderDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class NewFolderHandler {
    
    @Inject
    ESelectionService selectionService;
    
    @Inject
    IEventBroker eventBroker;
    
    protected FolderTreeEntity getSelectedTreeEntity(Object[] selectedObjects ) {
        if (selectedObjects == null || selectedObjects.length != 1 || !(selectedObjects[0] instanceof ITreeEntity)) {
            return null;
        }

        if (selectedObjects[0] instanceof FolderTreeEntity) {
            FolderTreeEntity parentFolder = (FolderTreeEntity) selectedObjects[0];
            return isIncludeFolder(parentFolder) ? parentFolder : null; 
        } else {
            ITreeEntity treeEntity = (ITreeEntity) selectedObjects[0];
            try {
                ITreeEntity parent = treeEntity.getParent();
                if (!(parent instanceof FolderTreeEntity)) {
                    return null;
                }
                FolderTreeEntity parentFolder = (FolderTreeEntity) parent;
                return isIncludeFolder(parentFolder) ? parentFolder : null; 
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                return null;
            }
        }
    }
    
    private boolean isIncludeFolder(FolderTreeEntity folderTree) {
        try {
            return folderTree.getObject().getFolderType() == FolderType.INCLUDE;
        } catch (Exception e) {
           return false;
        }
    }

    @Execute
    public void execute() {
        FolderTreeEntity parentFolderTreeEntity = getSelectedTreeEntity(
                (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID));
        FolderEntity parentFolder;
        try {
            parentFolder = parentFolderTreeEntity.getObject();

            String suggestedName = FolderController.getInstance().getAvailableFolderName(parentFolder,
                    "New Folder");

            NewFolderDialog newFolderDialog = new NewFolderDialog(Display.getCurrent().getActiveShell(), parentFolder);
            newFolderDialog.setName(suggestedName);
            newFolderDialog.open();

            if (newFolderDialog.getReturnCode() == Dialog.OK) {
                FolderEntity newEntity = FolderController.getInstance().addNewFolder(parentFolder,
                        newFolderDialog.getName());
                FolderTreeEntity newFolderTree = new FolderTreeEntity(newEntity, parentFolderTreeEntity);
                eventBroker.send(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, parentFolderTreeEntity);
                eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, newFolderTree);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
