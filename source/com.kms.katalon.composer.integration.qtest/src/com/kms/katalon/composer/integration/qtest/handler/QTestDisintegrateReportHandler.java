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
import com.kms.katalon.composer.integration.qtest.model.ReportTestCaseLogPair;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.entity.QTestReport;

public class QTestDisintegrateReportHandler extends AbstractQTestHandler {
    private List<ReportTestCaseLogPair> fPairs;

    @Inject
    private ESelectionService selectionService;

    @CanExecute
    public boolean canExecute() {
        try {
            Object selectedEntity = getFirstSelectedObject(selectionService);

            if (selectedEntity == null) {
                return false;
            }

            fPairs = new ArrayList<ReportTestCaseLogPair>();
            if (selectedEntity instanceof ReportEntity) {
                ReportTestCaseLogPair pair = getTestCaseLogPair((ReportEntity) selectedEntity);
                if (pair != null) {
                    fPairs.add(pair);
                }
            } else if (selectedEntity instanceof FolderEntity) {
                FolderEntity folderEntity = (FolderEntity) selectedEntity;
                if (!QTestIntegrationUtil.isFolderReportInTestSuiRepo(folderEntity, getProjectEntity())) {
                    return false;
                }

                for (Object childObject : FolderController.getInstance().getAllDescentdantEntities(folderEntity)) {
                    if (!(childObject instanceof ReportEntity)) {
                        continue;
                    }

                    ReportTestCaseLogPair pair = getTestCaseLogPair((ReportEntity) childObject);
                    if (pair != null) {
                        fPairs.add(pair);
                    }
                }
            }

            return fPairs.size() > 0;

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Execute
    public void execute() {
        performDisintegrateTestCaseLogs(fPairs);
    }

    private ReportTestCaseLogPair getTestCaseLogPair(ReportEntity reportEntity) throws Exception {
        TestSuiteLogRecord testSuiteLogRecord = LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity);
        if (testSuiteLogRecord == null) {
            return null;
        }

        List<TestCaseLogRecord> testCasesCanBeDisintegrated = new ArrayList<TestCaseLogRecord>();

        for (ILogRecord selectedTestCaseLogObject : testSuiteLogRecord.getChildRecords()) {
            TestCaseLogRecord testCaseLogRecord = (TestCaseLogRecord) selectedTestCaseLogObject;
            switch (QTestIntegrationUtil.evaluateTestCaseLog(testCaseLogRecord,
                    QTestIntegrationUtil.getSelectedQTestSuite(testSuiteLogRecord), reportEntity)) {
                case INTEGRATED:
                    testCasesCanBeDisintegrated.add(testCaseLogRecord);
                    break;
                default:
                    break;
            }
        }

        if (testCasesCanBeDisintegrated.size() > 0) {
            return new ReportTestCaseLogPair(reportEntity, testCasesCanBeDisintegrated);
        } else {
            return null;
        }
    }

    public static void performDisintegrateTestCaseLogs(List<ReportTestCaseLogPair> testCaseLogPairs) {
        if (!MessageDialog.openConfirm(null, StringConstants.CONFIRMATION,
                StringConstants.DIA_CONFIRM_DISINTEGRATE_TEST_LOGS)) return;
        for (ReportTestCaseLogPair pair : testCaseLogPairs) {
            ReportEntity reportEntity = pair.getReportEntity();
            List<TestCaseLogRecord> testCasesCanBeDisintegrated = pair.getTestCaseLogs();
            try {
                IntegratedEntity reportIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(reportEntity);
                QTestReport qTestReport = QTestIntegrationReportManager
                        .getQTestReportByIntegratedEntity(reportIntegratedEntity);

                for (TestCaseLogRecord testCaseLogRecord : testCasesCanBeDisintegrated) {
                    int index = QTestIntegrationUtil.getTestCaseLogIndex(testCaseLogRecord, reportEntity);
                    qTestReport.getTestLogMap().remove(index);
                }

                reportEntity = QTestIntegrationUtil.saveReportEntity(qTestReport, reportEntity);
                EventBrokerSingleton.getInstance().getEventBroker()
                        .post(EventConstants.REPORT_UPDATED, new Object[] {reportEntity.getId(), reportEntity});
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }
}
