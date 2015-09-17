package com.kms.katalon.composer.integration.qtest.view.report;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.dialog.ListReportUploadingPreviewDialog;
import com.kms.katalon.composer.integration.qtest.jobs.UploadTestCaseResultJob;
import com.kms.katalon.composer.integration.qtest.model.QTestLogEvaluation;
import com.kms.katalon.composer.integration.qtest.view.testsuite.providers.QTestSuiteTableLabelProvider;
import com.kms.katalon.composer.report.parts.integration.AbstractReportTestCaseIntegrationView;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestCaseManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.entity.QTestLog;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestReport;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;

public class QTestIntegrationReportTestCaseView extends AbstractReportTestCaseIntegrationView {
    private StyledText txtQTestId;
    private StyledText txtQTestName;
    private StyledText txtQTestTestCaseId;
    private StyledText txtTestCaseRunId;

    private TestSuiteEntity testSuiteEntity;
    private TestSuiteLogRecord testSuiteLogRecord;
    private QTestTestCase qTestCase;
    private QTestSuite qTestSuite;
    private QTestRun qTestRun;
    private StyledText txtAttachment;
    private TestCaseLogRecord testCaseLogRecord;
    private QTestReport qTestReport;
    private QTestLog qTestCaseLog;

    public QTestIntegrationReportTestCaseView(ReportEntity reportEntity, TestSuiteLogRecord testSuiteLogRecord) {
        super(reportEntity);
        this.testSuiteLogRecord = testSuiteLogRecord;
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public Composite createContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout(1, false);
        glContainer.marginWidth = 0;
        glContainer.marginHeight = 0;
        container.setLayout(glContainer);

        Composite compositeInfo = new Composite(container, SWT.NONE);
        GridLayout glCompositeInfo = new GridLayout(2, false);
        glCompositeInfo.verticalSpacing = 10;
        glCompositeInfo.horizontalSpacing = 25;
        compositeInfo.setLayout(glCompositeInfo);
        compositeInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Label lblQTestId = new Label(compositeInfo, SWT.NONE);
        lblQTestId.setText("QTest ID");

        txtQTestId = new StyledText(compositeInfo, SWT.READ_ONLY);
        txtQTestId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblQTestName = new Label(compositeInfo, SWT.NONE);
        lblQTestName.setText("QTest Name");

        txtQTestName = new StyledText(compositeInfo, SWT.READ_ONLY);
        txtQTestName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblTestCaseId = new Label(compositeInfo, SWT.NONE);
        lblTestCaseId.setText("Test Case ID");

        txtQTestTestCaseId = new StyledText(compositeInfo, SWT.READ_ONLY);
        txtQTestTestCaseId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblTestRunId = new Label(compositeInfo, SWT.NONE);
        lblTestRunId.setText("Test Run ID");

        txtTestCaseRunId = new StyledText(compositeInfo, SWT.READ_ONLY);
        txtTestCaseRunId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblAttachment = new Label(compositeInfo, SWT.NONE);
        lblAttachment.setText("Attachment");

        txtAttachment = new StyledText(compositeInfo, SWT.NONE);

        intialize();

        return container;
    }

