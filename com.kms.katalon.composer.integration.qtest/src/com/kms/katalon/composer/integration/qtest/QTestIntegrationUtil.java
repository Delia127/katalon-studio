package com.kms.katalon.composer.integration.qtest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.integration.qtest.model.TestCaseRepo;
import com.kms.katalon.composer.integration.qtest.model.TestSuiteRepo;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.QTestIntegrationProjectManager;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestReport;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;

public class QTestIntegrationUtil {

    private QTestIntegrationUtil() {
        // Disable default constructor
    }

    /**
     * Returns all {@link TestCaseRepo} as a {@link List} of the given
     * projectEntity and list of qTestProjects.
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
                try {
                    folderEntity = FolderController.getInstance().getFolderByDisplayId(projectEntity, testCaseFolderId);

                    QTestModule qTestModule = null;

                    if (folderEntity != null) {
                        IntegratedEntity integratedFolderEntity = folderEntity
                                .getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);
                        if (integratedFolderEntity != null) {
                            qTestModule = QTestIntegrationFolderManager
                                    .getQTestModuleByIntegratedEntity(integratedFolderEntity);
                        }
                    }

                    if (qTestModule != null) {
                        repo.setQTestModule(qTestModule);
                    }
                } catch (Exception ex) {
                    repo.setQTestModule(null);
                }
                testCaseRepositories.add(repo);
            }
        }
        return testCaseRepositories;
    }

    /**
     * Returns all {@link TestSuiteRepo} as a {@link List} of the given
     * projectEntity and list of qTestProjects.
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
     * Returns {@link TestCaseRepo} that the given {@link IntegratedFileEntity}
     * belongs to.
     * 
     * @param entity
     * @return {@link TestCaseRepo} if system can find it in
     *         {@link ProjectEntity}. Otherwise, <code>null</code>
     * @throws Exception
     * @see TestCaseRepo
     */
    public static TestCaseRepo getTestCaseRepo(IntegratedFileEntity entity, ProjectEntity projectEntity)
            throws Exception {
        if (entity == null) {
            return null;
        }

        IntegratedEntity projectIntegratedEntity = projectEntity.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);

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
     * Returns {@link TestSuiteRepo} that the given {@link IntegratedFileEntity}
     * belongs to.
     * 
     * @param entity
     * @return {@link TestSuiteRepo} if system can find it in
     *         {@link ProjectEntity}. Otherwise, <code>null</code>
     * @throws Exception
     */
    public static TestSuiteRepo getTestSuiteRepo(IntegratedFileEntity entity, ProjectEntity projectEntity)
            throws Exception {
        if (entity == null) return null;
        IntegratedEntity projectIntegratedEntity = projectEntity.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);
        if (projectIntegratedEntity == null) return null;
        List<QTestProject> qTestProjects = QTestIntegrationProjectManager
                .getQTestProjectsByIntegratedEntity(projectIntegratedEntity);
        String entityId = entity.getRelativePathForUI().replace(File.separator,
                GlobalStringConstants.ENTITY_ID_SEPERATOR);

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
     * Checks the given {@link IntegratedFileEntity} can be downloaded or
     * disintegrated.
     * <p>
     * For {@link TestCaseEntity} and test case {@link FolderEntity} only.
     * <p>
     * <p>
     * If the given entity is a {@link TestCaseEntity}, it can be downloaded or
     * disintegrated that means it contains qTest {@link IntegratedEntity}
     * inside and its {@link TestCaseRepo} also is not null.
     * <p>
     * * If the given entity is a {@link FolderEntity}, it can be downloaded or
     * disintegrated that means it has any child that contains qTest
     * {@link IntegratedEntity} inside and its {@link TestCaseRepo} also is not
     * null.
     * 
     * @param entity
     *            the entity that needs to be checked
     * @return <code>true</code> if the given {@link IntegratedFileEntity} can
     *         be downloaded or disintegrated. Otherwise, <code>false</code>
     * @throws Exception
     */
    public static boolean canBeDownloadedOrDisintegrated(IntegratedFileEntity entity, ProjectEntity projectEntity)
            throws Exception {
        boolean isIntegrated = (entity.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME) != null);

