package com.kms.katalon.composer.keyword.handlers;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.dialogs.DuplicatedImportDialog;
import com.kms.katalon.composer.keyword.logging.UpdatedKeywordObject;
import com.kms.katalon.composer.keyword.logging.UpdatedKeywordObject.ACTION_TYPE;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;

public class ImportFolderHandler {
    @Inject
    IEventBroker eventBroker;

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
                copyFilesToKeywordsDirectory(shell, importedFolder, false);
            }

            ITreeEntity keywordRootFolder = new FolderTreeEntity(FolderController.getInstance()
                    .getKeywordRoot(ProjectController.getInstance().getCurrentProject()), null);
            
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, keywordRootFolder);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordRootFolder);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, keywordRootFolder);
            MessageDialog.openInformation(shell, StringConstants.INFO, "Imported successfully !");

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

    }

    @Inject
    @Optional
    private void execute(@UIEventTopic(EventConstants.FOLDER_IMPORT) Object eventData) {
        if (!canExecute()) {
            return;
        }
        execute(Display.getCurrent().getActiveShell());
    }

    public void copyFilesToKeywordsDirectory(Shell shell, File importedFolder, boolean forGit)
            throws Exception {

        FolderEntity keywordRootFolder = FolderController.getInstance()
                .getKeywordRoot(ProjectController.getInstance().getCurrentProject());
        List<File> groovyFiles = listAllGroovyFiles(importedFolder.getAbsolutePath());
        List<UpdatedKeywordObject> modifiedFiles = new ArrayList<UpdatedKeywordObject>();

        int decisionAppliedToAll = -1;
        for (File groovyFile : groovyFiles) {
            StringBuilder destinationKeywordFolder = new StringBuilder(keywordRootFolder.getLocation());
            destinationKeywordFolder.append(File.separator);
            String absolutePath = groovyFile.getAbsolutePath();
            if (forGit) {
                destinationKeywordFolder.append(absolutePath.substring(
                        absolutePath.indexOf(importedFolder.getName()) + importedFolder.getName().length() + 1,
                        absolutePath.length()));
            } else {
                destinationKeywordFolder.append(
                        absolutePath.substring(absolutePath.indexOf(importedFolder.getName()) + importedFolder.getName().length(), absolutePath.length()));
            }
            if (new File(destinationKeywordFolder.toString()).exists()) {
                // Open popup with the file name
                DuplicatedImportDialog duplicatedDialog = new DuplicatedImportDialog(shell, groovyFile.getName());
                int decision = decisionAppliedToAll;
                if (decisionAppliedToAll == -1) {
                    decision = duplicatedDialog.open();
                    if (duplicatedDialog.isApplyToAll()) {
                        decisionAppliedToAll = decision;
                    }
                }

                if (decision == IDialogConstants.OK_ID) {
                    FileUtils.copyFile(groovyFile, new File(destinationKeywordFolder.toString()));
                    modifiedFiles.add(new UpdatedKeywordObject(absolutePath, ACTION_TYPE.OVERWRITE));
                }

                if (decision == DuplicatedImportDialog.KEEP_BOTH_ID) {
                    destinationKeywordFolder.replace(destinationKeywordFolder.indexOf(groovyFile.getName()),
                            destinationKeywordFolder.length(),
                            findAvailableName(new File(destinationKeywordFolder.toString()), 0));

                    FileUtils.copyFile(groovyFile, new File(destinationKeywordFolder.toString()));
                    modifiedFiles.add(new UpdatedKeywordObject(absolutePath, ACTION_TYPE.CREATE_DUPLICATE));
                }

                // Skip - contines with the other file.
                if (decision == IDialogConstants.SKIP_ID) {
                    modifiedFiles.add(new UpdatedKeywordObject(absolutePath, ACTION_TYPE.SKIP_KEEP_OLD_FILE));
                    continue;
                }
            }

            FileUtils.copyFile(groovyFile, new File(destinationKeywordFolder.toString()));
        }
        if (forGit) {
            writeActionToFile(modifiedFiles);
        }
    }

    private List<File> listAllGroovyFiles(String path) {
        List<File> absolutePathGroovyFiles = new ArrayList<File>();
        Deque<File> directories = new ArrayDeque<File>();
        directories.push(new File(path));

        while (!directories.isEmpty()) {
            File dir = directories.pop();
            if (Objects.nonNull(dir)) {
                File[] files = dir.listFiles();

                for (File file : files) {
                    if (file.isDirectory()) {
                        directories.push(file);
                    }
                    if (file.isFile() && file.getName().endsWith(".groovy")) {
                        absolutePathGroovyFiles.add(file);
                    }
                }
            }
        }
        return absolutePathGroovyFiles;
    }

    private String findAvailableName(File file, int timesOfDuplicate) {
        if (!file.exists()) {
            return file.getName();
        }

        timesOfDuplicate++;
        String testName = FilenameUtils.removeExtension(FilenameUtils.removeExtension(file.getName())) + "_"
                + timesOfDuplicate;

        StringBuilder path = new StringBuilder(file.getAbsolutePath());
        path.replace(path.indexOf(file.getName()), path.length(),
                testName + "." + FilenameUtils.getExtension(file.getName()));

        return findAvailableName(new File(path.toString()), timesOfDuplicate);
    }

    private void writeActionToFile(List<UpdatedKeywordObject> updatedFiles) {
        try {
            String internalSettingPath = ProjectController.getInstance().getCurrentProject().getFolderLocation() + File.separator + ProjectController.getInstance().getInternalSettingDir();
            File file = new File(internalSettingPath, "modified_keyword_log.properties");
            if (!file.exists()) {
                file.createNewFile();
            }
            StringBuilder parseText = new StringBuilder();
            for (UpdatedKeywordObject updatedKeywordObject : updatedFiles) {
                parseText.append(updatedKeywordObject.buildSingleRow());
                
            }
            FileUtils.writeStringToFile(file, parseText.toString());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
