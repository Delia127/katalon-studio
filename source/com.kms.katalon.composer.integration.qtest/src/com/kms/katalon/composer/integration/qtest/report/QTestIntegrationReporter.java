package com.kms.katalon.composer.integration.qtest.report;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.model.TestCaseRepo;
import com.kms.katalon.composer.integration.qtest.model.TestSuiteRepo;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.integration.ReportIntegrationContribution;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestCaseManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.entity.QTestLog;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.setting.QTestSettingCredential;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestIntegrationReporter implements ReportIntegrationContribution {

    /**
     * Uploads result of test runs of test suite to qTest. A test run matches with a test case link in test suite.
     * System will find test run in the given test suite. If test run does not exist, system will create new. Test
     * result of test run will be uploaded via qTest's API.
     */
    public void uploadTestCaseResult(TestSuiteEntity testSuiteEntity, IntegratedEntity projectIntegratedEntity,
            TestCaseLogRecord testLogEntity, TestSuiteLogRecord suiteLog) throws Exception {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        String projectDir = projectEntity.getFolderLocation();
        TestCaseEntity testCaseEntity = TestCaseController.getInstance().getTestCaseByDisplayId(testLogEntity.getId());
        IntegratedEntity testSuiteIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(testSuiteEntity);
        IntegratedEntity testCaseIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(testCaseEntity);

        if (testSuiteIntegratedEntity != null && testCaseIntegratedEntity != null
                && isSameQTestProject(testCaseEntity, testSuiteEntity, projectEntity)) {
            List<QTestSuite> qTestSuiteCollection = QTestIntegrationTestSuiteManager
                    .getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity);
            QTestSuite selectedQTestSuite = QTestIntegrationTestSuiteManager
                    .getSelectedQTestSuiteByIntegratedEntity(qTestSuiteCollection);
            if (selectedQTestSuite == null) {
                return;
            }

            QTestTestCase qTestCase = QTestIntegrationTestCaseManager
                    .getQTestTestCaseByIntegratedEntity(testCaseIntegratedEntity);
            QTestProject qTestProject = QTestIntegrationUtil.getTestCaseRepo(testCaseEntity, projectEntity)
                    .getQTestProject();
            QTestRun qTestRun = QTestIntegrationTestSuiteManager.getTestRunByTestSuiteAndTestCaseId(selectedQTestSuite,
                    qTestCase.getId());

            // If test run does not exist, create new and save it into
            // test suite.
            if (qTestRun == null) {
                // Check in the current list first
                qTestRun = QTestIntegrationUtil.getQTestRun(qTestCase,
                        getCurrentQTestRuns(testSuiteEntity, projectEntity));

                if (qTestRun == null) {
                    // If qTestRun isn't in the current list, upload new to qTest.
                    qTestRun = QTestIntegrationTestSuiteManager.uploadTestCaseInTestSuite(qTestCase,
                            selectedQTestSuite, qTestProject, projectDir);
                }

                // Save test suite.
                QTestIntegrationUtil.addNewTestRunToTestSuite(testSuiteEntity, testSuiteIntegratedEntity,
                        selectedQTestSuite, qTestRun, qTestSuiteCollection);
            }
            qTestRun.setTestCaseVersionId(qTestCase.getVersionId());

            int testLogIndex = Arrays.asList(suiteLog.getChildRecords()).indexOf(testLogEntity);

            QTestLogUploadedPreview uploadedPreview = new QTestLogUploadedPreview();
            uploadedPreview.setQTestProject(qTestProject);
            uploadedPreview.setQTestSuite(selectedQTestSuite);
            uploadedPreview.setQTestCase(qTestCase);
            uploadedPreview.setQTestRun(qTestRun);
            uploadedPreview.setQTestLog(null);
            uploadedPreview.setTestLogIndex(testLogIndex);
            uploadedPreview.setTestCaseLogRecord(testLogEntity);

            QTestLog qTestLog = QTestIntegrationReportManager.uploadTestLog(projectDir, uploadedPreview,
                    QTestIntegrationUtil.getTempDirPath(), new File(suiteLog.getLogFolder()));
            uploadedPreview.setQTestLog(qTestLog);

            ReportEntity reportEntity = ReportController.getInstance().getReportEntity(suiteLog.getLogFolder());

            QTestIntegrationUtil.saveReportEntity(reportEntity, uploadedPreview);
        }
    }

    /**
     * 
     * @param testCaseEntity
     * @param testSuiteEntity
     * @return true if the given params have the same qTestProject. Otherwise, false.
     * @throws Exception
     *             : throws if system cannot get repositories of the given params
     */
    private boolean isSameQTestProject(TestCaseEntity testCaseEntity, TestSuiteEntity testSuiteEntity,
            ProjectEntity projectEntity) throws Exception {
        TestCaseRepo testCaseRepo = QTestIntegrationUtil.getTestCaseRepo(testCaseEntity, projectEntity);
        TestSuiteRepo testSuiteRepo = QTestIntegrationUtil.getTestSuiteRepo(testSuiteEntity, projectEntity);

        if (testCaseRepo == null || testSuiteRepo == null) {
            return false;
        }

        QTestProject testCaseProject = testCaseRepo.getQTestProject();
        QTestProject testSuiteProject = testSuiteRepo.getQTestProject();
        return testCaseProject.equals(testSuiteProject);
    }

    private List<QTestRun> getCurrentQTestRuns(TestSuiteEntity testSuiteEntity, ProjectEntity projectEntity)
            throws Exception {
        IntegratedEntity testSuiteIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(testSuiteEntity);

        if (testSuiteIntegratedEntity != null) {
            List<QTestSuite> qTestSuiteCollection = QTestIntegrationTestSuiteManager
                    .getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity);
            QTestSuite selectedQTestSuite = QTestIntegrationTestSuiteManager
                    .getSelectedQTestSuiteByIntegratedEntity(qTestSuiteCollection);

            QTestProject qTestProject = QTestIntegrationUtil.getTestSuiteRepo(testSuiteEntity, projectEntity)
                    .getQTestProject();
            return QTestIntegrationTestSuiteManager.getTestRuns(selectedQTestSuite, qTestProject,
                    new QTestSettingCredential(projectEntity.getFolderLocation()));
        }
        return null;

    }

    /**
     * If qTest integration and auto-submit result option are both enable, Uploads the given test log of the given test
     * suite to qTest server.
     */
    @Override
    public void uploadTestSuiteResult(TestSuiteEntity testSuite, TestSuiteLogRecord suiteLog) throws Exception {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        String projectDir = projectEntity.getFolderLocation();
        if (QTestSettingStore.isIntegrationActive(projectDir) && QTestSettingStore.isAutoSubmitResultActive(projectDir)) {
            IntegratedEntity projectIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(projectEntity);
            if (projectIntegratedEntity == null) {
                return;
            }

            for (ILogRecord logRecord : suiteLog.getChildRecords()) {
                if (!(logRecord instanceof TestCaseLogRecord)) {
                    continue;
                }

                TestCaseLogRecord testCaseLogRecord = (TestCaseLogRecord) logRecord;

                if (!QTestIntegrationReportManager.isAvailableForSendingResult(testCaseLogRecord.getStatus()
                        .getStatusValue(), projectDir)) {
                    continue;
                }

                uploadTestCaseResult(testSuite, projectIntegratedEntity, testCaseLogRecord, suiteLog);
            }
        }
    }

};