        if (entity instanceof FolderEntity) {

            FolderEntity folderEntity = (FolderEntity) entity;
            if (isIntegrated) {
                return true;
            }

            if (getTestCaseRepo(entity, projectEntity) == null) {
                return false;
            }

            for (FileEntity childEntity : FolderController.getInstance().getChildren(folderEntity)) {
                if (canBeDownloadedOrDisintegrated((IntegratedFileEntity) childEntity, projectEntity)) {
                    return true;
                }
            }
            return false;

        } else {
            return isIntegrated;
        }
    }

    /**
     * Checks the given {@link IntegratedFileEntity} can be uploaded or not.
     * 
     * @param entity
     *            the entity that needs to be checked
     * @return true if the given {@link IntegratedFileEntity} can be uploaded.
     *         Otherwise, false.
     * @throws Exception
     */
    public static boolean canBeUploaded(IntegratedFileEntity entity, ProjectEntity projectEntity) throws Exception {
        if (getTestCaseRepo(entity, projectEntity) == null) return false;

        boolean isNotIntegrated = (entity.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME) == null);

        if (entity instanceof FolderEntity) {
            FolderEntity folderEntity = (FolderEntity) entity;
            for (FileEntity childEntity : FolderController.getInstance().getChildren(folderEntity)) {
                if (canBeUploaded((IntegratedFileEntity) childEntity, projectEntity)) {
                    return true;
                }
            }

            return false;
        } else {
            return isNotIntegrated;
        }
    }

    /**
     * Returns absolute path as string of qTest folder inside katalon's
     * temporary folder.
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
     * Stores the given {@link IntegratedEntity} into the given
     * {@link IntegratedFileEntity}.
     * 
     * @param entity
     *            the previous {@link IntegratedFileEntity} needs to update
     * @param newIntegrated
     *            new qTest {@link IntegratedEntity}
     * @return new {@link IntegratedFileEntity} after the addition complete.
     */
    public static IntegratedFileEntity updateFileIntegratedEntity(IntegratedFileEntity entity,
            IntegratedEntity newIntegrated) {
        IntegratedEntity oldIntegrated = entity.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);

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
     * Puts all information of the given uploadedPreview into the given
     * reportEntity. After that, saves the given reportEntity.
     * 
     * @param reportEntity
     *            the report that will be saved.
     * @param uploadedPreview
     *            the preview test case result entity will be put into the
     *            report.
     * @throws Exception
     *             throws if the project file
     *             <code>.prj<code> is invalid format.
     * @see {@link #updateFileIntegratedEntity(IntegratedFileEntity, IntegratedEntity)}
     */
    public static void saveReportEntity(ReportEntity reportEntity, QTestLogUploadedPreview uploadedPreview)
            throws Exception {
        IntegratedEntity reportIntegratedEntity = reportEntity.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);
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
     * Called by uploadTestCaseResult. Add testRun to qTestSuite and save the
     * given qTestSuite into testSuiteEntity.
     */
    public static void addNewTestRunToTestSuite(TestSuiteEntity testSuiteEntity,
            IntegratedEntity testSuiteIntegratedEntity, QTestSuite qTestSuite, QTestRun testRun,
            List<QTestSuite> qTestSuiteCollection) throws Exception {
        qTestSuite.getTestRuns().add(testRun);

        QTestIntegrationTestSuiteManager.addQTestSuiteToIntegratedEntity(qTestSuite, testSuiteIntegratedEntity,
                qTestSuiteCollection.indexOf(qTestSuite));

        TestSuiteController.getInstance().updateTestSuite(testSuiteEntity);
    }
}
