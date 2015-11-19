package com.kms.katalon.composer.integration.qtest.handler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Menu;

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

    private boolean canExecute;

    private Thread thread;

    @CanExecute
    public boolean canExecute(final MDirectMenuItem item) {
        if (canExecute) {
            return canExecute;
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String label = item.getLabel();
                try {
                    Object selectedEntity = getFirstSelectedObject(selectionService);

                    if (selectedEntity == null) {
                        return;
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
                            return;
                        }
                        List<Object> children = FolderController.getInstance().getAllDescentdantEntities(folderEntity);
                        for (int i = 0; i < children.size(); i++) {
                            Object childObject = children.get(i);
                            item.setLabel(MessageFormat.format(StringConstants.HDL_LABEL_VALIDATING_REPORT, label, i
                                    * 100 / children.size()));
                            if (!(childObject instanceof ReportEntity)) {
                                continue;
                            }

                            ReportTestCaseLogPair pair = getTestCaseLogPair((ReportEntity) childObject);
                            if (pair != null) {
                                fPairs.add(pair);
                            }
                        }
                    }

                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                } finally {
                    item.setLabel(label);
                    item.setEnabled(fPairs.size() > 0);
                    canExecute = item.isEnabled();
                }
            }
        });
        thread.start();
        MElementContainer<MUIElement> menuImpl = item.getParent();
        Menu menu = (Menu) menuImpl.getWidget();
        menu.addMenuListener(new MenuAdapter() {
            @Override
            public void menuHidden(MenuEvent e) {
                clearData();
            }
        });

        menu.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                clearData();
            }
        });
        return canExecute;
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
        performDisintegrateTestCaseLogs(testCaseLogPairs, true);
    }

    public static void performDisintegrateTestCaseLogs(List<ReportTestCaseLogPair> testCaseLogPairs,
            boolean needConfirmed) {
        if (needConfirmed
                && !MessageDialog.openConfirm(null, StringConstants.CONFIRMATION,
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
                        .post(EventConstants.REPORT_UPDATED, new Object[] { reportEntity.getId(), reportEntity });
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    @PreDestroy
    public void clearData() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            thread = null;
            canExecute = false;
        }
    }
}
