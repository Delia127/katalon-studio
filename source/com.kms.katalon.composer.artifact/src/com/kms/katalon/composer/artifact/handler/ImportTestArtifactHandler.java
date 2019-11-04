package com.kms.katalon.composer.artifact.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.katalon.platform.api.controller.FolderController;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.FolderEntity;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.ui.TestExplorerActionService;
import com.katalon.platform.api.ui.UISynchronizeService;
import com.kms.katalon.composer.artifact.constant.StringConstants;
import com.kms.katalon.composer.artifact.core.FileCompressionException;
import com.kms.katalon.composer.artifact.core.TestArtifactScriptRefactor;
import com.kms.katalon.composer.artifact.core.util.EntityUtil;
import com.kms.katalon.composer.artifact.core.util.FileUtil;
import com.kms.katalon.composer.artifact.core.util.KeywordUtil;
import com.kms.katalon.composer.artifact.core.util.PlatformUtil;
import com.kms.katalon.composer.artifact.core.util.ProfileUtil;
import com.kms.katalon.composer.artifact.core.util.TestCaseUtil;
import com.kms.katalon.composer.artifact.core.util.ZipUtil;
import com.kms.katalon.composer.artifact.dialog.ImportTestArtifactDialog;
import com.kms.katalon.composer.artifact.dialog.ImportTestArtifactDialog.ImportTestArtifactDialogResult;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class ImportTestArtifactHandler {

    private static final long DIALOG_CLOSED_DELAY_MILLIS = 500L;

    private Shell activeShell;

    public ImportTestArtifactHandler(Shell shell) {
        this.activeShell = shell;
    }

    public void execute() {
        ImportTestArtifactDialog dialog = new ImportTestArtifactDialog(activeShell);
        if (dialog.open() == Window.OK) {
            ImportTestArtifactDialogResult result = dialog.getResult();
            String importFileLocation = result.getImportFileLocation();
            String testCaseImportLocation = result.getTestCaseImportLocation();
            String testObjectImportLocation = result.getTestObjectImportLocation();
            try {
                importTestArtifacts(
                        importFileLocation,
                        testCaseImportLocation,
                        testObjectImportLocation);
            } catch (Exception e) {
                MessageDialog.openError(activeShell, StringConstants.ERROR,
                        StringConstants.MSG_UNABLE_TO_IMPORT_TEST_ARTIFACTS);
                LoggerSingleton.logError(e, StringConstants.MSG_UNABLE_TO_IMPORT_TEST_ARTIFACTS);
            }
        }
    }

    private void importTestArtifacts(
            String importFileLocation, 
            String testCaseImportLocation,
            String testObjectImportLocation) throws IOException, FileCompressionException {
        File importFile = new File(importFileLocation);
        if (!importFile.exists()) {
            MessageDialog.openError(activeShell, StringConstants.ERROR, StringConstants.MSG_INVALID_IMPORT_FILE);
            return;
        }

        if (!testCaseImportLocation.startsWith("Test Cases")) {
            MessageDialog.openError(activeShell, StringConstants.ERROR,
                    StringConstants.MSG_INVALID_TEST_CASE_IMPORT_LOCATION);
            return;
        }

        if (!testObjectImportLocation.startsWith("Object Repository")) {
            MessageDialog.openError(activeShell, StringConstants.ERROR,
                    StringConstants.MSG_INVALID_TEST_OBJECT_IMPORT_LOCATION);
            return;
        }

        Job importArtifactsJob = new Job(StringConstants.MSG_IMPORTING_TEST_ARTIFACTS) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    File tempFolder = Files.createTempDirectory(StringConstants.IMPORT_EXPORT_IMPORT_TEMP_FOLDER).toFile();
                    ZipUtil.extractAll(importFile, tempFolder);

                    if (!FileUtil.isEmptyFolder(tempFolder)) {
                        File sourceFolder = tempFolder.listFiles()[0];
                        if (sourceFolder.isDirectory()) {
                            File testCaseImportFolder = null;
                            File testScriptImportFolder = null;
                            File testObjectImportFolder = null;

                            testCaseImportFolder = importTestCases(sourceFolder, testCaseImportLocation);

                            if (testCaseImportFolder != null) {
                                testScriptImportFolder = importTestScripts(sourceFolder, testCaseImportFolder);
                            }

                            testObjectImportFolder = importTestObjects(sourceFolder, testObjectImportLocation);

                            importProfiles(sourceFolder);
                            
                            List<File> importedKeywordFiles = importKeywords(sourceFolder);

                            if (testObjectImportFolder != null && testScriptImportFolder != null) {
                                Map<String, String> testObjectIdLookup = collectTestObjectIds(testObjectImportFolder);
                                List<File> scriptFiles = FileUtil.listFilesWithExtension(testScriptImportFolder,
                                        "groovy");
                                TestArtifactScriptRefactor refactor = TestArtifactScriptRefactor
                                        .createForTestObjectEntity(testObjectIdLookup);
                                refactor.updateReferences(scriptFiles);
                                if (importedKeywordFiles != null) {
                                    refactor.updateReferences(importedKeywordFiles);
                                }
                            }

                            if (testCaseImportFolder != null && testScriptImportFolder != null) {
                                Map<String, String> testCaseIdLookup = collectTestCaseIds(testCaseImportFolder);
                                List<File> scriptFiles = FileUtil.listFilesWithExtension(testScriptImportFolder,
                                        "groovy");
                                TestArtifactScriptRefactor refactor = TestArtifactScriptRefactor
                                        .createForTestCaseEntity(testCaseIdLookup);
                                refactor.updateReferences(scriptFiles);
                                if (importedKeywordFiles != null) {
                                    refactor.updateReferences(importedKeywordFiles);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e, e.getMessage());
                    return new Status(Status.ERROR, "com.katalon.plugin.katashare", StringConstants.MSG_ERROR_IMPORTING_TEST_ARTIFACTS,
                            e);
                }
                return Status.OK_STATUS;
            }
        };

        importArtifactsJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                if (!importArtifactsJob.getResult().isOK()) {
                    LoggerSingleton.logError("Failed to import test artifacts!");
                    MessageDialog.openError(activeShell, StringConstants.ERROR,
                            StringConstants.MSG_FAILED_TO_IMPORT_TEST_ARTIFACTS);
                }

                Executors.newSingleThreadExecutor().submit(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DIALOG_CLOSED_DELAY_MILLIS);
                    } catch (InterruptedException ignored) {
                    }
                    PlatformUtil.getUIService(UISynchronizeService.class).syncExec(() -> {
                        MessageDialog.openInformation(activeShell, StringConstants.INFO,
                                StringConstants.MSG_TEST_ARTIFACTS_IMPORTED_SUCCESSFULLY);
                    });
                });
            }
        });

        importArtifactsJob.setUser(true);
        importArtifactsJob.schedule();
    }

    private File importTestCases(File sourceFolder, String testCaseImportLocation) throws IOException {
        File sharedTestCaseFolder = new File(sourceFolder, StringConstants.IMPORT_EXPORT_TEST_CASES_FOLDER);
        if (!FileUtil.isEmptyFolder(sharedTestCaseFolder)) {
            ProjectEntity project = PlatformUtil.getCurrentProject();

            String importFolderRelativePath = StringUtils.replace(testCaseImportLocation,
                    EntityUtil.getEntityIdSeparator(), File.separator);
            File importFolder = new File(project.getFolderLocation(), importFolderRelativePath);

            FileUtils.copyDirectory(sharedTestCaseFolder, importFolder);

            return importFolder;
        } else {
            return null;
        }
    }

    private File importTestScripts(File sourceFolder, File testCaseImportFolder) throws IOException, ResourceException {
        File sharedTestScriptFolder = new File(sourceFolder, StringConstants.IMPORT_EXPORT_TEST_SCRIPTS_FOLDER);
        if (!FileUtil.isEmptyFolder(sharedTestScriptFolder)) {
            ProjectEntity project = PlatformUtil.getCurrentProject();

            String importFolderRelativePath = testCaseImportFolder.getAbsolutePath()
                    .substring(TestCaseUtil.getTestCaseRootFolder(project).length());
            String importFolderLocation = TestCaseUtil.getTestScriptRootFolder(project) + importFolderRelativePath;
            Files.createDirectories(Paths.get(importFolderLocation));
            File importFolder = new File(importFolderLocation);

            FileUtils.copyDirectory(sharedTestScriptFolder, importFolder);

            String importFolderId = "Test Cases" + EntityUtil.getEntityIdSeparator()
                    + StringUtils.replace(importFolderRelativePath, File.separator, EntityUtil.getEntityIdSeparator());
            FolderEntity importFolderEntity = PlatformUtil.getPlatformController(FolderController.class)
                    .getFolder(project, importFolderId);
            TestExplorerActionService explorerActionService = PlatformUtil
                    .getUIService(TestExplorerActionService.class);
            explorerActionService.refreshFolder(project, importFolderEntity);

            return importFolder;
        } else {
            return null;
        }
    }

    private File importTestObjects(File sourceFolder, String testObjectImportLocation)
            throws IOException, ResourceException {
        File sharedTestObjectFolder = new File(sourceFolder, StringConstants.IMPORT_EXPORT_TEST_OBJECTS_FOLDER);
        if (!FileUtil.isEmptyFolder(sharedTestObjectFolder)) {
            ProjectEntity project = PlatformUtil.getCurrentProject();

            String importFolderRelativePath = StringUtils.replace(testObjectImportLocation,
                    EntityUtil.getEntityIdSeparator(), File.separator);
            File importFolder = new File(project.getFolderLocation(), importFolderRelativePath);

            FileUtils.copyDirectory(sharedTestObjectFolder, importFolder);

            FolderEntity importFolderEntity = PlatformUtil.getPlatformController(FolderController.class)
                    .getFolder(project, testObjectImportLocation);
            TestExplorerActionService explorerActionService = PlatformUtil
                    .getUIService(TestExplorerActionService.class);
            explorerActionService.refreshFolder(project, importFolderEntity);

            return importFolder;
        } else {
            return null;
        }
    }

    private void importProfiles(File sourceFolder) throws IOException, ResourceException {
        File sharedProfileFolder = new File(sourceFolder, StringConstants.IMPORT_EXPORT_PROFILES_FOLDER);
        if (!FileUtil.isEmptyFolder(sharedProfileFolder)) {
            ProjectEntity project = PlatformUtil.getCurrentProject();

            File profileRootFolder = new File(ProfileUtil.getProfileRootFolder(project));

            FileUtils.copyDirectory(sharedProfileFolder, profileRootFolder);

            FolderEntity importFolderEntity = PlatformUtil.getPlatformController(FolderController.class)
                    .getFolder(project, "Profiles");
            TestExplorerActionService explorerActionService = PlatformUtil
                    .getUIService(TestExplorerActionService.class);
            explorerActionService.refreshFolder(project, importFolderEntity);
        }
    }
    
    private List<File> importKeywords(File sourceFolder) throws Exception {
        File sharedKeywordFolder = new File(sourceFolder, StringConstants.IMPORT_EXPORT_KEYWORDS_FOLDER);
        if (!FileUtil.isEmptyFolder(sharedKeywordFolder)) {
            ProjectEntity project = PlatformUtil.getCurrentProject();

            File keywordRootFolder = new File(KeywordUtil.getKeywordRootFolder(project));

            FileUtils.copyDirectory(sharedKeywordFolder, keywordRootFolder);

            com.kms.katalon.entity.folder.FolderEntity keywordRootFolderEntity =  com.kms.katalon.controller.FolderController.getInstance()
                    .getKeywordRoot(ProjectController.getInstance().getCurrentProject());
            GroovyRefreshUtil.refreshFolder(keywordRootFolderEntity.getRelativePath(), keywordRootFolderEntity.getProject(),
                    new NullProgressMonitor());

            FolderEntity importFolderEntity = PlatformUtil.getPlatformController(FolderController.class)
                    .getFolder(project, "Keywords");
            TestExplorerActionService explorerActionService = PlatformUtil.getUIService(TestExplorerActionService.class);
            explorerActionService.refreshFolder(project, importFolderEntity);

            List<File> keywordFiles = FileUtil.listFilesWithExtension(sharedKeywordFolder, "groovy");
            List<File> copiedKeywordFiles = keywordFiles.stream().map(keywordFile -> {
                String keywordFilePath = keywordFile.getAbsolutePath();
                String keywordFileRelativePath = keywordFilePath
                        .substring((sharedKeywordFolder.getAbsolutePath() + File.separator).length());
                File copiedKeywordFile = new File(keywordRootFolder, keywordFileRelativePath);
                return copiedKeywordFile;
            }).collect(Collectors.toList());
            return copiedKeywordFiles;
        } else {
            return null;
        }
    }

    private Map<String, String> collectTestObjectIds(File testObjectImportFolder) throws IOException {
        ProjectEntity project = PlatformUtil.getCurrentProject();
        Map<String, String> testObjectIdLookup = new HashMap<>();
        Files.walk(Paths.get(testObjectImportFolder.getAbsolutePath())).filter(
                p -> Files.isRegularFile(p) && FilenameUtils.getExtension(p.toFile().getAbsolutePath()).equals("rs"))
                .forEach(p -> {
                    String path = p.toFile().getAbsolutePath();
                    String pathWithoutExtension = FilenameUtils.removeExtension(path);
                    String newRelativeId = pathWithoutExtension
                            .substring((project.getFolderLocation() + File.separator).length());
                    newRelativeId = StringUtils.replace(newRelativeId, File.separator,
                            EntityUtil.getEntityIdSeparator());
                    String oldRelativeId = "Object Repository" + File.separator + pathWithoutExtension
                            .substring((testObjectImportFolder.getAbsolutePath() + File.separator).length());
                    oldRelativeId = StringUtils.replace(oldRelativeId, File.separator,
                            EntityUtil.getEntityIdSeparator());
                    testObjectIdLookup.put(oldRelativeId, newRelativeId);
                });
        return testObjectIdLookup;
    }

    private Map<String, String> collectTestCaseIds(File testCaseImportFolder) throws IOException {
        ProjectEntity project = PlatformUtil.getCurrentProject();
        Map<String, String> testCaseIdLookup = new HashMap<>();
        Files.walk(Paths.get(testCaseImportFolder.getAbsolutePath())).filter(
                p -> Files.isRegularFile(p) && FilenameUtils.getExtension(p.toFile().getAbsolutePath()).equals("tc"))
                .forEach(p -> {
                    String path = p.toFile().getAbsolutePath();
                    String pathWithoutExtension = FilenameUtils.removeExtension(path);
                    String newRelativeId = pathWithoutExtension
                            .substring((project.getFolderLocation() + File.separator).length());
                    newRelativeId = StringUtils.replace(newRelativeId, File.separator,
                            EntityUtil.getEntityIdSeparator());
                    String oldRelativeId = "Test Cases" + File.separator + pathWithoutExtension
                            .substring((testCaseImportFolder.getAbsolutePath() + File.separator).length());
                    oldRelativeId = StringUtils.replace(oldRelativeId, File.separator,
                            EntityUtil.getEntityIdSeparator());
                    testCaseIdLookup.put(oldRelativeId, newRelativeId);
                });
        return testCaseIdLookup;
    }
}