    private void intialize() {
        // TODO Auto-generated method stub
        try {
            qTestSuite = getQTestSuite(testSuiteLogRecord);
            reloadView();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private QTestSuite getQTestSuite(TestSuiteLogRecord testSuiteLogRecord) {
        try {
            if (testSuiteLogRecord != null) {
                testSuiteEntity = TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteLogRecord.getId(),
                        ProjectController.getInstance().getCurrentProject());
                if (testSuiteEntity != null
                        && testSuiteEntity.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME) != null) {
                    List<QTestSuite> qTestSuiteCollection = QTestIntegrationTestSuiteManager
                            .getQTestSuiteListByIntegratedEntity(testSuiteEntity
                                    .getIntegratedEntity(QTestStringConstants.PRODUCT_NAME));
                    return qTestSuite = QTestIntegrationTestSuiteManager
                            .getSelectedQTestSuiteByIntegratedEntity(qTestSuiteCollection);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    private void reloadView() {
        // TODO Auto-generated method stub
        try {
            txtQTestId.setText("");
            txtQTestName.setText("");
            txtAttachment.setText("");
            txtQTestTestCaseId.setText("");
            txtTestCaseRunId.setText("");
            clearMouseDownListener(txtQTestId);

            if (qTestCase == null || qTestSuite == null) {
                return;
            }

            txtQTestTestCaseId.setText(Long.toString(qTestCase.getId()));

            qTestRun = QTestIntegrationTestSuiteManager.getTestRunByTestSuiteAndTestCaseId(qTestSuite,
                    qTestCase.getId());

            if (qTestRun != null) {
                txtTestCaseRunId.setText(Long.toString(qTestRun.getId()));
            }

            IntegratedEntity reportIntegratedEntity = reportEntity
                    .getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);
            qTestReport = QTestIntegrationReportManager.getQTestReportByIntegratedEntity(reportIntegratedEntity);

            if (qTestReport != null && testCaseLogRecord != null) {

                qTestCaseLog = qTestReport.getTestLogMap().get(getTestCaseLogIndex(testCaseLogRecord));
                if (qTestCaseLog != null) {
                    txtQTestId.setText(Long.toString(qTestCaseLog.getId()));
                    txtQTestName.setText(qTestCaseLog.getName());
                    registerTxtQTestIdClickListener();

                    String attachmentString = qTestCaseLog.isAttachmentIncluded() ? "Yes" : "No";
                    txtAttachment.setText(attachmentString);
                    return;
                }
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public void clearMouseDownListener(StyledText styleText) {
        while (styleText.getListeners(SWT.MouseDown).length > 1) {
            styleText.removeListener(SWT.MouseDown,
                    styleText.getListeners(SWT.MouseDown)[styleText.getListeners(SWT.MouseDown).length - 1]);
        }
    }

    private void registerTxtQTestIdClickListener() {
        StyleRange range = new StyleRange();
        range.start = 0;
        range.length = txtQTestId.getText().length();
        range.underline = true;
        range.data = txtQTestId.getText();
        range.underlineStyle = SWT.UNDERLINE_LINK;

        txtQTestId.setStyleRanges(new StyleRange[] { range });

        txtQTestId.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                try {
                    int offset = txtQTestId.getOffsetAtLocation(new Point(event.x, event.y));
                    StyleRange style = txtQTestId.getStyleRangeAtOffset(offset);
                    if (style != null && style.underline && style.underlineStyle == SWT.UNDERLINE_LINK) {
                        navigateToTestCaseLog();
                    }

                } catch (IllegalArgumentException e) {
                    // no character under event.x, event.y
                }
            }
        });
    }

    private void navigateToTestCaseLog() {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            String projectDir = projectEntity.getFolderLocation();
            QTestProject qTestProject = QTestIntegrationUtil.getTestSuiteRepo(testSuiteEntity, projectEntity)
                    .getQTestProject();
            URL url = QTestIntegrationReportManager.getTestLogURL(projectDir, qTestProject, qTestRun, qTestCaseLog);
            IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
            browser.openURL(url);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void saveReportEntity() {
        try {
            IntegratedEntity reportIntegratedEntity = QTestIntegrationReportManager
                    .getIntegratedEntityByQTestReport(qTestReport);
            reportEntity = (ReportEntity) QTestIntegrationUtil.updateFileIntegratedEntity(reportEntity,
                    reportIntegratedEntity);
            ReportController.getInstance().updateReport(reportEntity);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

    }

    private int getTestCaseLogIndex(TestCaseLogRecord testCaseLogRecord) {
        if (testCaseLogRecord == null || testSuiteLogRecord == null) return -1;
        return Arrays.asList(testSuiteLogRecord.getChildRecords()).indexOf(testCaseLogRecord);
    }

    @Override
    public void changeTestCase(TestCaseLogRecord testCaseLogRecord) {
        this.testCaseLogRecord = testCaseLogRecord;
        qTestCase = getQTestCase(testCaseLogRecord);
        reloadView();
    }

    public QTestTestCase getQTestCase(TestCaseLogRecord testCaseLogRecord) {
        try {
            TestCaseEntity testCaseEntity = TestCaseController.getInstance().getTestCaseByDisplayId(
                    testCaseLogRecord.getId());
            if (testCaseEntity != null) {
                return QTestIntegrationTestCaseManager.getQTestTestCaseByIntegratedEntity(testCaseEntity
                        .getIntegratedEntity(QTestStringConstants.PRODUCT_NAME));
            }
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public Image getImage(TestCaseLogRecord testCaseLogRecord) {
        try {
            QTestTestCase qTestCase = getQTestCase(testCaseLogRecord);
            if (qTestCase == null) return null;

            QTestSuite qTestSuite = getQTestSuite(testSuiteLogRecord);
            if (qTestSuite == null) return null;

            QTestRun qTestRun = QTestIntegrationTestSuiteManager.getTestRunByTestSuiteAndTestCaseId(qTestSuite,
                    qTestCase.getId());
            if (qTestRun != null) {
                IntegratedEntity reportIntegratedEntity = reportEntity
                        .getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);
                QTestReport qTestReport = QTestIntegrationReportManager
                        .getQTestReportByIntegratedEntity(reportIntegratedEntity);

                if (qTestReport != null) {
                    int index = getTestCaseLogIndex(testCaseLogRecord);
                    if (qTestReport.getTestLogMap().get(index) != null) {
                        return QTestSuiteTableLabelProvider.IMG_UPLOADED;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return QTestSuiteTableLabelProvider.IMG_UPLOADING;
    }

    private QTestLogEvaluation evaluateTestCaseLog(TestCaseLogRecord testCaseLogRecord, QTestSuite qTestSuite) {
        QTestTestCase qTestCase = getQTestCase(testCaseLogRecord);
        if (qTestCase == null) return QTestLogEvaluation.CANNOT_INTEGRATE;

        QTestRun qTestRun = QTestIntegrationTestSuiteManager.getTestRunByTestSuiteAndTestCaseId(qTestSuite,
                qTestCase.getId());
        if (qTestRun != null) {
            IntegratedEntity reportIntegratedEntity = reportEntity
                    .getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);
            QTestReport qTestReport;
            try {
                qTestReport = QTestIntegrationReportManager.getQTestReportByIntegratedEntity(reportIntegratedEntity);
            } catch (Exception e) {
                return QTestLogEvaluation.CANNOT_INTEGRATE;
            }

            if (qTestReport != null) {
                int index = getTestCaseLogIndex(testCaseLogRecord);
                if (qTestReport.getTestLogMap().get(index) != null) {
                    return QTestLogEvaluation.INTEGRATED;
                }
            }
        }
        return QTestLogEvaluation.CAN_INTEGRATE;
    }

    public void createTableContextMenu(Menu parentMenu, ISelection selection) {
        if (selection == null) return;

        List<TestCaseLogRecord> testCaseCanBeUploaded = new ArrayList<TestCaseLogRecord>();
        List<TestCaseLogRecord> testCaseCanBeDisintegrated = new ArrayList<TestCaseLogRecord>();

        QTestSuite qTestSuite = getQTestSuite(testSuiteLogRecord);
        if (qTestSuite == null) return;

        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        for (Object selectedTestCaseLogObject : structuredSelection.toArray()) {
            TestCaseLogRecord testCaseLogRecord = (TestCaseLogRecord) selectedTestCaseLogObject;
            switch (evaluateTestCaseLog(testCaseLogRecord, qTestSuite)) {
                case CAN_INTEGRATE:
                    testCaseCanBeUploaded.add(testCaseLogRecord);
                    break;
                case INTEGRATED:
                    testCaseCanBeDisintegrated.add(testCaseLogRecord);
                    break;
                default:
                    break;
            }
        }

        if (testCaseCanBeUploaded.isEmpty() && testCaseCanBeDisintegrated.isEmpty()) return;

        MenuItem qTestMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
        qTestMenuItem.setText(QTestStringConstants.PRODUCT_NAME);

        Menu qTestMenu = new Menu(parentMenu);
        qTestMenuItem.setMenu(qTestMenu);

        if (testCaseCanBeUploaded.size() > 0) {
            MenuItem uploadMenuItem = new MenuItem(qTestMenu, SWT.NONE);
            uploadMenuItem.setText("Upload");
            uploadMenuItem.setData(testCaseCanBeUploaded);

            uploadMenuItem.addSelectionListener(new SelectionAdapter() {
                @SuppressWarnings("unchecked")
                @Override
                public void widgetSelected(SelectionEvent e) {
                    // TODO Auto-generated method stub
                    MenuItem menuItem = (MenuItem) e.getSource();
                    performUploadTestCaseLogs(menuItem, (List<TestCaseLogRecord>) menuItem.getData());
                }
            });
        }

        if (testCaseCanBeDisintegrated.size() > 0) {
            MenuItem disintegrateMenuItem = new MenuItem(qTestMenu, SWT.NONE);
            disintegrateMenuItem.setText("Disintegrate");

            disintegrateMenuItem.setData(testCaseCanBeDisintegrated);

            disintegrateMenuItem.addSelectionListener(new SelectionAdapter() {
                @SuppressWarnings("unchecked")
                @Override
                public void widgetSelected(SelectionEvent e) {
                    MenuItem menuItem = (MenuItem) e.getSource();
                    performDisintegrateTestCaseLogs(menuItem, (List<TestCaseLogRecord>) menuItem.getData());
                }
            });
        }
    }

    private void performDisintegrateTestCaseLogs(MenuItem menuItem, List<TestCaseLogRecord> testCaseCanBeDisintegrated) {
        for (TestCaseLogRecord testCaseLogRecord : testCaseCanBeDisintegrated) {
            int index = getTestCaseLogIndex(testCaseLogRecord);
            qTestReport.getTestLogMap().remove(index);
        }
        saveReportEntity();
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.REPORT_UPDATED, reportEntity.getId());
    }

    private void performUploadTestCaseLogs(MenuItem menuItem, List<TestCaseLogRecord> testCaseCanBeUploaded) {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            QTestSuite qTestSuite = getQTestSuite(testSuiteLogRecord);
            QTestProject qTestProject = QTestIntegrationUtil.getTestSuiteRepo(testSuiteEntity, projectEntity)
                    .getQTestProject();

            List<QTestLogUploadedPreview> uploadedPreviewLst = new ArrayList<QTestLogUploadedPreview>();

            for (TestCaseLogRecord testCaseLogRecord : testCaseCanBeUploaded) {
                QTestTestCase qTestCase = getQTestCase(testCaseLogRecord);

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
                uploadedPreviewItem.setTestLogIndex(getTestCaseLogIndex(testCaseLogRecord));
                uploadedPreviewItem.setTestCaseLogRecord(testCaseLogRecord);

                uploadedPreviewLst.add(uploadedPreviewItem);
            }

            ListReportUploadingPreviewDialog dialog = new ListReportUploadingPreviewDialog(menuItem.getDisplay()
                    .getActiveShell(), uploadedPreviewLst);
            if (dialog.open() == Dialog.OK) {
                UploadTestCaseResultJob job = new UploadTestCaseResultJob(reportEntity, testSuiteEntity,
                        uploadedPreviewLst, ProjectController.getInstance().getCurrentProject().getFolderLocation());
                job.setUser(true);
                job.schedule();
            }
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, "Unable to upload test case's result.", e.getClass()
                    .getSimpleName());
            LoggerSingleton.logError(e);
        }

    }

}
