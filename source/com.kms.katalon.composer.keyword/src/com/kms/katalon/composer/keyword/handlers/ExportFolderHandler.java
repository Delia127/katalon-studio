package com.kms.katalon.composer.keyword.handlers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import com.kms.katalon.tracking.service.Trackings;

public class ExportFolderHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    private ESelectionService selectionService;

    private final String DOT_DILIMETER = ".";
    
    private final String OPEN_CONTAINING_FOLDER_WINDOW_CMD = "explorer.exe /select,";
    
    private final String OPEN_CONTAINING_FOLDER_MACOS_CMD = "open -R ";
    
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
                Trackings.trackExportKeywords();
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

    private void exportKeywordsDirectory(Shell shell, Object selectedTreeEntity, File selectedExportFolder) throws Exception {
        ITreeEntity keywordRootFolder = new FolderTreeEntity(
                FolderController.getInstance().getKeywordRoot(ProjectController.getInstance().getCurrentProject()),
                null);
        
        // Destination folder
        FolderEntity outputFolder = null;
        FolderEntity parentOutputFolder = null;;
        FolderEntity sourceFolder = null;
        String exportedFolderPath = "";
        boolean isExportPackageLevel = selectedTreeEntity instanceof PackageTreeEntity;
        
        // Source folder
        if (isExportPackageLevel) {
            String packageName = ((PackageTreeEntity) selectedTreeEntity).getPackageName();
            exportedFolderPath = selectedExportFolder.getAbsolutePath() + File.separator + packageName.substring(0,packageName.indexOf(DOT_DILIMETER));
            Path outputFolderPath = createFolderTreeBasePackageName(selectedExportFolder, packageName);
            outputFolder = FolderController.getInstance().getFolder(outputFolderPath.toString());
            outputFolder.setFolderType(FolderType.KEYWORD);
            outputFolder.setName(outputFolderPath.getFileName().toString());
            
            parentOutputFolder = FolderController.getInstance().getFolder(outputFolderPath.getParent().toString());
            parentOutputFolder.setName(outputFolderPath.getParent().toString());
            
            String path = FolderController.getInstance()
                    .getKeywordRoot(ProjectController.getInstance().getCurrentProject())
                    .getLocation() + File.separator + packageName.replace(DOT_DILIMETER, File.separator);

            sourceFolder = FolderController.getInstance().getFolder(path);
            sourceFolder.setFolderType(FolderType.KEYWORD);
            sourceFolder.setName(packageName.substring(packageName.lastIndexOf(DOT_DILIMETER) + 1, packageName.length()));
            sourceFolder.setParentFolder(FolderController.getInstance().getFolder(new File(path).getParent()));

        } else {
            outputFolder = FolderController.getInstance().getFolder(selectedExportFolder.getAbsolutePath());
            outputFolder.setFolderType(FolderType.KEYWORD);
            outputFolder.setName(selectedExportFolder.getName());
            
            exportedFolderPath = outputFolder.getLocation();
            parentOutputFolder = FolderController.getInstance().getFolder(selectedExportFolder.getParentFile().getAbsolutePath());
            parentOutputFolder.setName(selectedExportFolder.getParentFile().getAbsolutePath());
            sourceFolder = (FolderEntity) keywordRootFolder.getObject();
        }
        
        outputFolder.setParentFolder(parentOutputFolder);
        FolderEntity copiedFolder = FolderController.getInstance().copyFolder(sourceFolder, outputFolder);

        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, keywordRootFolder);
        eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordRootFolder);
        eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordRootFolder);
        
        MessageDialog.openInformation(shell, StringConstants.INFO, "Export keyword folder successful !");
        
        String osName = System.getProperty("os.name");
        String finalCommand = "";
        
        String appendOpenExportFolderPath = "";
        if (!isExportPackageLevel) {
            appendOpenExportFolderPath = File.separator + copiedFolder.getName();
        }
        
        if (osName == null || osName.toLowerCase().contains("win")) {
            finalCommand = OPEN_CONTAINING_FOLDER_WINDOW_CMD + exportedFolderPath + appendOpenExportFolderPath ;
        } else {
            finalCommand = OPEN_CONTAINING_FOLDER_MACOS_CMD + exportedFolderPath + appendOpenExportFolderPath;
        }
        
        Runtime.getRuntime().exec(finalCommand);
    }
    
    private Path createFolderTreeBasePackageName(File parentFolder, String packageName) throws Exception {
        //remove the last level folder.
        packageName = packageName.substring(0, packageName.lastIndexOf(DOT_DILIMETER));
        String relativePath = packageName.replace(DOT_DILIMETER, File.separator);
        String absolutePath = parentFolder.getAbsolutePath() + File.separator + relativePath;
        Path path = Paths.get(absolutePath);
        return Files.createDirectories(path);
    }
}
