package com.kms.katalon.composer.integration.qtest.job;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.SynchronizedConfirmationDialog;
import com.kms.katalon.composer.components.impl.dialogs.YesNoAllOptions;
import com.kms.katalon.composer.components.impl.util.StatusUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.handler.QTestDisintegrateReportHandler;
import com.kms.katalon.composer.integration.qtest.model.ReportTestCaseLogPair;
import com.kms.katalon.composer.integration.qtest.model.ReportUploadedPreviewPair;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.entity.QTestLog;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.setting.QTestSettingCredential;

public class UploadTestCaseResultJob extends QTestJob {

    private List<ReportUploadedPreviewPair> fUnuploadedPairs;

    private List<ReportTestCaseLogPair> fUploadedTestLogPairs;

    public UploadTestCaseResultJob(List<ReportUploadedPreviewPair> uploadedPairs) {
        super(StringConstants.JOB_TITLE_UPLOAD_TEST_RESULT);
        fUnuploadedPairs = uploadedPairs;
        fUploadedTestLogPairs = new ArrayList<ReportTestCaseLogPair>();
    }

    /**
     * Uploads all members of <code>fUnuploadedPairs</code>
     * 
     * @return {@link Status#OK_STATUS} if all items uploaded. Otherwise, false.
     */
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            int total = fUnuploadedPairs.size();
            monitor.beginTask(StringConstants.JOB_TASK_UPLOAD_TEST_RESULT, total);
            for (ReportUploadedPreviewPair pair : fUnuploadedPairs) {
                if (monitor.isCanceled()) {
                    return canceled();
                }
                IStatus childStatus = uploadPair(new SubProgressMonitor(monitor, 1,
                        SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK), pair);
                if (childStatus == Status.CANCEL_STATUS) {
                    return canceled();
                } else if (childStatus.getSeverity() == Status.ERROR) {
                    return childStatus;
                }
            }
            return Status.OK_STATUS;
        } finally {
            monitor.done();
        }
    }

    /**
     * Cancels the uploading progress. If there is any uploaded items, opens a confirmation to let user keep them or
     * not.
     * 
     * @return {@link Status#CANCEL_STATUS}
     */
    private IStatus canceled() {
        final int uploadedCount = fUploadedTestLogPairs.size();
        if (uploadedCount == 0) {
            return Status.CANCEL_STATUS;
        }

        SynchronizedConfirmationDialog dialog = new SynchronizedConfirmationDialog() {
            @Override
            public void run() {
                boolean confirmed = MessageDialog.open(MessageDialog.QUESTION, null, StringConstants.CONFIRMATION,
                        MessageFormat.format(StringConstants.JOB_MSG_CONFIRM_CANCEL_UPLOAD, uploadedCount), SWT.NONE);
                setConfirmedValue(confirmed ? YesNoAllOptions.YES : YesNoAllOptions.NO);
            }
        };
        UISynchronizeService.getInstance().getSync().syncExec(dialog);
        if (dialog.getConfirmedValue() == YesNoAllOptions.NO) {
            QTestDisintegrateReportHandler.performDisintegrateTestCaseLogs(fUploadedTestLogPairs, false);
        }
        return Status.CANCEL_STATUS;
    }

    /**
     * Performs "Upload" for the given <code>pair</code> to qTest.
     * 
     * @param monitor
     * @param pair
     * @return {@link Status#OK_STATUS} if all {@link QTestLogUploadedPreview} can be uploaded successfully. Otherwise,
     *         {@link Status#CANCEL_STATUS}.
     */
    private IStatus uploadPair(IProgressMonitor monitor, ReportUploadedPreviewPair pair) {
        ReportEntity reportEntity = pair.getReportEntity();
        List<QTestLogUploadedPreview> uploadedPreviewLst = pair.getTestLogs();
        List<TestCaseLogRecord> uploadedTestCaseLogs = new ArrayList<TestCaseLogRecord>();
        try {
            TestSuiteEntity testSuiteEntity = ReportController.getInstance().getTestSuiteByReport(reportEntity);

            monitor.beginTask(StringConstants.JOB_TASK_UPLOAD_TEST_RESULT, uploadedPreviewLst.size());
            for (QTestLogUploadedPreview uploadedItem : uploadedPreviewLst) {
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
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
                                    uploadedItem.getQTestProject(),
                                    QTestSettingCredential.getCredential(getProjectDir()));

                            // update test run for the uploaded item
                            uploadedItem.setQTestRun(qTestRun);
                        } catch (Exception e) {
                            monitor.setCanceled(true);
                            LoggerSingleton.logError(e);
                            return StatusUtil.getErrorStatus(getClass(), e);
                        }
                    } else {
                        qTestRun.setQTestCaseId(uploadedItem.getQTestCase().getId());
                    }

                    // Save test suite.
                    testSuiteEntity = QTestIntegrationUtil.addNewTestRunToTestSuite(testSuiteEntity, testSuiteIntegratedEntity,
                            uploadedItem.getQTestSuite(), qTestRun, qTestSuiteCollection);

                    uploadedItem.setQTestRun(qTestRun);
                }

                try {
                    QTestLog qTestCaseLog = QTestIntegrationReportManager.uploadTestLog(getProjectDir(), uploadedItem,
                            QTestIntegrationUtil.getTempDirPath(),
                            LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity));

                    uploadedItem.setQTestLog(qTestCaseLog);

                    QTestIntegrationUtil.saveReportEntity(reportEntity, uploadedItem);
                    uploadedTestCaseLogs.add(uploadedItem.getTestCaseLogRecord());
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    return StatusUtil.getErrorStatus(getClass(), e);
                }
                monitor.worked(1);
            }
            return Status.OK_STATUS;
        } catch (OperationCanceledException ex) {
            return Status.CANCEL_STATUS;
        } catch (Exception ex) {
            monitor.setCanceled(true);
            LoggerSingleton.logError(ex);
            return StatusUtil.getErrorStatus(getClass(), ex);
        } finally {
            ReportTestCaseLogPair uploadedPair = new ReportTestCaseLogPair(reportEntity, uploadedTestCaseLogs);
            fUploadedTestLogPairs.add(uploadedPair);
            monitor.done();
            EventBrokerSingleton.getInstance().getEventBroker()
                    .post(EventConstants.REPORT_UPDATED, new Object[] { reportEntity.getId(), reportEntity });
        }
    }

    /**
     * Checks if there is any a {@link QTestRun} that represents for of the given <code>uploadedPreviewItem</code>
     * occurs in the given <code>currentQTestRunsOnQTest</code> or not.
     * 
     * @param uploadedPreviewItem
     *            the item that represents for a {@link TestCaseLogRecord} that will be uploaded.
     * @param currentQTestRunsOnQTest
     *            collection of {@link QTestRun} that is updated from qTest server.
     * @return a {@link QTestRun} if existed. Otherwise, null.
     */
    private QTestRun getQTestRun(QTestLogUploadedPreview uploadedPreviewItem, List<QTestRun> currentQTestRunsOnQTest) {
        for (QTestRun currentQTestRun : currentQTestRunsOnQTest) {
            if (uploadedPreviewItem.getQTestCase().getId() == currentQTestRun.getQTestCaseId()) {
                return currentQTestRun;
            }
        }
        return null;
    }

}
