package com.kms.katalon.composer.integration.qtest.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.dialog.ListReportUploadingPreviewDialog;
import com.kms.katalon.composer.integration.qtest.job.UploadTestCaseResultJob;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.entity.QTestLog;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;

public class QTestUploadReportHandler extends AbstractQTestHandler {

    private ReportEntity fReportEntity;
    private List<TestCaseLogRecord> fTestCasesCanBeUploaded;
    
    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell activeShell;

    @Inject
    private ESelectionService selectionService;

    @CanExecute
    public boolean canExecute() {
        try {
            Object selectedEntity = getFirstSelectedObject(selectionService);

            if (selectedEntity == null || !(selectedEntity instanceof ReportEntity)) {
                return false;
            }

            fReportEntity = (ReportEntity) selectedEntity;
            TestSuiteLogRecord testSuiteLogRecord = LogRecordLookup.getInstance().getTestSuiteLogRecord(fReportEntity);
            if (testSuiteLogRecord == null) {
                return false;
            }

            fTestCasesCanBeUploaded = new ArrayList<TestCaseLogRecord>();

            for (ILogRecord selectedTestCaseLogObject : testSuiteLogRecord.getChildRecords()) {
                TestCaseLogRecord testCaseLogRecord = (TestCaseLogRecord) selectedTestCaseLogObject;
                switch (QTestIntegrationUtil.evaluateTestCaseLog(testCaseLogRecord,
                        QTestIntegrationUtil.getSelectedQTestSuite(testSuiteLogRecord), fReportEntity)) {
                    case CAN_INTEGRATE:
                        fTestCasesCanBeUploaded.add(testCaseLogRecord);
                        break;
                    default:
                        break;
                }
            }

            if (fTestCasesCanBeUploaded.size() > 0) {
                return true;
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Execute
    public void execute() {
        performUploadTestCaseLogs(fTestCasesCanBeUploaded, fReportEntity, activeShell);
    }

    /**
     * Uploads the given <code>testCasesCanBeUploaded</code> of the given <code>reportEntity</code> to qTest server by
     * using {@link UploadTestCaseResultJob}.
     * 
     * @param testCasesCanBeUploaded
     * @param reportEntity
     * @param shell
     */
    public static void performUploadTestCaseLogs(List<TestCaseLogRecord> testCasesCanBeUploaded,
            ReportEntity reportEntity, Shell shell) {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            
            TestSuiteLogRecord testSuiteLogRecord = LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity);

            QTestSuite qTestSuite = QTestIntegrationUtil.getSelectedQTestSuite(testSuiteLogRecord);
            
            TestSuiteEntity testSuiteEntity = QTestIntegrationUtil.getTestSuiteEntity(testSuiteLogRecord);

            QTestProject qTestProject = QTestIntegrationUtil.getTestSuiteRepo(testSuiteEntity, projectEntity)
                    .getQTestProject();

            List<QTestLogUploadedPreview> uploadedPreviewLst = new ArrayList<QTestLogUploadedPreview>();

            for (TestCaseLogRecord testCaseLogRecord : testCasesCanBeUploaded) {
                QTestTestCase qTestCase = QTestIntegrationUtil.getQTestCase(testCaseLogRecord);

                QTestLog qTestLog = new QTestLog();
                qTestLog.setAttachmentIncluded(true);
                qTestLog.setMessage(testCaseLogRecord.getMessage());
                qTestLog.setName(testCaseLogRecord.getName());

                QTestRun qTestRun = QTestIntegrationTestSuiteManager.getTestRunByTestSuiteAndTestCaseId(qTestSuite,
                        qTestCase.getId());

                QTestLogUploadedPreview uploadedPreviewItem = new QTestLogUploadedPreview();
                uploadedPreviewItem.setQTestProject(qTestProject);
                uploadedPreviewItem.setQTestSuite(qTestSuite);
                uploadedPreviewItem.setQTestCase(qTestCase);
                uploadedPreviewItem.setQTestLog(qTestLog);
                uploadedPreviewItem.setQTestRun(qTestRun);
                uploadedPreviewItem.setTestLogIndex(QTestIntegrationUtil.getTestCaseLogIndex(testCaseLogRecord,
                        reportEntity));
                uploadedPreviewItem.setTestCaseLogRecord(testCaseLogRecord);

                uploadedPreviewLst.add(uploadedPreviewItem);
            }

            ListReportUploadingPreviewDialog dialog = new ListReportUploadingPreviewDialog(shell, uploadedPreviewLst);
            if (dialog.open() == Dialog.OK) {
                UploadTestCaseResultJob job = new UploadTestCaseResultJob(reportEntity, testSuiteEntity,
                        uploadedPreviewLst, ProjectController.getInstance().getCurrentProject().getFolderLocation());
                job.setUser(true);
                job.schedule();
            }
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.VIEW_MSG_UNABLE_UPLOAD_TEST_RESULT, e.getClass()
                    .getSimpleName());
            LoggerSingleton.logError(e);
        }
    }
}
