package com.kms.katalon.composer.integration.qtest.job;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.model.ReportUploadedPreviewPair;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.entity.QTestLog;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;

public class UploadTestCaseResultJob extends UploadJob {

    private List<ReportUploadedPreviewPair> fPairs;

    public UploadTestCaseResultJob(List<ReportUploadedPreviewPair> uploadedPairs) {
        super(StringConstants.JOB_TITLE_UPLOAD_TEST_RESULT);
        fPairs = uploadedPairs;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            int total = fPairs.size();
            monitor.beginTask(StringConstants.JOB_TASK_UPLOAD_TEST_RESULT, total);
            for (ReportUploadedPreviewPair pair : fPairs) {
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }
                IStatus childStatus = uploadPair(new SubProgressMonitor(monitor, 1,
                        SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK), pair);
                if (childStatus == Status.CANCEL_STATUS) {
                    return Status.CANCEL_STATUS;
                }
            }
            return Status.OK_STATUS;
        } finally {
            monitor.done();
        }
    }

    private IStatus uploadPair(IProgressMonitor monitor, ReportUploadedPreviewPair pair) {
        ReportEntity reportEntity = pair.getReportEntity();
        List<QTestLogUploadedPreview> uploadedPreviewLst = pair.getTestLogs();
        try {
            TestSuiteEntity testSuiteEntity = ReportController.getInstance().getTestSuiteByReport(reportEntity);

            monitor.beginTask(StringConstants.JOB_TASK_UPLOAD_TEST_RESULT, uploadedPreviewLst.size());
            for (QTestLogUploadedPreview uploadedItem : uploadedPreviewLst) {
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                monitor.subTask(MessageFormat.format(StringConstants.JOB_SUB_TASK_UPLOAD_TEST_RESULT,
                        getWrappedName(uploadedItem.getTestCaseLogRecord().getName())));
                IntegratedEntity testSuiteIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(testSuiteEntity);

                List<QTestSuite> qTestSuiteCollection = QTestIntegrationTestSuiteManager
                        .getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity);

                QTestRun qTestRun = uploadedItem.getQTestRun();

                if (qTestRun == null) {
                    // Check in the current list first
                    qTestRun = getQTestRun(uploadedItem,
                            QTestIntegrationUtil.getCurrentQTestRuns(testSuiteEntity, projectEntity));

                    // If qTestRun isn't in the current list, upload new to qTest.
                    if (qTestRun == null) {
                        try {
                            qTestRun = QTestIntegrationTestSuiteManager.uploadTestCaseInTestSuite(
                                    uploadedItem.getQTestCase(), uploadedItem.getQTestSuite(),
                                    uploadedItem.getQTestProject(), getProjectDir());

                            // update test run for the uploaded item
                            uploadedItem.setQTestRun(qTestRun);
                        } catch (Exception e) {
                            LoggerSingleton.logError(e);
                            monitor.setCanceled(true);
                            return Status.CANCEL_STATUS;
                        }
                    } else {
                        qTestRun.setQTestCaseId(uploadedItem.getQTestCase().getId());
                    }

                    // Save test suite.
                    QTestIntegrationUtil.addNewTestRunToTestSuite(testSuiteEntity, testSuiteIntegratedEntity,
                            uploadedItem.getQTestSuite(), qTestRun, qTestSuiteCollection);
                }

                try {
                    QTestLog qTestCaseLog = QTestIntegrationReportManager.uploadTestLog(getProjectDir(), uploadedItem,
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
        } catch (Exception ex) {
            monitor.setCanceled(true);
            LoggerSingleton.logError(ex);
            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
            EventBrokerSingleton.getInstance().getEventBroker()
                    .post(EventConstants.REPORT_UPDATED, new Object[] { reportEntity.getId(), reportEntity });
        }
    }

    private QTestRun getQTestRun(QTestLogUploadedPreview uploadedPreviewItem, List<QTestRun> currentQTestRunsOnQTest) {
        for (QTestRun currentQTestRun : currentQTestRunsOnQTest) {
            if (uploadedPreviewItem.getQTestCase().getId() == currentQTestRun.getQTestCaseId()) {
                return currentQTestRun;
            }
        }
        return null;
    }

}
