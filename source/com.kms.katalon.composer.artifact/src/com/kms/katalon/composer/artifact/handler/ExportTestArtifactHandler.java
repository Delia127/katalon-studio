package com.kms.katalon.composer.artifact.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.katalon.platform.api.model.ExecutionProfileEntity;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.platform.api.model.TestObjectEntity;
import com.katalon.platform.api.ui.UISynchronizeService;
import com.kms.katalon.composer.artifact.constant.StringConstants;
import com.kms.katalon.composer.artifact.core.FileCompressionException;
import com.kms.katalon.composer.artifact.core.util.KeywordUtil;
import com.kms.katalon.composer.artifact.core.util.PlatformUtil;
import com.kms.katalon.composer.artifact.core.util.TestCaseUtil;
import com.kms.katalon.composer.artifact.core.util.TestObjectUtil;
import com.kms.katalon.composer.artifact.core.util.ZipUtil;
import com.kms.katalon.composer.artifact.dialog.ExportTestArtifactDialog;
import com.kms.katalon.composer.artifact.dialog.ExportTestArtifactDialog.ExportTestArtifactDialogResult;
import com.kms.katalon.composer.components.log.LoggerSingleton;

public class ExportTestArtifactHandler {
    
    private static final long DIALOG_CLOSED_DELAY_MILLIS = 500L;

    private Shell activeShell;

    public ExportTestArtifactHandler(Shell shell) {
        this.activeShell = shell;
    }

    public void execute() {
        ExportTestArtifactDialog dialog = new ExportTestArtifactDialog(activeShell);
        if (dialog.open() == Window.OK) {
            ExportTestArtifactDialogResult result = dialog.getResult();
            List<TestCaseEntity> exportedTestCases = result.getSelectedTestCases();
            List<TestObjectEntity> exportedTestObjects = result.getSelectedTestObjects();
            List<ExecutionProfileEntity> exportedProfiles = result.getSelectedProfiles();
            List<File> exportedKeywords = result.getSelectedKeywords();
            String exportLocation = result.getExportLocation();
            try {
                exportTestArtifacts(
                        exportedTestCases,
                        exportedTestObjects,
                        exportedProfiles,
                        exportedKeywords,
                        exportLocation);
            } catch (Exception e) {
                MessageDialog.openError(activeShell, StringConstants.ERROR,
                        StringConstants.MSG_UNABLE_TO_EXPORT_TEST_ARTIFACTS);
                LoggerSingleton.logError(e, StringConstants.MSG_UNABLE_TO_EXPORT_TEST_ARTIFACTS);
            }
        }
    }

