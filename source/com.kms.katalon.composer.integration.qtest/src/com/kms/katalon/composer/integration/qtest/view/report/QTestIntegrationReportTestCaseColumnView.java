package com.kms.katalon.composer.integration.qtest.view.report;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.ImageConstants;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.composer.report.parts.integration.IntegrationTestCaseColumnLabelProvider;
import com.kms.katalon.composer.report.parts.integration.TestCaseIntegrationColumn;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.entity.QTestReport;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;

public class QTestIntegrationReportTestCaseColumnView extends TestCaseIntegrationColumn {

    public QTestIntegrationReportTestCaseColumnView(ReportEntity reportEntity, TestSuiteLogRecord testSuiteLogRecord) {
        super(reportEntity, testSuiteLogRecord);
    }

    @Override
    public ViewerColumn createIntegrationColumn(ColumnViewer tableViewer, int columnIndex) {
        TableViewerColumn tableViewerColumnIntegration = new TableViewerColumn((TableViewer) tableViewer, SWT.NONE);
        TableColumn tblclmnTCIntegration = tableViewerColumnIntegration.getColumn();
        tableViewerColumnIntegration.setLabelProvider(new IntegrationTestCaseColumnLabelProvider(columnIndex) {

            @Override
            protected Image getImage(TestCaseLogRecord element) {
                try {
                    if (reportEntity == null) {
                        return null;
                    }
                    QTestTestCase qTestCase = QTestIntegrationUtil.getQTestCase(element);
                    if (qTestCase == null) {
                        return null;
                    }
                    QTestSuite qTestSuite = QTestIntegrationUtil
                            .getSelectedQTestSuite(LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity));
                    if (qTestSuite == null) {
                        return null;
                    }

                    QTestRun qTestRun = QTestIntegrationTestSuiteManager.getTestRunByTestSuiteAndTestCaseId(qTestSuite,
                            qTestCase.getId());
                    if (qTestRun != null) {
                        QTestReport qTestReport = QTestIntegrationReportManager.getQTestReportByIntegratedEntity(
                                QTestIntegrationUtil.getIntegratedEntity(reportEntity));

                        if (qTestReport != null) {
                            int index = QTestIntegrationUtil.getTestCaseLogIndex(element, reportEntity);
                            if (qTestReport.getTestLogMap().get(index) != null) {
                                return ImageConstants.IMG_16_UPLOADED;
                            }
                        }
                    }
                } catch (Exception e) {
                    return null;
                }
                return ImageConstants.IMG_16_UPLOADING;
            }
        });
        tblclmnTCIntegration.setImage(getProductImage());

        tblclmnTCIntegration.setToolTipText(DocumentationMessageConstants.MSG_CLICK_TO_GO_TO_DOCUMENTATION);
        tblclmnTCIntegration.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openBrowserToLink(DocumentationMessageConstants.REPORT_INTEGRATION_QTEST_TEST_CASE);
            }
        });
        return tableViewerColumnIntegration;
    }

    @Override
    public Image getProductImage() {
        return ImageConstants.IMG_16_QTEST;
    }

}
