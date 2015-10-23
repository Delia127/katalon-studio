package com.kms.katalon.composer.integration.qtest.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.entity.QTestReport;

public class QTestDisintegrateReportHandler extends AbstractQTestHandler {

    private ReportEntity fReportEntity;
    private List<TestCaseLogRecord> fTestCasesCanBeDisintegrated;

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

            fTestCasesCanBeDisintegrated = new ArrayList<TestCaseLogRecord>();

            for (ILogRecord selectedTestCaseLogObject : testSuiteLogRecord.getChildRecords()) {
                TestCaseLogRecord testCaseLogRecord = (TestCaseLogRecord) selectedTestCaseLogObject;
                switch (QTestIntegrationUtil.evaluateTestCaseLog(testCaseLogRecord,
                        QTestIntegrationUtil.getSelectedQTestSuite(testSuiteLogRecord), fReportEntity)) {
                    case INTEGRATED:
                        fTestCasesCanBeDisintegrated.add(testCaseLogRecord);
                        break;
                    default:
                        break;
                }
            }

            if (fTestCasesCanBeDisintegrated.size() > 0) {
                return true;
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Execute
    public void execute() {
        performDisintegrateTestCaseLogs(fTestCasesCanBeDisintegrated, fReportEntity);
    }

    public static void performDisintegrateTestCaseLogs(List<TestCaseLogRecord> testCasesCanBeDisintegrated,
            ReportEntity reportEntity) {
        try {
            if (MessageDialog.openConfirm(null, StringConstants.CONFIRMATION,
                    StringConstants.DIA_CONFIRM_DISINTEGRATE_TEST_LOGS)) {

                IntegratedEntity reportIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(reportEntity);
                QTestReport qTestReport = QTestIntegrationReportManager
                        .getQTestReportByIntegratedEntity(reportIntegratedEntity);

                for (TestCaseLogRecord testCaseLogRecord : testCasesCanBeDisintegrated) {
                    int index = QTestIntegrationUtil.getTestCaseLogIndex(testCaseLogRecord, reportEntity);
                    qTestReport.getTestLogMap().remove(index);
                }

                reportEntity = QTestIntegrationUtil.saveReportEntity(qTestReport, reportEntity);
                EventBrokerSingleton.getInstance().getEventBroker()
                        .post(EventConstants.REPORT_UPDATED, reportEntity.getId());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
