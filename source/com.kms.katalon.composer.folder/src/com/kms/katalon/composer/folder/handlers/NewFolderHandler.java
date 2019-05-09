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
import com.kms.katalon.composer.folder.dialogs.NewRootFolderDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.tracking.service.Trackings;

public class NewFolderHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private ESelectionService selectionService;

    @CanExecute
    public static boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            createNewFolderEntity(parentShell, selectedObjects, eventBroker);
        } catch (FilePathTooLongException e) {
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE, e.getMessage());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_FOLDER);
        }
    }

    public FolderTreeEntity createNewFolderEntity(Shell parentShell, Object[] selectedObjects, IEventBroker eventBroker)
            throws Exception {
        if (!isFirstSelectedObjectValid(selectedObjects)) {
            return newFolderAtRoot(parentShell);
        }
        FolderEntity parentFolder = null;
        // The entity which is right-clicked on
        ITreeEntity parentTreeEntity = (ITreeEntity) selectedObjects[0];
        Object selectedEntity = parentTreeEntity.getObject();
        if (!(selectedEntity instanceof FileEntity)) {
            return null;
        }

        // not a root folder
        if (!parentTreeEntity.hasChildren()) {
            parentFolder = ((FileEntity) selectedEntity).getParentFolder();
            parentTreeEntity = (ITreeEntity) parentTreeEntity.getParent();
            if (parentFolder == null) {
                return newFolderAtRoot(parentShell);
            }
        }

        if (parentFolder == null) {
            parentFolder = (FolderEntity) selectedEntity;
        }

        String newDefaultName = StringConstants.HAND_NEW_FOLDER;

        String suggestedName = FolderController.getInstance().getAvailableFolderName(parentFolder, newDefaultName);

        NewFolderDialog dialog = new NewFolderDialog(parentShell, parentFolder);
        dialog.setName(suggestedName);
        if (dialog.open() != Dialog.OK) {
            return null;
        }

        FolderEntity newEntity = FolderController.getInstance().addNewFolder(parentFolder, dialog.getName());
        if (newEntity == null) {
            return null;
        }

        FolderTreeEntity newFolderTreeEntity = new FolderTreeEntity(newEntity, parentTreeEntity);
        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
        eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, newFolderTreeEntity);
        
        Trackings.trackCreatingObject("folder");
        
        return newFolderTreeEntity;
    }

    private boolean isFirstSelectedObjectValid(Object[] selectedObjects) {
        return selectedObjects != null && selectedObjects.length > 0 && selectedObjects[0] instanceof ITreeEntity;
    }
    
    private FolderTreeEntity newFolderAtRoot(Shell parentShell) throws ControllerException {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        NewRootFolderDialog dialog = new NewRootFolderDialog(parentShell);
        if (dialog.open() == NewRootFolderDialog.OK) {
            String newFolderName = dialog.getNewFolderName();
            FolderEntity newFolder = FolderController.getInstance().addNewRootFolder(project, newFolderName);
            FolderTreeEntity newFolderTreeEntity = new FolderTreeEntity(newFolder, null);
            eventBroker.send(EventConstants.EXPLORER_ADD_AND_SELECT_ITEM, newFolderTreeEntity);
            return newFolderTreeEntity;
        }
        return null;
    }
}
