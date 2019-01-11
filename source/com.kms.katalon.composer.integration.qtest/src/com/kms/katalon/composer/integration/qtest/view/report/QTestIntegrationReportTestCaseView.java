package com.kms.katalon.composer.integration.qtest.view.report;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.handler.QTestDisintegrateReportHandler;
import com.kms.katalon.composer.integration.qtest.handler.QTestUploadReportHandler;
import com.kms.katalon.composer.integration.qtest.model.ReportTestCaseLogPair;
import com.kms.katalon.composer.report.parts.integration.TestCaseLogDetailsIntegrationView;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.entity.QTestLog;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestReport;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;

public class QTestIntegrationReportTestCaseView extends TestCaseLogDetailsIntegrationView {
    private StyledText txtTestLogId;

    private StyledText txtTestCaseRunAlias;

    private StyledText txtAttachment;

    private TestSuiteLogRecord testSuiteLogRecord;

    private QTestTestCase qTestCase;

    private QTestSuite qTestSuite;

    private QTestRun qTestRun;

    private TestCaseLogRecord testCaseLogRecord;

    private QTestLog qTestCaseLog;

    public QTestIntegrationReportTestCaseView(ReportEntity reportEntity, TestSuiteLogRecord testSuiteLogRecord) {
        super(reportEntity, testSuiteLogRecord);
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

        Label lblTestRunAlias = new Label(compositeInfo, SWT.NONE);
        lblTestRunAlias.setText(StringConstants.VIEW_TITLE_TEST_RUN_ALIAS);

        txtTestCaseRunAlias = new StyledText(compositeInfo, SWT.READ_ONLY);
        txtTestCaseRunAlias.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblTestLogId = new Label(compositeInfo, SWT.NONE);
        lblTestLogId.setText(StringConstants.VIEW_TITLE_TEST_LOG_ID);

        txtTestLogId = new StyledText(compositeInfo, SWT.READ_ONLY);
        txtTestLogId.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblAttachment = new Label(compositeInfo, SWT.NONE);
        lblAttachment.setText(StringConstants.ATTACHMENT);

        txtAttachment = new StyledText(compositeInfo, SWT.NONE);
        txtAttachment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        intialize();

        return container;
    }

