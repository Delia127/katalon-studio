package com.kms.katalon.composer.integration.qtest.job;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.entity.QTestLog;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;

public class UploadTestCaseResultJob extends UploadJob {

    private List<QTestLogUploadedPreview> uploadedPreviewLst;
    private String projectDir;
    private ReportEntity reportEntity;
    private TestSuiteEntity testSuiteEntity;

    public UploadTestCaseResultJob(ReportEntity reportEntity, TestSuiteEntity testSuiteEntity,
            List<QTestLogUploadedPreview> uploadedPreviewLst, String projectDir) {
        super(StringConstants.JOB_TITLE_UPLOAD_TEST_RESULT);
        setUploadedPreviewLst(uploadedPreviewLst);
        setProjectDir(projectDir);
        setReportEntity(reportEntity);
        setTestSuiteEntity(testSuiteEntity);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(StringConstants.JOB_TASK_UPLOAD_TEST_RESULT, uploadedPreviewLst.size());
        try {
            for (QTestLogUploadedPreview uploadedItem : uploadedPreviewLst) {
                if (monitor.isCanceled()) break;

                monitor.subTask(MessageFormat.format(StringConstants.JOB_SUB_TASK_UPLOAD_TEST_RESULT,
                        getWrappedName(uploadedItem.getTestCaseLogRecord().getName())));
                IntegratedEntity testSuiteIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(testSuiteEntity);

                List<QTestSuite> qTestSuiteCollection = QTestIntegrationTestSuiteManager
                        .getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity);

                QTestRun qTestRun = uploadedItem.getQTestRun();
                if (qTestRun == null) {
                    try {
                        qTestRun = QTestIntegrationTestSuiteManager.uploadTestCaseInTestSuite(
                                uploadedItem.getQTestCase(), uploadedItem.getQTestSuite(),
                                uploadedItem.getQTestProject(), projectDir);

                        QTestIntegrationUtil.addNewTestRunToTestSuite(testSuiteEntity, testSuiteIntegratedEntity,
                                uploadedItem.getQTestSuite(), qTestRun, qTestSuiteCollection);

                        // update test run for the uploaded item
                        uploadedItem.setQTestRun(qTestRun);
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                        monitor.setCanceled(true);
                        return Status.CANCEL_STATUS;
                    }
                }

                try {
                    QTestLog qTestCaseLog = QTestIntegrationReportManager.uploadTestLog(projectDir, uploadedItem,
                            QTestIntegrationUtil.getTempDirPath(), new File(reportEntity.getLocation()));

                    uploadedItem.setQTestLog(qTestCaseLog);

                    QTestIntegrationUtil.saveReportEntity(reportEntity, uploadedItem);
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    monitor.setCanceled(true);
                    return Status.CANCEL_STATUS;
                }

                monitor.worked(1);
            }
            return Status.OK_STATUS;
        } catch (QTestInvalidFormatException ex) {
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
            uploadedPreviewLst = null;
            EventBrokerSingleton.getInstance().getEventBroker()
                    .post(EventConstants.REPORT_UPDATED, reportEntity.getId());
        }

    }

    private void setUploadedPreviewLst(List<QTestLogUploadedPreview> uploadedPreviewLst) {
        this.uploadedPreviewLst = uploadedPreviewLst;
    }

    private void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }

    public void setReportEntity(ReportEntity reportEntity) {
        this.reportEntity = reportEntity;
    }

    public void setTestSuiteEntity(TestSuiteEntity testSuiteEntity) {
        this.testSuiteEntity = testSuiteEntity;
    }

}
