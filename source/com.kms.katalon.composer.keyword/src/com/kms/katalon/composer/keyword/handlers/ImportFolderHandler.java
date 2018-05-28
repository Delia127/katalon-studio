package com.kms.katalon.composer.keyword.handlers;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class ImportFolderHandler {
    @Inject
    IEventBroker eventBroker;

    private FolderTreeEntity keywordFolderTreeRoot;

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {

            Shell shell = Display.getCurrent().getActiveShell();
            DirectoryDialog directoryDialog = new DirectoryDialog(shell, SWT.SYSTEM_MODAL);
            directoryDialog.setMessage("Select existing keyword folder to import to this keyword folder");
            String selectedFolder = directoryDialog.open();
            
            if (selectedFolder == null) {
                return;
            }
            
            File importedFolder = new File(selectedFolder);
            if (importedFolder != null && importedFolder.exists() && importedFolder.isDirectory()) {
                copyKeywordsDirectory(shell, keywordFolderTreeRoot, importedFolder);
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

    }

    @Inject
    @Optional
    private void catchKeywordFolderTreeEntitiesRoot(
            @UIEventTopic(EventConstants.EXPLORER_RELOAD_INPUT) List<Object> treeEntities) {

        keywordFolderTreeRoot = treeEntities.stream().filter(object -> {
            try {
                Object entityObject;
                entityObject = ((ITreeEntity) object).getObject();
                if (entityObject instanceof FolderEntity) {
                    FolderEntity folder = (FolderEntity) entityObject;
                    if (folder.getFolderType() == FolderType.KEYWORD) {
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                return false;
            }
        }).map(object -> (FolderTreeEntity) object).findAny().orElse(null);
    }

    @Inject
    @Optional
    private void execute(@UIEventTopic(EventConstants.FOLDER_IMPORT) Object eventData) {
        if (!canExecute()) {
            return;
        }
        execute(Display.getCurrent().getActiveShell());
    }

    private void copyKeywordsDirectory(Shell shell, ITreeEntity selectedTreeEntity, File importedFolder)
            throws Exception {

        // source folder
        FolderEntity sourceFolder = FolderController.getInstance().getFolder(importedFolder.getAbsolutePath());
        sourceFolder.setFolderType(FolderType.KEYWORD);
        sourceFolder.setName(importedFolder.getName());
        FolderEntity parentFolder = FolderController.getInstance()
                .getFolder(importedFolder.getParentFile().getAbsolutePath());
        parentFolder.setName(importedFolder.getParentFile().getAbsolutePath());
        sourceFolder.setParentFolder(parentFolder);

        // target folder
        FolderEntity targetFolder = ((FolderTreeEntity) selectedTreeEntity).getObject();

        FolderController.getInstance().copyFolder(sourceFolder, targetFolder);
        ITreeEntity keywordRootFolder = new FolderTreeEntity(
                FolderController.getInstance().getKeywordRoot(ProjectController.getInstance().getCurrentProject()),
                null);

        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, keywordRootFolder);
        eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordRootFolder);
        eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordRootFolder);
        MessageDialog.openInformation(shell, StringConstants.INFO, "Import keyword folder successful !");

    }

}
