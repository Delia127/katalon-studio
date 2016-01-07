package com.kms.katalon.composer.integration.qtest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.model.QTestLogEvaluation;
import com.kms.katalon.composer.integration.qtest.model.TestCaseRepo;
import com.kms.katalon.composer.integration.qtest.model.TestSuiteRepo;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.QTestIntegrationProjectManager;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestCaseManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestReport;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.setting.QTestSettingCredential;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestIntegrationUtil {

    private QTestIntegrationUtil() {
        // Disable default constructor
    }

    /**
     * @param projectEntity
     * @return true if uses set enable qTest integration.
     */
    public static boolean isIntegrationEnable(ProjectEntity projectEntity) {
        if (projectEntity == null) {
            return false;
        }

        String projectDir = projectEntity.getFolderLocation();

        if (!QTestSettingStore.isIntegrationActive(projectDir)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * The qTest {@link IntegratedEntity} of the given <code>fileEntity</code>
     * 
     * @param fileEntity
     * @return
     */
    public static IntegratedEntity getIntegratedEntity(IntegratedFileEntity fileEntity) {
        return fileEntity.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);
    }

    /**
     * Removes qTest {@link IntegratedEntity} from the given <code>fileEntity</code>
     * 
     * @param fileEntity
     * @return the {@link IntegratedFileEntity} after removing qTest {@link IntegratedEntity}
     */
    public static IntegratedFileEntity removeQTestIntegratedEntity(IntegratedFileEntity fileEntity) {
        IntegratedEntity qTestIntegratedEntity = getIntegratedEntity(fileEntity);
        if (qTestIntegratedEntity != null) {
            fileEntity.getIntegratedEntities().remove(qTestIntegratedEntity);
        }
        return fileEntity;
    }

    /**
     * Returns all {@link TestCaseRepo} as a {@link List} of the given projectEntity and list of qTestProjects.
     * 
     * @return an array list of {@link TestCaseRepo}
     * @see TestCaseRepo
     */
    public static List<TestCaseRepo> getTestCaseRepositories(ProjectEntity projectEntity,
            List<QTestProject> qTestProjects) {
        List<TestCaseRepo> testCaseRepositories = new ArrayList<TestCaseRepo>();
        for (QTestProject qTestProject : qTestProjects) {
            for (String testCaseFolderId : qTestProject.getTestCaseFolderIds()) {
                TestCaseRepo repo = new TestCaseRepo();
                repo.setQTestProject(qTestProject);
                repo.setFolderId(testCaseFolderId);

                FolderEntity folderEntity = null;
                QTestModule qTestModule = null;
                try {
                    folderEntity = FolderController.getInstance().getFolderByDisplayId(projectEntity, testCaseFolderId);

                    if (folderEntity != null) {
                        IntegratedEntity integratedFolderEntity = getIntegratedEntity(folderEntity);
                        if (integratedFolderEntity != null) {
                            qTestModule = QTestIntegrationFolderManager
                                    .getQTestModuleByIntegratedEntity(integratedFolderEntity);
                        }
                    }

                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                } finally {
                    repo.setQTestModule(qTestModule);
                }
                testCaseRepositories.add(repo);
            }
        }
        return testCaseRepositories;
    }

    /**
     * Returns all {@link TestSuiteRepo} as a {@link List} of the given projectEntity and list of qTestProjects.
     * 
     * @return an array list of {@link TestSuiteRepo}
     * @see TestSuiteRepo
     */
    public static List<TestSuiteRepo> getTestSuiteRepositories(ProjectEntity projectEntity,
            List<QTestProject> qTestProjects) {
        List<TestSuiteRepo> testSuiteRepositories = new ArrayList<TestSuiteRepo>();
        for (QTestProject qTestProject : qTestProjects) {
            for (String testCaseFolderId : qTestProject.getTestSuiteFolderIds()) {
                TestSuiteRepo repo = new TestSuiteRepo();
                repo.setQTestProject(qTestProject);
                repo.setFolderId(testCaseFolderId);
                testSuiteRepositories.add(repo);
            }
        }

        return testSuiteRepositories;
    }

    /**
     * Returns {@link TestCaseRepo} that the given {@link IntegratedFileEntity} belongs to.
     * 
     * @param entity
     * @return {@link TestCaseRepo} if system can find it in {@link ProjectEntity}. Otherwise, <code>null</code>
     * @throws Exception
     * @see TestCaseRepo
     */
    public static TestCaseRepo getTestCaseRepo(IntegratedFileEntity entity, ProjectEntity projectEntity)
            throws Exception {
        if (entity == null) {
            return null;
        }

        IntegratedEntity projectIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(projectEntity);

        if (projectIntegratedEntity == null) {
            return null;
        }

        List<QTestProject> qTestProjects = QTestIntegrationProjectManager
                .getQTestProjectsByIntegratedEntity(projectIntegratedEntity);
        String entityId = entity.getRelativePathForUI().replace(File.separator,
                GlobalStringConstants.ENTITY_ID_SEPERATOR);

        for (TestCaseRepo testCaseRepo : getTestCaseRepositories(projectEntity, qTestProjects)) {
            String repoFolderId = testCaseRepo.getFolderId();
            if (entityId.startsWith(repoFolderId + GlobalStringConstants.ENTITY_ID_SEPERATOR)
                    || entityId.equals(repoFolderId)) {
                return testCaseRepo;
            }
        }

        return null;
    }

    /**
     * Returns {@link TestSuiteRepo} that the given {@link IntegratedFileEntity} belongs to.
     * 
     * @param entity
     * @return {@link TestSuiteRepo} if system can find it in {@link ProjectEntity}. Otherwise, <code>null</code>
     * @throws Exception
     */
    public static TestSuiteRepo getTestSuiteRepo(IntegratedFileEntity entity, ProjectEntity projectEntity)
            throws Exception {
        if (entity == null) return null;
        String entityId = entity.getRelativePathForUI().replace(File.separator,
                GlobalStringConstants.ENTITY_ID_SEPERATOR);

        return getTestSuiteRepo(entityId, projectEntity);
    }

    /**
     * Returns {@link TestSuiteRepo} that the given <code>entityId</code> belongs to.
     * 
     * @param entityId
     * @param projectEntity
     * @return
     */
    public static TestSuiteRepo getTestSuiteRepo(String entityId, ProjectEntity projectEntity) {
        IntegratedEntity projectIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(projectEntity);
        if (projectIntegratedEntity == null) {
            return null;
        }
        List<QTestProject> qTestProjects = QTestIntegrationProjectManager
                .getQTestProjectsByIntegratedEntity(projectIntegratedEntity);

        for (TestSuiteRepo testSuiteRepo : getTestSuiteRepositories(projectEntity, qTestProjects)) {
            String repoFolderId = testSuiteRepo.getFolderId();
            if (entityId.startsWith(repoFolderId + GlobalStringConstants.ENTITY_ID_SEPERATOR)
                    || entityId.equals(repoFolderId)) {
                return testSuiteRepo;
            }
        }

        return null;
    }

    /**
     * Checks the given {@link IntegratedFileEntity} can be downloaded or disintegrated.
     * <p>
     * For {@link TestCaseEntity} and test case {@link FolderEntity} only.
     * <p>
     * <p>
     * If the given entity is a {@link TestCaseEntity} or {@link TestSuiteEntity}, it can be downloaded or disintegrated
     * that means it contains qTest {@link IntegratedEntity} inside o its {@link TestCaseRepo}/ {@link TestSuiteRepo}
     * also is not null.
     * <p>
     * * If the given entity is a {@link FolderEntity}, it can be downloaded or disintegrated that means it has any
     * child that contains qTest {@link IntegratedEntity} inside and its {@link TestCaseRepo}/{@link TestSuiteRepo} also
     * is not null.
     * 
     * @param entity the entity that needs to be checked
     * @return <code>true</code> if the given {@link IntegratedFileEntity} can be downloaded or disintegrated.
     * Otherwise, <code>false</code>
     * @throws Exception
     */
    public static boolean canBeDownloadedOrDisintegrated(IntegratedFileEntity entity, ProjectEntity projectEntity)
            throws Exception {
        boolean isIntegrated = (QTestIntegrationUtil.getIntegratedEntity(entity) != null);

        if (entity instanceof FolderEntity) {

            FolderEntity folderEntity = (FolderEntity) entity;

            if (folderEntity.getFolderType() == FolderType.TESTCASE) {
                TestCaseRepo testCaseRepo = getTestCaseRepo(entity, projectEntity);
                if (testCaseRepo == null) {
                    return false;
                }

                if (testCaseRepo.getFolderId().equals(folderEntity.getIdForDisplay())) {
                    isIntegrated = false;
                }
            } else if (folderEntity.getFolderType() == FolderType.TESTSUITE) {
                TestSuiteRepo testSuiteRepo = getTestSuiteRepo(entity, projectEntity);
                if (testSuiteRepo == null) {
                    return false;
                }

                if (testSuiteRepo.getFolderId().equals(folderEntity.getIdForDisplay())) {
                    isIntegrated = false;
                }
            }

            if (isIntegrated) {
                return true;
            }

            for (FileEntity childEntity : FolderController.getInstance().getChildren(folderEntity)) {
                if (canBeDownloadedOrDisintegrated((IntegratedFileEntity) childEntity, projectEntity)) {
                    return true;
                }
            }
            return false;

        } else if (entity instanceof TestCaseEntity) {
            return isIntegrated && (getTestCaseRepo(entity, projectEntity) != null);
        } else if (entity instanceof TestSuiteEntity) {
            return isIntegrated && (getTestSuiteRepo(entity, projectEntity) != null);
        } else {
            return false;
        }
    }

    /**
     * Checks the given {@link IntegratedFileEntity} can be uploaded or not.
     * 
     * @param entity the entity that needs to be checked
     * @return true if the given {@link IntegratedFileEntity} can be uploaded. Otherwise, false.
     * @throws Exception
     */
    public static boolean canBeUploaded(IntegratedFileEntity entity, ProjectEntity projectEntity) throws Exception {
        boolean isNotIntegrated = (QTestIntegrationUtil.getIntegratedEntity(entity) == null);

        if (entity instanceof FolderEntity) {
            if (((FolderEntity) entity).getFolderType() == FolderType.TESTCASE) {
                if (getTestCaseRepo(entity, projectEntity) == null) {
                    return false;
                }
            } else if (((FolderEntity) entity).getFolderType() == FolderType.TESTSUITE) {
                if (getTestSuiteRepo(entity, projectEntity) == null) {
                    return false;
                }
            }

            FolderEntity folderEntity = (FolderEntity) entity;
            for (FileEntity childEntity : FolderController.getInstance().getChildren(folderEntity)) {
                if (canBeUploaded((IntegratedFileEntity) childEntity, projectEntity)) {
                    return true;
                }
            }

            return false;
        } else if (entity instanceof TestCaseEntity) {
            if (isNotIntegrated && (getTestCaseRepo(entity, projectEntity) != null)) {
                QTestModule module = QTestIntegrationFolderManager.getQTestModuleByFolderEntity(entity
                        .getParentFolder());

                // Cannot upload test case under root module.
                if (module != null && module.getParentId() <= 0) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else if (entity instanceof TestSuiteEntity) {
            return (getTestSuiteRepo(entity, projectEntity) != null);
        } else {
            return false;
        }
    }

    /**
     * Returns absolute path as string of qTest folder inside katalon's temporary folder.
     */
    public static String getTempDirPath() {
        String tempDir = ProjectController.getInstance().getTempDir();
        File qTestTempFolder = new File(tempDir, QTestStringConstants.PRODUCT_NAME);
        if (!qTestTempFolder.exists()) {
            qTestTempFolder.mkdirs();
        }
        return qTestTempFolder.getAbsolutePath();
    }

    /**
     * Stores the given {@link IntegratedEntity} into the given {@link IntegratedFileEntity}.
     * 
     * @param entity the previous {@link IntegratedFileEntity} needs to update
     * @param newIntegrated new qTest {@link IntegratedEntity}
     * @return new {@link IntegratedFileEntity} after the addition complete.
     */
    public static IntegratedFileEntity updateFileIntegratedEntity(IntegratedFileEntity entity,
            IntegratedEntity newIntegrated) {
        IntegratedEntity oldIntegrated = QTestIntegrationUtil.getIntegratedEntity(entity);

        // Otherwise, add the new one to integrated list
        int index = 0;
        if (oldIntegrated == null) {
            index = entity.getIntegratedEntities().size();
        } else {
            index = entity.getIntegratedEntities().indexOf(oldIntegrated);
            entity.getIntegratedEntities().remove(index);
        }

        if (index >= entity.getIntegratedEntities().size()) {
            entity.getIntegratedEntities().add(newIntegrated);
        } else {
            entity.getIntegratedEntities().add(index, oldIntegrated);
        }
        return entity;
    }

    /**
     * Puts all information of the given uploadedPreview into the given reportEntity. After that, saves the given
     * reportEntity.
     * 
     * @param reportEntity the report that will be saved.
     * @param uploadedPreview the preview test case result entity will be put into the report.
     * @throws Exception throws if the project file <code>.prj<code> is invalid format.
     * @see {@link #updateFileIntegratedEntity(IntegratedFileEntity, IntegratedEntity)}
     */
    public static void saveReportEntity(ReportEntity reportEntity, QTestLogUploadedPreview uploadedPreview)
            throws Exception {
        IntegratedEntity reportIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(reportEntity);
        QTestReport qTestReport = QTestIntegrationReportManager
                .getQTestReportByIntegratedEntity(reportIntegratedEntity);

        if (qTestReport == null) {
            qTestReport = new QTestReport();
        }

        qTestReport.getTestLogMap().put(uploadedPreview.getTestLogIndex(), uploadedPreview.getQTestLog());

        reportIntegratedEntity = QTestIntegrationReportManager.getIntegratedEntityByQTestReport(qTestReport);

        reportEntity = (ReportEntity) QTestIntegrationUtil.updateFileIntegratedEntity(reportEntity,
                reportIntegratedEntity);
        ReportController.getInstance().updateReport(reportEntity);
    }

    /**
     * Called by uploadTestCaseResult. Add testRun to qTestSuite and save the given qTestSuite into testSuiteEntity.
     */
    public static TestSuiteEntity addNewTestRunToTestSuite(TestSuiteEntity testSuiteEntity,
            IntegratedEntity testSuiteIntegratedEntity, QTestSuite qTestSuite, QTestRun testRun,
            List<QTestSuite> qTestSuiteCollection) throws Exception {
        qTestSuite.getTestRuns().add(testRun);

        for (int index = 0; index < qTestSuiteCollection.size(); index++) {
            if (qTestSuiteCollection.get(index).getId() == qTestSuite.getId()) {
                QTestIntegrationTestSuiteManager.addQTestSuiteToIntegratedEntity(qTestSuite, testSuiteIntegratedEntity,
                        index);

                return TestSuiteController.getInstance().updateTestSuite(testSuiteEntity);
            }
        }
        return null;
    }

    /**
     * Return a list of {@link QTestSuite} that each item can be uploaded to qTest of the give <code>testSuite</code>
     * 
     * @param testSuite a {@link TestSuiteEntity}
     * @return an array list of {@link QTestSuite}
     * @throws QTestInvalidFormatException thrown the given <code>testSuite</code> has invalid qTest integrated
     * information.
     */
    public static List<QTestSuite> getUnuploadedQTestSuites(TestSuiteEntity testSuite)
            throws QTestInvalidFormatException {
        List<QTestSuite> qTestSuites = QTestIntegrationTestSuiteManager
                .getQTestSuiteListByIntegratedEntity(getIntegratedEntity(testSuite));
        List<QTestSuite> unuploadedQTestSuites = new ArrayList<QTestSuite>();

        for (QTestSuite availableQTestSuite : qTestSuites) {
            if (availableQTestSuite.getId() <= 0) {
                unuploadedQTestSuites.add(availableQTestSuite);
            }
        }

        return unuploadedQTestSuites;
    }

    /**
     * Returns new {@link TestCaseRepo} that created by the given params.
     * 
     * @param qTestModule
     * @param qTestProject
     * @param folderEntity
     * @return new instance of {@link TestCaseRepo}
     */
    public static TestCaseRepo getNewTestCaseRepo(QTestModule qTestModule, QTestProject qTestProject,
            FolderEntity folderEntity) {
        TestCaseRepo testCaseRepo = new TestCaseRepo();
        testCaseRepo.setQTestModule(qTestModule);
        testCaseRepo.setQTestProject(qTestProject);

        String folderId = folderEntity.getIdForDisplay();
        testCaseRepo.setFolderId(folderId);
        return testCaseRepo;
    }

    /**
     * Returns new {@link TestSuiteRepo} that created by the given params.
     * 
     * @param qTestProject
     * @param folderEntity
     * @return new instance of {@link TestSuiteRepo}
     */
    public static TestSuiteRepo getNewTestSuiteRepo(QTestProject qTestProject, FolderEntity folderEntity) {
        TestSuiteRepo testSuiteRepo = new TestSuiteRepo();
        testSuiteRepo.setQTestProject(qTestProject);

        String folderId = folderEntity.getIdForDisplay();
        testSuiteRepo.setFolderId(folderId);
        return testSuiteRepo;
    }

    /**
     * Returns an instance of {@link QTestLogEvaluation} that represents state of an {@link TestCaseLogRecord}.
     * 
     * @param testCaseLogRecord the {@link TestCaseLogRecord} that needs to evaluate.
     * @param qTestSuite
     * @param reportEntity
     * @return <li> {@link QTestLogEvaluation#CANNOT_INTEGRATE} if the given testCaseLogRecord cannot be integrated.</li>
     * <li> {@link QTestLogEvaluation#INTEGRATED} if the given testCaseLogRecord is integrated.</li> <li>
     * {@link QTestLogEvaluation#CAN_INTEGRATE} if the given testCaseLogRecord can be integrated but have not been
     * integrated yet.</li>
     * @throws Exception
     */
    public static QTestLogEvaluation evaluateTestCaseLog(TestCaseLogRecord testCaseLogRecord, QTestSuite qTestSuite,
            ReportEntity reportEntity) {
        QTestTestCase qTestCase = getQTestCase(testCaseLogRecord);
        if (qTestCase == null) {
            return QTestLogEvaluation.CANNOT_INTEGRATE;
        }

        if (!isSameQTestProject(testCaseLogRecord, LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity))) {
            return QTestLogEvaluation.CANNOT_INTEGRATE;
        }

        QTestRun qTestRun = QTestIntegrationTestSuiteManager.getTestRunByTestSuiteAndTestCaseId(qTestSuite,
                qTestCase.getId());
        if (qTestRun != null) {
            IntegratedEntity reportIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(reportEntity);
            QTestReport qTestReport;
            try {
                qTestReport = QTestIntegrationReportManager.getQTestReportByIntegratedEntity(reportIntegratedEntity);
            } catch (Exception e) {
                return QTestLogEvaluation.CANNOT_INTEGRATE;
            }

            if (qTestReport != null) {
                int index = getTestCaseLogIndex(testCaseLogRecord, reportEntity);
                if (index >= 0 && qTestReport.getTestLogMap().get(index) != null) {
                    return QTestLogEvaluation.INTEGRATED;
                }
            }
        }
        return QTestLogEvaluation.CAN_INTEGRATE;
    }

    /**
     * Checks that the givens parameters has the same {@link QTestProject} or not.
     * 
     * @param testCaseLogRecord
     * @param testSuiteLogRecord
     * @return true if same. Otherwise, false.
     */
    private static boolean isSameQTestProject(TestCaseLogRecord testCaseLogRecord, TestSuiteLogRecord testSuiteLogRecord) {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            // Return QTestLogEvaluation.CANNOT_INTEGRATE if testCaseEntity and testSuiteEntity are not in the same
            // qTestProject
            TestCaseRepo testCaseRepo = getTestCaseRepo(
                    TestCaseController.getInstance().getTestCaseByDisplayId(testCaseLogRecord.getId()), projectEntity);
            if (testCaseRepo == null || testCaseRepo.getQTestProject() == null) {
                return false;
            }
            TestSuiteEntity testSuiteEntity = QTestIntegrationUtil.getTestSuiteEntity(testSuiteLogRecord);
            TestSuiteRepo testSuiteRepo = getTestSuiteRepo(testSuiteEntity, projectEntity);

            if (testSuiteRepo == null || testSuiteRepo.getQTestProject() == null) {
                return false;
            }

            return testCaseRepo.getQTestProject().getId() == testSuiteRepo.getQTestProject().getId();
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return false;
        }
    }

    /**
     * Returns an instance of {@link QTestTestCase} that has been referred by the given testCaseLogRecord.
     * <p>
     * a {@link QTestTestCase} is stored as a {@link IntegratedEntity} in a {@link TestCaseEntity}.
     * 
     * @param testCaseLogRecord
     * @return {@link QTestTestCase} if it exists. Otherwise, returns null.
     */
    public static QTestTestCase getQTestCase(TestCaseLogRecord testCaseLogRecord) {
        try {
            TestCaseEntity testCaseEntity = TestCaseController.getInstance().getTestCaseByDisplayId(
                    testCaseLogRecord.getId());
            if (testCaseEntity != null) {
                return QTestIntegrationTestCaseManager
                        .getQTestTestCaseByIntegratedEntity(getIntegratedEntity(testCaseEntity));
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    /**
     * Returns the index of {@link TestCaseLogRecord} in a collection that's child records of a
     * {@link TestSuiteLogRecord} that's the log of the given <code>reportEntity</code>
     * 
     * @param testCaseLogRecord
     * @param reportEntity
     * @return
     */
    public static int getTestCaseLogIndex(TestCaseLogRecord testCaseLogRecord, ReportEntity reportEntity) {
        TestSuiteLogRecord testSuiteLogRecord = LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity);
        if (testCaseLogRecord == null || testSuiteLogRecord == null) {
            return -1;
        }
        return Arrays.asList(testSuiteLogRecord.getChildRecords()).indexOf(testCaseLogRecord);
    }

    /**
     * Returns the selected {@link QTestSuite} of a {@link TestSuiteEntity} that is integrated with qTest.
     * <p>
     * The {@link TestSuiteEntity} whose id equals with the id of the given <code>testSuiteLogRecord</code>.
     * 
     * @param testSuiteLogRecord
     * @return
     */
    public static QTestSuite getSelectedQTestSuite(TestSuiteLogRecord testSuiteLogRecord) {
        try {
            if (testSuiteLogRecord == null) {
                return null;
            }

            TestSuiteEntity testSuiteEntity = getTestSuiteEntity(testSuiteLogRecord);
            if (testSuiteEntity == null) {
                return null;
            }

            IntegratedEntity testSuiteIntegratedEntity = getIntegratedEntity(testSuiteEntity);

            if (testSuiteIntegratedEntity != null) {
                List<QTestSuite> qTestSuiteCollection = QTestIntegrationTestSuiteManager
                        .getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity);
                return QTestIntegrationTestSuiteManager.getSelectedQTestSuiteByIntegratedEntity(qTestSuiteCollection);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    /**
     * Stores the given <code>qTestReport</code> as a qTest's {@link IntegratedEntity} to the given
     * <code>reportEntity</code>.
     * 
     * @param qTestReport
     * @param reportEntity
     * @return
     */
    public static ReportEntity saveReportEntity(QTestReport qTestReport, ReportEntity reportEntity) {
        try {
            IntegratedEntity reportIntegratedEntity = QTestIntegrationReportManager
                    .getIntegratedEntityByQTestReport(qTestReport);
            reportEntity = (ReportEntity) QTestIntegrationUtil.updateFileIntegratedEntity(reportEntity,
                    reportIntegratedEntity);
            reportEntity = ReportController.getInstance().updateReport(reportEntity);
            return reportEntity;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    /**
     * Returns a {@link TestSuiteEntity} that's id equals with the id of the given <code>testSuiteLogRecord</code>
     * 
     * @param testSuiteLogRecord
     * @return
     * @throws Exception
     */
    public static TestSuiteEntity getTestSuiteEntity(TestSuiteLogRecord testSuiteLogRecord) throws Exception {
        return TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteLogRecord.getId(),
                ProjectController.getInstance().getCurrentProject());
    }

    /**
     * Returns the first {@link QTestRun} in the given <code>currentQTestRuns</code> that's
     * {@link QTestRun#getQTestCaseId()} equals with <code>id</code> of the given <code>qTestCase</code>.
     * 
     * @param qTestCase
     * @param currentQTestRuns
     * @return {@link QTestRun} if can be found. Otherwise, null.
     */
    public static QTestRun getQTestRun(QTestTestCase qTestCase, List<QTestRun> currentQTestRuns) {
        if (qTestCase == null || currentQTestRuns == null) {
            return null;
        }

        for (QTestRun currentQTestRun : currentQTestRuns) {
            if (qTestCase.getId() == currentQTestRun.getQTestCaseId()) {
                return currentQTestRun;
            }
        }
        return null;
    }

    /**
     * Fetches the list of {@link QTestRun} under the given selected {@link QTestSuite} of the given
     * <code>testSuiteEntity</code> from qTest
     * 
     * @param testSuiteEntity
     * @param projectEntity
     * @return list of {@link QTestRun} that each one has id that equals with a {@link QTestTestCase#getId()} in the
     * given <code>testSuiteEntity</code>, null if the given <code>testSuiteEntity</code> isn't integrated with qTest.
     * @throws Exception
     */
    public static List<QTestRun> getCurrentQTestRuns(TestSuiteEntity testSuiteEntity, ProjectEntity projectEntity)
            throws Exception {
        IntegratedEntity testSuiteIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(testSuiteEntity);

        if (testSuiteIntegratedEntity != null) {
            List<QTestSuite> qTestSuiteCollection = QTestIntegrationTestSuiteManager
                    .getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity);
            QTestSuite selectedQTestSuite = QTestIntegrationTestSuiteManager
                    .getSelectedQTestSuiteByIntegratedEntity(qTestSuiteCollection);
            if (selectedQTestSuite == null) {
                return null;
            }
            QTestProject qTestProject = QTestIntegrationUtil.getTestSuiteRepo(testSuiteEntity, projectEntity)
                    .getQTestProject();
            return QTestIntegrationTestSuiteManager.getTestRuns(selectedQTestSuite, qTestProject,
                    QTestSettingCredential.getCredential(projectEntity.getFolderLocation()));
        }
        return null;
    }

    /**
     * Checks the given report {@link FolderEntity} is in any {@link TestSuiteRepo} or not.
     * 
     * @param folderEntity
     * @param projectEntity
     * @return true if it's in a {@link TestSuiteRepo}. Otherwise, false.
     */
    public static boolean isFolderReportInTestSuiRepo(FolderEntity folderEntity, ProjectEntity projectEntity) {
        if (folderEntity.getFolderType() != FolderType.REPORT) {
            return false;
        }

        String testSuiteId = ReportController.getInstance().getTestSuiteFolderId(folderEntity.getIdForDisplay());
        TestSuiteRepo testSuiteRepo = QTestIntegrationUtil.getTestSuiteRepo(testSuiteId, projectEntity);
        if (testSuiteRepo == null) {
            return false;
        } else {
            return true;
        }
    }
}
