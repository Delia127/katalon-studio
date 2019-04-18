package com.kms.katalon.composer.explorer.handlers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.UserFileTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.dialogs.NewTextFileDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.UserFileController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class NewTextFileHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private ESelectionService selectionService;

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            FolderTreeEntity parentTreeFolder = getParentTreeFolder();
            if (parentTreeFolder == null) {
                return;
            }
            FolderEntity parentFolder = parentTreeFolder.getObject();
            List<FileEntity> currentFiles = UserFileController.getInstance().getChildren(parentFolder);
            NewTextFileDialog dialog = new NewTextFileDialog(parentShell, currentFiles);
            if (dialog.open() == NewTextFileDialog.OK) {
                String newFileName = dialog.getNewFileName();
                UserFileEntity fileEntity = UserFileController.getInstance().newFile(newFileName, parentFolder);
                openFile(fileEntity);
                
                eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeFolder);
                eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM,
                        new UserFileTreeEntity(fileEntity, parentTreeFolder));
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, "Unable to create file", e.getMessage());
        }
    }

    private FolderTreeEntity getParentTreeFolder() {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            if (selectedObjects == null || selectedObjects.length != 1
                    || !(selectedObjects[0] instanceof ITreeEntity)) {
                return null;
            }

            if (selectedObjects[0] instanceof FolderTreeEntity) {
                FolderTreeEntity parentFolder = (FolderTreeEntity) selectedObjects[0];
                return parentFolder.getObject().getFolderType() == FolderType.USER ? parentFolder : null;
            } else {
                ITreeEntity parent = (ITreeEntity) selectedObjects[0];
                if (!(parent instanceof FolderTreeEntity)) {
                    return null;
                }
                FolderTreeEntity parentFolder = (FolderTreeEntity) parent;
                return parentFolder.getObject().getFolderType() == FolderType.USER ? parentFolder : null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    private void openFile(UserFileEntity file) throws PartInitException, CoreException {
        OpenUserFileHandler.openEditor(file);
    }
}