    private void intialize() {
        try {
            qTestSuite = QTestIntegrationUtil.getSelectedQTestSuite(testSuiteLogRecord);
            reloadView();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void reloadView() {
        try {
            txtTestLogId.setText("");
            txtAttachment.setText("");
            txtTestCaseRunAlias.setText("");
            clearMouseDownListener(txtTestLogId);

            if (qTestCase == null || qTestSuite == null) {
                return;
            }

            qTestRun = QTestIntegrationTestSuiteManager.getTestRunByTestSuiteAndTestCaseId(qTestSuite,
                    qTestCase.getId());

            if (qTestRun == null) {
                return;
            }

            QTestReport qTestReport = QTestIntegrationReportManager
                    .getQTestReportByIntegratedEntity(QTestIntegrationUtil.getIntegratedEntity(reportEntity));

            if (qTestReport != null && testCaseLogRecord != null) {

                qTestCaseLog = qTestReport.getTestLogMap()
                        .get(QTestIntegrationUtil.getTestCaseLogIndex(testCaseLogRecord, reportEntity));
                if (qTestCaseLog != null) {
                    txtTestLogId.setText(Long.toString(qTestCaseLog.getId()));
                    registerTxtQTestIdClickListener();

                    txtTestCaseRunAlias.setText(qTestRun.getPid());

                    String attachmentString = qTestCaseLog.isAttachmentIncluded() ? StringConstants.CM_YES
                            : StringConstants.CM_NO;
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
        range.length = txtTestLogId.getText().length();
        range.underline = true;
        range.data = txtTestLogId.getText();
        range.underlineStyle = SWT.UNDERLINE_LINK;

        txtTestLogId.setStyleRanges(new StyleRange[] { range });

        txtTestLogId.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                try {
                    int offset = txtTestLogId.getOffsetAtLocation(new Point(event.x, event.y));
                    StyleRange style = txtTestLogId.getStyleRangeAtOffset(offset);
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

            QTestProject qTestProject = QTestIntegrationUtil
                    .getTestSuiteRepo(QTestIntegrationUtil.getTestSuiteEntity(testSuiteLogRecord), projectEntity)
                    .getQTestProject();

            URL url = QTestIntegrationReportManager.getTestLogURL(projectDir, qTestProject, qTestRun, qTestCaseLog);

            Program.launch(url.toString());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public void changeTestCase(TestCaseLogRecord testCaseLogRecord) {
        if (testCaseLogRecord == null) {
            return;
        }

        this.testCaseLogRecord = testCaseLogRecord;
        qTestCase = QTestIntegrationUtil.getQTestCase(testCaseLogRecord);
        reloadView();
    }

    public void createTableContextMenu(Menu parentMenu, ISelection selection) {
        if (selection == null) {
            return;
        }

        List<TestCaseLogRecord> testCaseCanBeUploaded = new ArrayList<TestCaseLogRecord>();
        List<TestCaseLogRecord> testCaseCanBeDisintegrated = new ArrayList<TestCaseLogRecord>();

        QTestSuite qTestSuite = QTestIntegrationUtil.getSelectedQTestSuite(testSuiteLogRecord);
        if (qTestSuite == null) {
            return;
        }

        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        for (Object selectedTestCaseLogObject : structuredSelection.toArray()) {
            TestCaseLogRecord testCaseLogRecord = (TestCaseLogRecord) selectedTestCaseLogObject;
            switch (QTestIntegrationUtil.evaluateTestCaseLog(testCaseLogRecord, qTestSuite, reportEntity)) {
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

        // Returns if there is nothing to do.
        if (testCaseCanBeUploaded.isEmpty() && testCaseCanBeDisintegrated.isEmpty()) {
            return;
        }

        MenuItem qTestMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
        qTestMenuItem.setText(QTestStringConstants.PRODUCT_NAME);

        Menu qTestMenu = new Menu(parentMenu);
        qTestMenuItem.setMenu(qTestMenu);

        if (testCaseCanBeUploaded.size() > 0) {
            MenuItem uploadMenuItem = new MenuItem(qTestMenu, SWT.NONE);
            uploadMenuItem.setText(StringConstants.CM_UPLOAD);
            uploadMenuItem.setData(testCaseCanBeUploaded);

            uploadMenuItem.addSelectionListener(new SelectionAdapter() {
                @SuppressWarnings("unchecked")
                @Override
                public void widgetSelected(SelectionEvent e) {
                    MenuItem menuItem = (MenuItem) e.getSource();
                    performUploadTestCaseLogs(menuItem, (List<TestCaseLogRecord>) menuItem.getData());
                }
            });
        }

        if (testCaseCanBeDisintegrated.size() > 0) {
            MenuItem disintegrateMenuItem = new MenuItem(qTestMenu, SWT.NONE);
            disintegrateMenuItem.setText(StringConstants.CM_DISINTEGRATE);

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

    private void performDisintegrateTestCaseLogs(MenuItem menuItem,
            List<TestCaseLogRecord> testCasesCanBeDisintegrated) {
        List<ReportTestCaseLogPair> testCaseLogPairs = new ArrayList<ReportTestCaseLogPair>();
        ReportTestCaseLogPair pair = new ReportTestCaseLogPair(reportEntity, testCasesCanBeDisintegrated);
        testCaseLogPairs.add(pair);
        QTestDisintegrateReportHandler.performDisintegrateTestCaseLogs(testCaseLogPairs);
    }

    private void performUploadTestCaseLogs(MenuItem menuItem, List<TestCaseLogRecord> testCasesCanBeUploaded) {
        List<ReportTestCaseLogPair> testCaseLogPairs = new ArrayList<ReportTestCaseLogPair>();
        ReportTestCaseLogPair pair = new ReportTestCaseLogPair(reportEntity, testCasesCanBeUploaded);
        testCaseLogPairs.add(pair);
        QTestUploadReportHandler.performUploadTestCaseLogs(testCaseLogPairs, menuItem.getDisplay().getActiveShell());
    }

}
