package com.kms.katalon.composer.keyword.handlers;

import java.io.File;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.kms.katalon.composer.keyword.logging.ChangeFile;
import com.kms.katalon.composer.keyword.logging.ChangeFile.FileStatus;
import com.kms.katalon.composer.keyword.logging.GitKeywordObject;
import com.kms.katalon.composer.keyword.logging.GitKeywordObject.ActionType;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.util.FileHashUtil;

public class ImportFolderHandler {

    private final String TIMESTAMP_FORMAT = "hh:mm:ss - dd/MM/yyyy";

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
                copyFilesToKeywordsDirectory(shell, importedFolder, StringUtils.EMPTY, StringUtils.EMPTY);
            }

            ITreeEntity keywordRootFolder = new FolderTreeEntity(
                    FolderController.getInstance().getKeywordRoot(ProjectController.getInstance().getCurrentProject()),
                    null);

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

    public void copyFilesToKeywordsDirectory(Shell shell, File importedFolder, String commitId, String repoUrl)
            throws Exception {

        FolderEntity keywordRootFolder = FolderController.getInstance()
                .getKeywordRoot(ProjectController.getInstance().getCurrentProject());
        List<File> groovyFiles = listAllGroovyFiles(importedFolder.getAbsolutePath());

        int decisionAppliedToAll = -1;

        DateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
        GitKeywordObject keywordObject = new GitKeywordObject(repoUrl, commitId,
                dateFormat.format(System.currentTimeMillis()));
        keywordObject.setType(ActionType.KEYWORD_IMPORT);

        for (File groovyFile : groovyFiles) {
            StringBuilder destinationKeywordFolder = new StringBuilder(keywordRootFolder.getLocation());
            destinationKeywordFolder.append(File.separator);
            String absolutePath = groovyFile.getAbsolutePath();
            String relativePath = PathUtil.absoluteToRelativePath(absolutePath,
                    ProjectController.getInstance().getCurrentProject().getFolderLocation());
            if (StringUtils.isNotEmpty(repoUrl)) {
                destinationKeywordFolder.append(absolutePath.substring(
                        absolutePath.indexOf(importedFolder.getName()) + importedFolder.getName().length() + 1,
                        absolutePath.length()));
            } else {
                destinationKeywordFolder.append(
                        absolutePath.substring(absolutePath.indexOf(importedFolder.getName()), absolutePath.length()));
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

                FileStatus fileStatus = FileStatus.SKIP_KEEP_OLD_FILE;
                if (decision == IDialogConstants.OK_ID) {
                    fileStatus = FileStatus.OVERWRITE;
                    FileUtils.copyFile(groovyFile, new File(destinationKeywordFolder.toString()));
                }

                if (decision == DuplicatedImportDialog.KEEP_BOTH_ID) {
                    fileStatus = FileStatus.CREATE_DUPLICATE;
                    destinationKeywordFolder.replace(destinationKeywordFolder.indexOf(groovyFile.getName()),
                            destinationKeywordFolder.length(),
                            findAvailableName(new File(destinationKeywordFolder.toString()), 0));
                    FileUtils.copyFile(groovyFile, new File(destinationKeywordFolder.toString()));
                }
                
                keywordObject.addChangeFile(
                        new ChangeFile(relativePath, FileHashUtil.hash(absolutePath, "MD5"), fileStatus));

                if (decision == IDialogConstants.SKIP_ID) {
                    fileStatus = FileStatus.SKIP_KEEP_OLD_FILE;
                    continue;
                }
                
            } else {
                keywordObject.addChangeFile(
                        new ChangeFile(relativePath, FileHashUtil.hash(absolutePath, "MD5"), FileStatus.NEW));
            }

            FileUtils.copyFile(groovyFile, new File(destinationKeywordFolder.toString()));
        }
        if (StringUtils.isNotEmpty(repoUrl)) {
            writeActionToFile(keywordObject);
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

    private void writeActionToFile(GitKeywordObject updatedObj) {
        try {
            String internalSettingPath = ProjectController.getInstance().getCurrentProject().getFolderLocation()
                    + File.separator + ProjectController.getInstance().getInternalSettingDir();
            File file = new File(internalSettingPath, "extraLib.properties");
            String output = "";

            if (file.exists()) {
                String currentContent = new String(Files.readAllBytes(file.toPath()));
                List<GitKeywordObject> currentContentLst = new ArrayList<>(
                        Arrays.asList(JsonUtil.fromJson(currentContent, GitKeywordObject[].class)));
                Iterator<GitKeywordObject> currentObjIterator = currentContentLst.iterator();
                boolean foundCurrentFile = false;

                while (currentObjIterator.hasNext()) {
                    GitKeywordObject obj = currentObjIterator.next();
                    if (Objects.equals(obj.getGitUrl(), updatedObj.getGitUrl())) {
                        obj.setListChangeFiles(updatedObj.getChangeFiles());
                        obj.setType(updatedObj.getType());
                        obj.setCommitId(updatedObj.getCommitId());
                        obj.setTimeStamp(updatedObj.getTimestamp());
                        foundCurrentFile = true;
                    }
                }
                // append to last json list.
                if (!foundCurrentFile) {
                    currentContentLst.add(updatedObj);
                }
                output = JsonUtil.toJson(currentContentLst);

            } else {
                file.createNewFile();
                GitKeywordObject[] firstGitObj = { updatedObj };
                output = JsonUtil.toJson(firstGitObj);
            }

            FileUtils.writeStringToFile(file, output);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
