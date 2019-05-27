package com.kms.katalon.composer.explorer.handlers;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.UserFileTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.dialogs.NewTextFileDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.UserFileController;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;

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
            boolean isRoot = parentTreeFolder == null;
            if (isRoot) {
                newFileAtRoot(parentShell, ProjectController.getInstance().getCurrentProject());
            } else {
                newFileInFolder(parentShell, parentTreeFolder);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, "Unable to create file", e.getMessage());
        }
    }
    
    private void newFileAtRoot(Shell parentShell, ProjectEntity project) throws Exception {
        List<String> siblingFileNames = FolderController.getInstance().getRootFileOrFolderNames(project);
        NewTextFileDialog dialog = new NewTextFileDialog(parentShell, siblingFileNames);
        if (dialog.open() == NewTextFileDialog.OK) {
            String newFileName = dialog.getNewFileName();
            UserFileEntity newFile = UserFileController.getInstance().newRootFile(newFileName, project);
            openFile(newFile);
            eventBroker.post(EventConstants.EXPLORER_ADD_AND_SELECT_ITEM, new UserFileTreeEntity(newFile, null));
        }
    }
    
    private void newFileInFolder(Shell parentShell, FolderTreeEntity parentTreeFolder) throws Exception {
        FolderEntity parentFolder = parentTreeFolder.getObject();
        List<String> siblingFileNames =  UserFileController.getInstance().getChildren(parentFolder)
                .stream()
                .map(f -> f.toFile().getName())
                .collect(Collectors.toList());
        NewTextFileDialog dialog = new NewTextFileDialog(parentShell, siblingFileNames);
        if (dialog.open() == NewTextFileDialog.OK) {
            String newFileName = dialog.getNewFileName();
            UserFileEntity newFile = UserFileController.getInstance().newFile(newFileName, parentFolder);
            openFile(newFile);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeFolder);
            eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, new UserFileTreeEntity(newFile, parentTreeFolder));
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
                ITreeEntity treeEntity = (ITreeEntity) selectedObjects[0];
                ITreeEntity parent = treeEntity.getParent();
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
