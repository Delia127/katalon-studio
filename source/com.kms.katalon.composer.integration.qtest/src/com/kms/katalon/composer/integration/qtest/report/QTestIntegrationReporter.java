package com.kms.katalon.composer.integration.qtest.report;

import static com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager.getQTestSuiteListByIntegratedEntity;

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
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.LongConsoleOption;
import com.kms.katalon.execution.console.entity.StringConsoleOption;
import com.kms.katalon.execution.entity.ReportFolder;
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
    private static final String UPLOADED_ID_PREFIX = "qTestDestId";

    private static final String UPLOADED_TYPE_PREFIX = "qTestDestType";

    private static final long DEFAULT_DESTINATION_ID = -1L;

    private long destId = DEFAULT_DESTINATION_ID;

    private String destType;

    public static final LongConsoleOption DESTINATION_ID_CONSOLE_OPTION = new LongConsoleOption() {
        @Override
        public String getOption() {
            return UPLOADED_ID_PREFIX;
        }
    };

    public static final StringConsoleOption DESTINATION_TYPE_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return UPLOADED_TYPE_PREFIX;
        }
    };

    private IQTestCredential getCredential() {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        return QTestSettingCredential.getCredential(projectDir);
    }

    /**
     * Uploads result of test runs of test suite to qTest. A test run matches with a test case link in test suite.
     * System will find test run in the given test suite. If test run does not exist, system will create new.
     * Test result of test run will be uploaded via qTest's API.
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
            List<QTestSuite> qTestSuiteCollection = getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity);
            if (selectedQTestSuite == null) {
                return;
            }

            QTestTestCase qTestCase = QTestIntegrationTestCaseManager.getQTestTestCaseByIntegratedEntity(testCaseIntegratedEntity);
            QTestProject qTestProject = QTestIntegrationUtil.getTestCaseRepo(testCaseEntity, projectEntity)
                    .getQTestProject();
            QTestRun qTestRun = QTestIntegrationTestSuiteManager.getTestRunByTestSuiteAndTestCaseId(selectedQTestSuite,
                    qTestCase.getId());

            // If test run does not exist, create new and save it into test suite.
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
        System.out.println(MessageFormat.format(StringConstants.REPORT_MSG_UPLOAD_SUCCESFULLY, Long.toString(destId),
                destType));
    }

    private QTestSuite getSelectedTestSuite(TestSuiteEntity testSuite) throws Exception {
        IntegratedEntity testSuiteIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(testSuite);
        List<QTestSuite> qTestSuiteCollection = new ArrayList<QTestSuite>();
        if (testSuiteIntegratedEntity != null) {
            qTestSuiteCollection = getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity);
        }

        QTestProject qTestProject = QTestIntegrationUtil.getTestSuiteRepo(testSuite,
                ProjectController.getInstance().getCurrentProject()).getQTestProject();
        if (isUploadByDefault()) {
            return QTestIntegrationTestSuiteManager.getSelectedQTestSuiteByIntegratedEntity(qTestSuiteCollection);
        } else {
            QTestSuite selectedQTestSuite = null;

            // Search in list of qTestSuite of the given testSuite first
            if ("test-suite".equals(destType)) {
                selectedQTestSuite = QTestIntegrationTestSuiteManager.getQTestSuite(destId, qTestSuiteCollection);
                if (selectedQTestSuite != null) {
                    return updateTestSuite(selectedQTestSuite, qTestSuiteCollection, testSuite, qTestProject);
                }

                selectedQTestSuite = QTestIntegrationTestSuiteManager.getQTestSuite(destId, qTestProject,
                        getCredential());
                return updateTestSuite(selectedQTestSuite, qTestSuiteCollection, testSuite, qTestProject);
            } else {
                selectedQTestSuite = getQTestSuiteByParentId(destId, qTestSuiteCollection);

                if (selectedQTestSuite != null) {
                    return updateTestSuite(selectedQTestSuite, qTestSuiteCollection, testSuite, qTestProject);
                }

                // Update the uploaded destination's information
                QTestSuiteParent testSuiteParent = null;
                int parentType = QTestIntegrationTestSuiteManager.getTestSuiteParentType(destType);
                testSuiteParent = QTestIntegrationTestSuiteManager.getQTestSuiteParent(destId, parentType,
                        qTestProject, getCredential());

                // Create new QTestSuite that has not been uploaded
                selectedQTestSuite = new QTestSuite();
                selectedQTestSuite.setParent(testSuiteParent);

                // Update test suite with new selectedQTestSuite
                return updateTestSuite(selectedQTestSuite, qTestSuiteCollection, testSuite, qTestProject);
            }
        }
    }

    /**
     * Adds the given <code>selectedQTs</code> into <code>qTsCollection</code> if it doesn't contain.
     * Beside that, sets <code>selectedQTs</code> is selected and others are not.
     * Finally, saves the given <code>tsEntity</code>.
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
        IntegratedEntity tsIntegratedEntity = QTestIntegrationTestSuiteManager.getIntegratedEntityByTestSuiteList(qTsCollection);

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
     * @param testCaseEntity
     * @param testSuiteEntity
     * @return true if the given params have the same qTestProject. Otherwise, false.
     * @throws Exception: throws if system cannot get repositories of the given params
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
            List<QTestSuite> qTestSuiteCollection = getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity);

            QTestSuite selectedQTestSuite = QTestIntegrationTestSuiteManager.getSelectedQTestSuiteByIntegratedEntity(qTestSuiteCollection);

            QTestProject qTestProject = QTestIntegrationUtil.getTestSuiteRepo(testSuiteEntity, projectEntity)
                    .getQTestProject();

            return QTestIntegrationTestSuiteManager.getTestRuns(selectedQTestSuite, qTestProject,
                    QTestSettingCredential.getCredential(projectEntity.getFolderLocation()));
        }
        return null;

    }

    /**
     * If qTest integration and auto-submit result option are both enable,
     * uploads the given test log of the given test suite to qTest server.
     */
    @Override
    public void uploadTestSuiteResult(TestSuiteEntity testSuite, ReportFolder reportFolder) throws Exception {
        for (String subFolder : reportFolder.getReportFolders()) {
            TestSuiteLogRecord suiteLog = ReportUtil.generate(subFolder);

            if (!isIntegrationActive(testSuite)) {
                return;
            }

            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

            IntegratedEntity projectIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(projectEntity);
            for (ILogRecord logRecord : suiteLog.getChildRecords()) {
                if (!(logRecord instanceof TestCaseLogRecord)) {
                    continue;
                }

                uploadTestCaseResult(testSuite, projectIntegratedEntity, (TestCaseLogRecord) logRecord, suiteLog);
            }
        }
    }

    @Override
    public void uploadTestSuiteCollectionResult(ReportFolder reportFolder) throws Exception {
        // TODO Auto-generated method stub
        
    }

    private boolean isUploadByDefault() {
        return destId <= 0 || StringUtils.isBlank(destType);
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> integrationCommandList = new ArrayList<ConsoleOption<?>>();
        integrationCommandList.add(DESTINATION_ID_CONSOLE_OPTION);
        integrationCommandList.add(DESTINATION_TYPE_CONSOLE_OPTION);
        return integrationCommandList;
    }

    @Override
    public boolean isIntegrationActive(TestSuiteEntity testSuite) {
        if (testSuite == null) {
            return false;
        }

        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        if (!QTestIntegrationUtil.isIntegrationEnable(projectEntity)
                || !QTestSettingStore.isAutoSubmitResultActive(projectEntity.getFolderLocation())) {
            return false;
        }

        return QTestIntegrationUtil.getIntegratedEntity(projectEntity) != null
                && QTestIntegrationUtil.getIntegratedEntity(testSuite) != null;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if (StringUtils.isBlank(argumentValue)) {
            return;
        }
        if (consoleOption == DESTINATION_ID_CONSOLE_OPTION) {
            destId = Long.parseLong(argumentValue.trim());
        } else if (consoleOption == DESTINATION_TYPE_CONSOLE_OPTION) {
            destType = argumentValue.trim();
        }
    }
}
