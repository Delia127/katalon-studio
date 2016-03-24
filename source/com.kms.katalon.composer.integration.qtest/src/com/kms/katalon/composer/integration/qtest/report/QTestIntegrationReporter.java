package com.kms.katalon.composer.integration.qtest.report;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.model.TestCaseRepo;
import com.kms.katalon.composer.integration.qtest.model.TestSuiteRepo;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.ConsoleOption;
import com.kms.katalon.execution.integration.ReportIntegrationContribution;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestCaseManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.entity.QTestLog;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.entity.QTestSuiteParent;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.setting.QTestSettingCredential;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestIntegrationReporter implements ReportIntegrationContribution {
    private QTestDestinationIdIntegrationCommand destinationIdCommand;
    private QTestDestinationTypeIntegrationCommand destinationTypeCommand;

    private IQTestCredential getCredential() {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        return QTestSettingCredential.getCredential(projectDir);
    }

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
        IntegratedEntity testCaseIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(testCaseEntity);

        if (testCaseIntegratedEntity != null && isSameQTestProject(testCaseEntity, testSuiteEntity, projectEntity)) {
            QTestSuite selectedQTestSuite = getSelectedTestSuite(testSuiteEntity);

            IntegratedEntity testSuiteIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(testSuiteEntity);
            List<QTestSuite> qTestSuiteCollection = QTestIntegrationTestSuiteManager
                    .getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity);
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
                            selectedQTestSuite, qTestProject, getCredential());
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
                    QTestIntegrationUtil.getTempDirPath(), suiteLog);
            uploadedPreview.setQTestLog(qTestLog);

            ReportEntity reportEntity = ReportController.getInstance().getReportEntity(suiteLog.getLogFolder());

            QTestIntegrationUtil.saveReportEntity(reportEntity, uploadedPreview);

            printlnSuccessfulMessage();
        }
    }

    private void printlnSuccessfulMessage() {
        if (isUploadByDefault()) {
            return;
        }
        long desId = destinationIdCommand.getDestinationId();
        String desType = destinationTypeCommand.getDestinationType();
        System.out.println(MessageFormat.format(StringConstants.REPORT_MSG_UPLOAD_SUCCESFULLY, Long.toString(desId),
                desType));
    }

    private QTestSuite getSelectedTestSuite(TestSuiteEntity testSuite) throws Exception {
        IntegratedEntity testSuiteIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(testSuite);
        List<QTestSuite> qTestSuiteCollection = new ArrayList<QTestSuite>();
        if (testSuiteIntegratedEntity != null) {
            qTestSuiteCollection = QTestIntegrationTestSuiteManager
                    .getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity);
        }

        QTestProject qTestProject = QTestIntegrationUtil.getTestSuiteRepo(testSuite,
                ProjectController.getInstance().getCurrentProject()).getQTestProject();
        if (isUploadByDefault()) {
            return QTestIntegrationTestSuiteManager.getSelectedQTestSuiteByIntegratedEntity(qTestSuiteCollection);
        } else {
            QTestSuite selectedQTestSuite = null;
            long desId = destinationIdCommand.getDestinationId();
            String desType = destinationTypeCommand.getDestinationType();

            // Search in list of qTestSuite of the given testSuite first
            if ("test-suite".equals(desType)) {
                selectedQTestSuite = QTestIntegrationTestSuiteManager.getQTestSuite(desId, qTestSuiteCollection);
                if (selectedQTestSuite != null) {
                    return updateTestSuite(selectedQTestSuite, qTestSuiteCollection, testSuite, qTestProject);
                }

                selectedQTestSuite = QTestIntegrationTestSuiteManager.getQTestSuite(desId, qTestProject,
                        getCredential());
                return updateTestSuite(selectedQTestSuite, qTestSuiteCollection, testSuite, qTestProject);
            } else {
                selectedQTestSuite = getQTestSuiteByParentId(desId, qTestSuiteCollection);

                if (selectedQTestSuite != null) {
                    return updateTestSuite(selectedQTestSuite, qTestSuiteCollection, testSuite, qTestProject);
                }

                // Update the uploaded destination's information
                QTestSuiteParent testSuiteParent = null;
                int parentType = QTestIntegrationTestSuiteManager.getTestSuiteParentType(desType);
                testSuiteParent = QTestIntegrationTestSuiteManager.getQTestSuiteParent(desId, parentType, qTestProject,
                        getCredential());

                // Create new QTestSuite that has not been uploaded
                selectedQTestSuite = new QTestSuite();
                selectedQTestSuite.setParent(testSuiteParent);

                // Update test suite with new selectedQTestSuite
                return updateTestSuite(selectedQTestSuite, qTestSuiteCollection, testSuite, qTestProject);
            }
        }
    }

    /**
     * Adds the given <code>selectedQTs</code> into <code>qTsCollection</code> if it doesn't contain. Beside that, sets
     * <code>selectedQTs</code> is selected and others are not. Finally, saves the given <code>tsEntity</code>.
     * 
     * @param selectedQTs
     * @param qTsCollection
     * @param tsEntity
     * @return
     * @throws Exception
     */
    private QTestSuite updateTestSuite(QTestSuite selectedQTs, List<QTestSuite> qTsCollection,
            TestSuiteEntity testSuite, QTestProject qTestProject) throws Exception {
        // Make sure the selectedQTestSuite is uploaded
        if (selectedQTs.getId() <= 0L) {
            QTestSuite siblingQTestSuite = QTestIntegrationTestSuiteManager.getDuplicatedTestSuiteOnQTest(
                    getCredential(), testSuite.getName(), selectedQTs.getParent(), qTestProject);

            if (siblingQTestSuite != null) {
                selectedQTs = siblingQTestSuite;
            } else {
                selectedQTs = QTestIntegrationTestSuiteManager.uploadTestSuite(getCredential(), testSuite.getName(),
                        testSuite.getDescription(), selectedQTs.getParent(), qTestProject);
            }
        }

        // Set selectedQTs to be default
        QTestIntegrationTestSuiteManager.setSelectedQTestSuite(selectedQTs, qTsCollection);

        // Update test suite
        IntegratedEntity tsIntegratedEntity = QTestIntegrationTestSuiteManager
                .getIntegratedEntityByTestSuiteList(qTsCollection);

        QTestIntegrationUtil.updateFileIntegratedEntity(testSuite, tsIntegratedEntity);

        TestSuiteController.getInstance().updateTestSuite(testSuite);
        return selectedQTs;

    }

    private QTestSuite getQTestSuiteByParentId(long id, List<QTestSuite> qTestSuiteCollection) {
        for (QTestSuite qTestSuite : qTestSuiteCollection) {
            QTestSuiteParent parent = qTestSuite.getParent();

            if (parent != null && id == parent.getId()) {
                return qTestSuite;
            }
        }
        return null;
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
                    QTestSettingCredential.getCredential(projectEntity.getFolderLocation()));
        }
        return null;

    }

    /**
     * If qTest integration and auto-submit result option are both enable, uploads the given test log of the given test
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

    private boolean isUploadByDefault() {
        return destinationIdCommand == null 
                || destinationTypeCommand == null
                || destinationIdCommand.getDestinationId() <= 0
                || StringUtils.isBlank(destinationTypeCommand.getDestinationType());
    }

    @Override
    public List<ConsoleOption<?>> getIntegrationCommands() {
        if (destinationIdCommand == null) {
            destinationIdCommand = new QTestDestinationIdIntegrationCommand();
        }
        if (destinationTypeCommand == null) {
            destinationTypeCommand = new QTestDestinationTypeIntegrationCommand();
        }
        List<ConsoleOption<?>> integrationCommandList = new ArrayList<ConsoleOption<?>>();
        integrationCommandList.add(destinationIdCommand);
        integrationCommandList.add(destinationTypeCommand);
        return integrationCommandList;
    }
}