    private void exportTestArtifacts(
            List<TestCaseEntity> testCases, 
            List<TestObjectEntity> testObjects,
            List<ExecutionProfileEntity> profiles,
            List<File> keywords,
            String exportLocation) throws IOException, FileCompressionException {

        File exportFolder = new File(exportLocation);
        if (!exportFolder.exists() || !exportFolder.isDirectory()) {
            MessageDialog.openError(activeShell, StringConstants.ERROR, StringConstants.MSG_INVALID_EXPORT_LOCATION);
            return;
        }

        Job exportArtifactsJob = new Job(StringConstants.MSG_EXPORTING_TEST_ARTIFACTS) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    File exportTempFolder = Files.createTempDirectory(StringConstants.IMPORT_EXPORT_EXPORT_TEMP_FOLDER).toFile();

                    SubMonitor subMonitor = SubMonitor.convert(monitor);
                    subMonitor.beginTask("", 100);
                    
                    int numberOfTestScripts = testCases.size();
                    
                    int totalWork = testCases.size() 
                            + numberOfTestScripts 
                            + testObjects.size() 
                            + profiles.size() 
                            + keywords.size();
                    
                    int exportTestCaseWork = Math.round((float) testCases.size() * 90 / totalWork);
                    SubMonitor exportTestCaseMonitor = subMonitor.split(exportTestCaseWork, SubMonitor.SUPPRESS_NONE);
                    exportTestCaseMonitor.beginTask(StringConstants.MSG_EXPORTING_TEST_CASES, 100);
                    
                    exportTestCases(testCases, exportTempFolder, exportTestCaseMonitor);
                    
                    exportTestCaseMonitor.done();

                    int exportTestScriptWork = Math.round((float) testCases.size() * 90 / totalWork);
                    SubMonitor exportTestScriptMonitor = subMonitor.split(exportTestScriptWork, SubMonitor.SUPPRESS_NONE);
                    exportTestScriptMonitor.beginTask(StringConstants.MSG_EXPORTING_TEST_SCRIPTS, 100);
                    
                    exportTestScripts(testCases, exportTempFolder, exportTestScriptMonitor);
                    
                    exportTestScriptMonitor.done();

                    int exportTestObjectWork = Math.round((float) testObjects.size() * 90 / totalWork);
                    SubMonitor exportTestObjectMonitor = subMonitor.split(exportTestObjectWork, SubMonitor.SUPPRESS_NONE);
                    exportTestObjectMonitor.beginTask(StringConstants.MSG_EXPORTING_TEST_OBJECTS, 100);
                    
                    exportTestObjects(testObjects, exportTempFolder, exportTestObjectMonitor);
                    
                    exportTestObjectMonitor.done();
                    
                    int exportProfileWork = Math.round((float) profiles.size() * 90 / totalWork);
                    SubMonitor exportProfileMonitor = subMonitor.split(exportProfileWork, SubMonitor.SUPPRESS_NONE);
                    exportProfileMonitor.beginTask(StringConstants.MSG_EXPORTING_PROFILES, 100);
                    
                    exportProfiles(profiles, exportTempFolder, exportProfileMonitor);
                    
                    exportProfileMonitor.done();
                    
                    int exportKeywordWork = Math.round((float) keywords.size() * 90 / totalWork);
                    SubMonitor exportKeywordMonitor = subMonitor.split(exportKeywordWork, SubMonitor.SUPPRESS_NONE);
                    exportKeywordMonitor.beginTask(StringConstants.MSG_EXPORTING_KEYWORDS, 100);
                    
                    exportKeywords(keywords, exportTempFolder, exportKeywordMonitor);
                    
                    exportKeywordMonitor.done();

                    SubMonitor zipMonitor = subMonitor.split(10, SubMonitor.SUPPRESS_NONE);
                    zipMonitor.beginTask(StringConstants.MSG_COMPRESSING_FILES,  100);
                    
                    String exportedPackageName = MessageFormat.format(StringConstants.IMPORT_EXPORT_EXPORT_FILE_NAME, System.currentTimeMillis());
                    File result = new File(exportFolder, exportedPackageName);
                    result.createNewFile();
                    ZipUtil.getZipFile(result, exportTempFolder);
                    
                    zipMonitor.done();
                    
                    FileUtils.forceDelete(exportTempFolder);
                    
                    subMonitor.done();
                } catch (Exception e) {
                    LoggerSingleton.logError(e, e.getMessage());
                    return new Status(Status.ERROR, "com.katalon.plugin.katashare", StringConstants.MSG_ERROR_EXPORTING_TEST_ARTIFACTS,
                            e);
                }
                return Status.OK_STATUS;
            }
        };

        exportArtifactsJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                if (!exportArtifactsJob.getResult().isOK()) {
                    LoggerSingleton.logError("Failed to export test artifacts");
                    MessageDialog.openError(activeShell, StringConstants.ERROR,
                            StringConstants.MSG_FAILED_TO_EXPORT_TEST_ARTIFACTS);
                }

                Executors.newSingleThreadExecutor().submit(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DIALOG_CLOSED_DELAY_MILLIS);
                    } catch (InterruptedException ignored) {
                    }
                    PlatformUtil.getUIService(UISynchronizeService.class).syncExec(() -> {
                        MessageDialog.openInformation(activeShell, StringConstants.INFO,
                                StringConstants.MSG_TEST_ARTIFACTS_EXPORTED_SUCCESSFULLY);
                    });
                });
            }
        });

        exportArtifactsJob.setUser(true);
        exportArtifactsJob.schedule();
    }

    private void exportTestCases(List<TestCaseEntity> testCases, File exportFolder, SubMonitor monitor) throws IOException {
        ProjectEntity project = PlatformUtil.getCurrentProject();

        File sharedTestCaseFolder = new File(exportFolder, StringConstants.IMPORT_EXPORT_TEST_CASES_FOLDER);
        sharedTestCaseFolder.mkdirs();

        int progress = 0;
        for (TestCaseEntity testCase : testCases) {
            String parentRelativePath = TestCaseUtil.getTestCaseParentRelativePath(project, testCase);

            String copyToFolderLocation = sharedTestCaseFolder.getAbsolutePath() + File.separator + parentRelativePath;
            Files.createDirectories(Paths.get(copyToFolderLocation));
            File copyToFolder = new File(copyToFolderLocation);

            File testCaseFile = new File(testCase.getFileLocation());
            if (testCaseFile.exists()) {
                FileUtils.copyFileToDirectory(testCaseFile, copyToFolder);
            }
            
            progress++;
            monitor.worked(Math.round((float) progress * 100 / testCases.size()));
        }
    }

    private void exportTestScripts(List<TestCaseEntity> testCases, File exportFolder, SubMonitor monitor) throws IOException {
        ProjectEntity project = PlatformUtil.getCurrentProject();

        File sharedTestScriptFolder = new File(exportFolder, StringConstants.IMPORT_EXPORT_TEST_SCRIPTS_FOLDER);
        sharedTestScriptFolder.mkdirs();

        int progress = 0;
        for (TestCaseEntity testCase : testCases) {
            String testScriptParentRelativePath = TestCaseUtil.getTestScriptParentRelativePath(project,
                    testCase);

            String copyToFolderLocation = sharedTestScriptFolder.getAbsolutePath() + File.separator
                    + testScriptParentRelativePath;
            Files.createDirectories(Paths.get(copyToFolderLocation));
            File copyToFolder = new File(copyToFolderLocation);

            File testScriptFile = testCase.getScriptFile();
            if (testScriptFile.exists()) {
                FileUtils.copyFileToDirectory(testScriptFile, copyToFolder);
            }
            
            progress++;
            monitor.worked(Math.round((float) progress * 100 / testCases.size()));
        }
    }

    private void exportTestObjects(List<TestObjectEntity> testObjects, File exportFolder, SubMonitor monitor) throws IOException {
        ProjectEntity project = PlatformUtil.getCurrentProject();

        File sharedTestObjectFolder = new File(exportFolder, StringConstants.IMPORT_EXPORT_TEST_OBJECTS_FOLDER);
        sharedTestObjectFolder.mkdirs();

        int progress = 0;
        for (TestObjectEntity testObject : testObjects) {
            String parentRelativePath = TestObjectUtil.getTestObjectParentRelativePath(project, testObject);

            String copyToFolderLocation = sharedTestObjectFolder.getAbsolutePath() + File.separator
                    + parentRelativePath;
            Files.createDirectories(Paths.get(copyToFolderLocation));
            File copyToFolder = new File(copyToFolderLocation);

            File testObjectFile = new File(testObject.getFileLocation());
            if (testObjectFile.exists()) {
                FileUtils.copyFileToDirectory(testObjectFile, copyToFolder);
            }
            
            progress++;
            monitor.worked(Math.round((float) progress * 100 / testObjects.size()));
        }
    }
    
    private void exportProfiles(List<ExecutionProfileEntity> profiles, File exportFolder, SubMonitor monitor) throws IOException {
        File sharedProfileFolder = new File(exportFolder, StringConstants.IMPORT_EXPORT_PROFILES_FOLDER);
        sharedProfileFolder.mkdirs();
        
        int progress = 0;
        for (ExecutionProfileEntity profile : profiles) {
            File profileFile = new File(profile.getFileLocation());
            if (profileFile.exists()) {
                FileUtils.copyFileToDirectory(profileFile, sharedProfileFolder);
            }
            
            progress++;
            monitor.worked(Math.round((float) progress * 100 / profiles.size()));
        }
    }
    
    private void exportKeywords(List<File> keywords, File exportFolder, SubMonitor monitor) throws IOException {
        ProjectEntity project = PlatformUtil.getCurrentProject();

        File sharedKeywordFolder = new File(exportFolder, StringConstants.IMPORT_EXPORT_KEYWORDS_FOLDER);
        sharedKeywordFolder.mkdirs();

        int progress = 0;
        for (File keyword : keywords) {
            String parentRelativePath = KeywordUtil.getKeywordParentRelativePath(project, keyword);

            String copyToFolderLocation = sharedKeywordFolder.getAbsolutePath() + File.separator
                    + parentRelativePath;
            Files.createDirectories(Paths.get(copyToFolderLocation));
            File copyToFolder = new File(copyToFolderLocation);

            if (keyword.exists()) {
                FileUtils.copyFileToDirectory(keyword, copyToFolder);
            }
            
            progress++;
            monitor.worked(Math.round((float) progress * 100 / keywords.size()));
        }
    }
}
