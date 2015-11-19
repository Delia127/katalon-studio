package com.kms.katalon.composer.integration.qtest.handler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.job.UploadTestCaseResultJob;
import com.kms.katalon.composer.integration.qtest.model.ReportTestCaseLogPair;
import com.kms.katalon.composer.integration.qtest.model.ReportUploadedPreviewPair;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.folder.FolderEntity;
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
    private List<ReportTestCaseLogPair> fPairs;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell activeShell;

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
                    item.setIconURI(null);
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
        performUploadTestCaseLogs(fPairs, activeShell);
    }

    public static void performUploadTestCaseLogs(List<ReportTestCaseLogPair> testCaseLogPairs, Shell shell) {
        List<ReportUploadedPreviewPair> uploadedPairs = new ArrayList<ReportUploadedPreviewPair>();
        for (ReportTestCaseLogPair testCaseLogPair : testCaseLogPairs) {
            ReportUploadedPreviewPair uploadedPair = transform(testCaseLogPair, shell);
            if (uploadedPair != null) {
                uploadedPairs.add(uploadedPair);
            }
        }
        UploadTestCaseResultJob job = new UploadTestCaseResultJob(uploadedPairs);
        job.setUser(true);
        job.schedule();
    }

    private ReportTestCaseLogPair getTestCaseLogPair(ReportEntity reportEntity) throws Exception {
        TestSuiteLogRecord testSuiteLogRecord = LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity);
        if (testSuiteLogRecord == null) {
            return null;
        }

        if (QTestIntegrationUtil.getSelectedQTestSuite(testSuiteLogRecord) == null) {
            return null;
        }

        List<TestCaseLogRecord> testCasesCanBeUploaded = new ArrayList<TestCaseLogRecord>();

        for (ILogRecord selectedTestCaseLogObject : testSuiteLogRecord.getChildRecords()) {

            TestCaseLogRecord testCaseLogRecord = (TestCaseLogRecord) selectedTestCaseLogObject;
            switch (QTestIntegrationUtil.evaluateTestCaseLog(testCaseLogRecord,
                    QTestIntegrationUtil.getSelectedQTestSuite(testSuiteLogRecord), reportEntity)) {
                case CAN_INTEGRATE:
                    testCasesCanBeUploaded.add(testCaseLogRecord);
                    break;
                default:
                    break;
            }
        }

        if (testCasesCanBeUploaded.size() > 0) {
            return new ReportTestCaseLogPair(reportEntity, testCasesCanBeUploaded);
        } else {
            return null;
        }
    }

    /**
     * Uploads the given <code>testCasesCanBeUploaded</code> of the given <code>reportEntity</code> to qTest server by
     * using {@link UploadTestCaseResultJob}.
     * 
     * @param testCasesCanBeUploaded
     * @param reportEntity
     * @param shell
     *            used when system needs to open a warning dialog.
     */
    public static ReportUploadedPreviewPair transform(ReportTestCaseLogPair testCaseLogPair, Shell shell) {
        try {
            ReportEntity reportEntity = testCaseLogPair.getReportEntity();
            List<TestCaseLogRecord> testCasesCanBeUploaded = testCaseLogPair.getTestCaseLogs();
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

            TestSuiteLogRecord testSuiteLogRecord = LogRecordLookup.getInstance().getTestSuiteLogRecord(
                    testCaseLogPair.getReportEntity());

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

            return new ReportUploadedPreviewPair(reportEntity, uploadedPreviewLst);
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.VIEW_MSG_UNABLE_UPLOAD_TEST_RESULT, e.getClass()
                    .getSimpleName());
            LoggerSingleton.logError(e);
            return null;
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
