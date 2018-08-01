package com.kms.katalon.composer.keyword.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.folder.dialogs.NewFolderDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.folder.FolderEntity;

public class NewFolderHandler {
    
    @Inject
    ESelectionService selectionService;
    
    @Inject
    IEventBroker eventBroker;
    
    private FolderTreeEntity getSelectedTreeEntity() {
        Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        if (selectedObjects == null || selectedObjects.length != 1 || !(selectedObjects[0] instanceof FolderTreeEntity)) {
            return null;
        }
        return (FolderTreeEntity) selectedObjects[0];
    }

    @Execute
    public void execute() {
        FolderTreeEntity parentFolderTreeEntity = getSelectedTreeEntity();
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
