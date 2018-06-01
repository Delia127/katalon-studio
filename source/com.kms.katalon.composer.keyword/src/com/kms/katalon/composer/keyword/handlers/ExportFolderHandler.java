package com.kms.katalon.composer.keyword.handlers;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class ExportFolderHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    private ESelectionService selectionService;

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);

            Shell shell = Display.getCurrent().getActiveShell();
            DirectoryDialog directoryDialog = new DirectoryDialog(shell, SWT.SYSTEM_MODAL);
            directoryDialog.setMessage("Select output folder to export the keyword folder");
            String selectedOutputPath = directoryDialog.open();

            if (selectedOutputPath == null) {
                return;
            }

            File exportedFolder = new File(selectedOutputPath);
            if (exportedFolder != null && exportedFolder.exists() && exportedFolder.isDirectory()) {
                exportKeywordsDirectory(shell, selectedObjects[0], exportedFolder);
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

    }

    @Inject
    @Optional
    private void execute(@UIEventTopic(EventConstants.FOLDER_EXPORT) Object eventData) {
        if (!canExecute()) {
            return;
        }
        execute(Display.getCurrent().getActiveShell());
    }

    private void exportKeywordsDirectory(Shell shell, Object selectedTreeEntity, File exportedFolder) throws Exception {

        // Destination folder
        FolderEntity outputFolder = FolderController.getInstance().getFolder(exportedFolder.getAbsolutePath());
        outputFolder.setFolderType(FolderType.KEYWORD);
        outputFolder.setName(exportedFolder.getName());
        FolderEntity parentFolder = FolderController.getInstance()
                .getFolder(exportedFolder.getParentFile().getAbsolutePath());
        parentFolder.setName(exportedFolder.getParentFile().getAbsolutePath());
        outputFolder.setParentFolder(parentFolder);

        FolderEntity sourceFolder = null;

        // Source folder
        if (selectedTreeEntity instanceof FolderTreeEntity) {
            sourceFolder = ((FolderTreeEntity) selectedTreeEntity).getObject();
        } else if (selectedTreeEntity instanceof PackageTreeEntity) {
            String packageName = ((PackageTreeEntity) selectedTreeEntity).getPackageName();
            String path = FolderController.getInstance()
                    .getKeywordRoot(ProjectController.getInstance().getCurrentProject())
                    .getLocation() + File.separator + packageName;

            sourceFolder = FolderController.getInstance().getFolder(path);
            sourceFolder.setFolderType(FolderType.KEYWORD);
            sourceFolder.setName(packageName);
            sourceFolder.setParentFolder((FolderEntity) ((PackageTreeEntity) selectedTreeEntity).getParent().getObject());
        }

        FolderController.getInstance().copyFolder(sourceFolder, outputFolder);
        ITreeEntity keywordRootFolder = new FolderTreeEntity(
                FolderController.getInstance().getKeywordRoot(ProjectController.getInstance().getCurrentProject()),
                null);

        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, keywordRootFolder);
        eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordRootFolder);
        eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordRootFolder);
        MessageDialog.openInformation(shell, StringConstants.INFO, "Export keyword folder successful !");

    }
}
