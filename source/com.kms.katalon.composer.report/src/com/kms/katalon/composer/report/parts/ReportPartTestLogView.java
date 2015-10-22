package com.kms.katalon.composer.report.parts;

import java.io.File;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.DateUtil;
import com.kms.katalon.composer.components.util.ImageUtil;
import com.kms.katalon.composer.report.constants.ImageConstants;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.composer.report.parts.integration.AbstractReportTestCaseIntegrationView;
import com.kms.katalon.composer.report.provider.ReportPartTestStepLabelProvider;
import com.kms.katalon.composer.report.provider.ReportTestStepTableViewerFilter;
import com.kms.katalon.composer.report.provider.ReportTestStepTreeViewer;
import com.kms.katalon.composer.report.provider.ReportTreeTableContentProvider;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.MessageLogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class ReportPartTestLogView {
    private Button btnFilterTestStepInfo, btnFilterTestStepPassed, btnFilterTestStepFailed, btnFilterTestStepError;
    private Text txtTestLogSearch;
    private CLabel lblTestLogSearch;
    private ReportTestStepTreeViewer treeViewerTestSteps;
    private StyledText txtSTestCaseId, txtSTestCaseStartTime, txtSTestCaseEndTime, txtSTestCaseElapsedTime,
            txtSTestCaseDescription, txtSTestCaseMessage;
    private Composite compositeTestCaseLogIntegration;
    private ToolBar testCaseLogIntegrationToolbar;
    private Composite compositeSTLInformation;
    private StyledText txtSTLStartTime, txtSTLEndTime, txtSTLElapsedTime, txtSTLDescription, txtSTLMessage, txtSTLName;
    private ScrolledComposite compositeSTLSImageView;
    private Canvas selectedTestLogCanvas;

    private Image selectedTestLogImage;
    @SuppressWarnings("unused")
    private StyledText txtSTLStackTrace;

    private ReportPart parentPart;
    private AbstractReportTestCaseIntegrationView selectedReportTestCaseIntegrationView;
    private ToolItem tltmCollapseAllLogs, tltmExpandAllLogs;
    private ReportTestStepTableViewerFilter testStepFilter;
    private boolean isSearching;
    private ToolBar testLogToolbar;
    private CTabFolder tabFolder;
    private CTabItem tbtmTestLog;
    private Composite compositeTestCaseInformation;

    public ReportPartTestLogView(ReportPart parentPart) {
        this.parentPart = parentPart;
        this.isSearching = false;
    }

    public void registerControlModifyListener() {
        compositeTestCaseInformation.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event event) {
                compositeTestCaseInformation.layout(true, true);
            }
        });

        compositeSTLInformation.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event event) {
                compositeSTLInformation.layout(true, true);
            }
        });

        compositeSTLSImageView.addListener(SWT.MouseWheel, new Listener() {
            public void handleEvent(Event event) {
                int wheelCount = event.count;
                wheelCount = (int) Math.ceil(wheelCount / 3.0f);
                while (wheelCount < 0) {
                    compositeSTLSImageView.getVerticalBar().setIncrement(4);
                    wheelCount++;
                }

                while (wheelCount > 0) {
                    compositeSTLSImageView.getVerticalBar().setIncrement(-4);
                    wheelCount--;
                }
            }
        });

        treeViewerTestSteps.addPostSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        tltmCollapseAllLogs.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewerTestSteps.getTree().setRedraw(false);
                treeViewerTestSteps.collapseAll();
                treeViewerTestSteps.getTree().setRedraw(true);
            }
        });

        tltmExpandAllLogs.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewerTestSteps.expandAll();
            }
        });

        btnFilterTestStepInfo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                testStepFilter.setShowInfo(btnFilterTestStepInfo.getSelection());
                treeViewerTestSteps.refresh(true);
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        btnFilterTestStepPassed.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testStepFilter.setShowPassed(btnFilterTestStepPassed.getSelection());
                treeViewerTestSteps.refresh(true);
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        btnFilterTestStepFailed.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testStepFilter.setShowFailed(btnFilterTestStepFailed.getSelection());
                treeViewerTestSteps.refresh(true);
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        btnFilterTestStepError.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testStepFilter.setShowError(btnFilterTestStepError.getSelection());
                treeViewerTestSteps.refresh(true);
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        txtTestLogSearch.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    filterTestStepBySearchedText();
                }
            }
        });

        lblTestLogSearch.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if (isSearching) {
                    txtTestLogSearch.setText("");
                }

                filterTestStepBySearchedText();
            }
        });

        tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (tabFolder.getSelection() == tbtmTestLog) {
                    testLogToolbar.setVisible(true);
                } else {
                    testLogToolbar.setVisible(false);
                }
            }
        });
    }

    private ILogRecord getSelectedTestStep() {
        StructuredSelection selection = (StructuredSelection) treeViewerTestSteps.getSelection();
        if (selection == null || selection.size() != 1) return null;
        return (ILogRecord) selection.getFirstElement();
    }

    private void createCompositeTestLogFilter(Composite compositeTestCaseLogTree) {
        Composite compositeTestLogFilter = new Composite(compositeTestCaseLogTree, SWT.NONE);
        GridLayout glCompositeTestLogFilter = new GridLayout(5, false);
        glCompositeTestLogFilter.marginHeight = 0;
        compositeTestLogFilter.setLayout(glCompositeTestLogFilter);
        compositeTestLogFilter.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        btnFilterTestStepInfo = new Button(compositeTestLogFilter, SWT.CHECK);
        btnFilterTestStepInfo.setText("Info");
        btnFilterTestStepInfo.setImage(ImageConstants.IMG_16_INFO);
        btnFilterTestStepInfo.setSelection(true);

        btnFilterTestStepPassed = new Button(compositeTestLogFilter, SWT.CHECK);
        btnFilterTestStepPassed.setText("Passed");
        btnFilterTestStepPassed.setImage(ImageConstants.IMG_16_PASSED);
        btnFilterTestStepPassed.setSelection(true);

        btnFilterTestStepFailed = new Button(compositeTestLogFilter, SWT.CHECK);
        btnFilterTestStepFailed.setText("Failed");
        btnFilterTestStepFailed.setImage(ImageConstants.IMG_16_FAILED);
        btnFilterTestStepFailed.setSelection(true);

        btnFilterTestStepError = new Button(compositeTestLogFilter, SWT.CHECK);
        btnFilterTestStepError.setText("Error");
        btnFilterTestStepError.setImage(ImageConstants.IMG_16_ERROR);
        btnFilterTestStepError.setSelection(true);

        Composite compositeTestLogSearch = new Composite(compositeTestLogFilter, SWT.BORDER);
        compositeTestLogSearch.setBackground(ColorUtil.getWhiteBackgroundColor());
        compositeTestLogSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glCompositeTestLogSearch = new GridLayout(2, false);
        glCompositeTestLogSearch.marginWidth = 0;
        glCompositeTestLogSearch.marginHeight = 0;
        compositeTestLogSearch.setLayout(glCompositeTestLogSearch);

        txtTestLogSearch = new Text(compositeTestLogSearch, SWT.NONE);
        txtTestLogSearch.setMessage(StringConstants.PA_SEARCH_TEXT_DEFAULT_VALUE);
        GridData gd_txtTestCaseSearch = new GridData(GridData.FILL_HORIZONTAL);
        gd_txtTestCaseSearch.grabExcessVerticalSpace = true;
        gd_txtTestCaseSearch.verticalAlignment = SWT.CENTER;
        txtTestLogSearch.setLayoutData(gd_txtTestCaseSearch);

        Canvas canvasTestLogSearch = new Canvas(compositeTestLogSearch, SWT.NONE);
        canvasTestLogSearch.setLayout(new FillLayout(SWT.HORIZONTAL));

        lblTestLogSearch = new CLabel(canvasTestLogSearch, SWT.NONE);
        lblTestLogSearch.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
        updateStatusSearchLabel();
    }

    private void filterTestStepBySearchedText() {
        if (txtTestLogSearch.getText().isEmpty()) {
            isSearching = false;
        } else {
            isSearching = true;
        }

        treeViewerTestSteps.setSearchedString(txtTestLogSearch.getText());
        treeViewerTestSteps.refresh(true);
        updateStatusSearchLabel();
    }

    private void updateStatusSearchLabel() {
        if (isSearching) {
            lblTestLogSearch
                    .setImage(com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_CLOSE_SEARCH);
            lblTestLogSearch.setToolTipText(GlobalStringConstants.CLEAR);
        } else {
            lblTestLogSearch.setImage(com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_SEARCH);
            lblTestLogSearch.setToolTipText(GlobalStringConstants.SEARCH);
        }
    }

    /**
     * @wbp.parser.entryPoint
     */
    public Composite createCompositeTestStepTree(SashForm sashFormDetails) {
        Composite compositeTestLogTree = new Composite(sashFormDetails, SWT.BORDER);
        GridLayout glCompositeTestLogTree = new GridLayout(1, false);
        glCompositeTestLogTree.marginWidth = 0;
        glCompositeTestLogTree.marginHeight = 0;
        compositeTestLogTree.setLayout(glCompositeTestLogTree);

        Composite compositeTestLogTreeHeader = new Composite(compositeTestLogTree, SWT.NONE);
        compositeTestLogTreeHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeTestLogTreeHeader = new GridLayout(1, false);
        glCompositeTestLogTreeHeader.marginTop = 5;
        compositeTestLogTreeHeader.setLayout(glCompositeTestLogTreeHeader);

        Label lblTestCaseLog = new Label(compositeTestLogTreeHeader, SWT.NONE);
        lblTestCaseLog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblTestCaseLog.setText("Test Case's Log");
        parentPart.setLabelToBeBold(lblTestCaseLog);

        createCompositeTestLogFilter(compositeTestLogTree);

        Composite compositeTestCaseLogTreeDetails = new Composite(compositeTestLogTree, SWT.NONE);
        compositeTestCaseLogTreeDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeTestCaseLogTreeDetails = new GridLayout(1, false);
        glCompositeTestCaseLogTreeDetails.marginWidth = 0;
        glCompositeTestCaseLogTreeDetails.marginHeight = 0;
        compositeTestCaseLogTreeDetails.setLayout(glCompositeTestCaseLogTreeDetails);

        tabFolder = new CTabFolder(compositeTestCaseLogTreeDetails, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
                SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

        createTestLogTabItem(tabFolder);
        createTestCaseInformationTabItem(tabFolder);
        createTestCaseIntegrationTabItem(tabFolder);

        testLogToolbar = new ToolBar(tabFolder, SWT.NONE);
        testLogToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        tltmCollapseAllLogs = new ToolItem(testLogToolbar, SWT.NONE);
        tltmCollapseAllLogs.setToolTipText("Collapse All Logs");
        tltmCollapseAllLogs.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_ELCL_COLLAPSEALL));

        tltmExpandAllLogs = new ToolItem(testLogToolbar, SWT.NONE);
        tltmExpandAllLogs.setToolTipText("Expand All Logs");
        tltmExpandAllLogs.setImage(ImageUtil.loadImage(Platform.getBundle("org.eclipse.ui"),
                "icons/full/elcl16/expandall.png"));

        tabFolder.setTopRight(testLogToolbar);

        return compositeTestLogTree;
    }

    private void createTestLogTabItem(CTabFolder tabFolder) {
        tbtmTestLog = new CTabItem(tabFolder, SWT.NONE);
        tbtmTestLog.setText("Test Log");

        Composite composite = new Composite(tabFolder, SWT.NONE);
        tbtmTestLog.setControl(composite);
        TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
        composite.setLayout(treeColumnLayout);

        treeViewerTestSteps = new ReportTestStepTreeViewer(composite, SWT.FULL_SELECTION);
        Tree treeTestCaseLog = treeViewerTestSteps.getTree();
        treeTestCaseLog.setLinesVisible(true);
        treeTestCaseLog.setHeaderVisible(true);

        TreeViewerColumn treeViewerColumnLogItem = new TreeViewerColumn(treeViewerTestSteps, SWT.NONE);
        TreeColumn trclmnTestLogItem = treeViewerColumnLogItem.getColumn();
        trclmnTestLogItem.setText("Item");
        treeColumnLayout.setColumnData(trclmnTestLogItem, new ColumnWeightData(45, 300));
        treeViewerColumnLogItem.setLabelProvider(new ReportPartTestStepLabelProvider(
                ReportPartTestStepLabelProvider.CLMN_TEST_LOG_ITEM_IDX, this));

        TreeViewerColumn treeViewerColumnLogDescription = new TreeViewerColumn(treeViewerTestSteps, SWT.NONE);
        TreeColumn trclmnTestLogDescription = treeViewerColumnLogDescription.getColumn();
        trclmnTestLogDescription.setText("Description");
        treeColumnLayout.setColumnData(trclmnTestLogDescription, new ColumnWeightData(30, 170));
        treeViewerColumnLogDescription.setLabelProvider(new ReportPartTestStepLabelProvider(
                ReportPartTestStepLabelProvider.CLMN_TEST_LOG_DESCRIPTION_IDX, this));

        TreeViewerColumn treeViewerColumnElapsedTime = new TreeViewerColumn(treeViewerTestSteps, SWT.NONE);
        TreeColumn trclmnTestLogElapsedTime = treeViewerColumnElapsedTime.getColumn();
        trclmnTestLogElapsedTime.setText("Elapsed");
        treeColumnLayout.setColumnData(trclmnTestLogElapsedTime, new ColumnWeightData(0, 80));
        treeViewerColumnElapsedTime.setLabelProvider(new ReportPartTestStepLabelProvider(
                ReportPartTestStepLabelProvider.CLMN_TEST_LOG_ELAPSED_IDX, this));

        TreeViewerColumn treeViewerColumnAttachment = new TreeViewerColumn(treeViewerTestSteps, SWT.NONE);
        TreeColumn trclmnTestLogAttachment = treeViewerColumnAttachment.getColumn();
        trclmnTestLogAttachment.setText("");
        trclmnTestLogAttachment.setImage(ImageConstants.IMG_16_ATTACHMENT);
        treeColumnLayout.setColumnData(trclmnTestLogAttachment, new ColumnWeightData(0, 40));
        treeViewerColumnAttachment.setLabelProvider(new ReportPartTestStepLabelProvider(
                ReportPartTestStepLabelProvider.CLMN_TEST_LOG_ATTACHMENT_IDX, this));

        treeViewerTestSteps.setContentProvider(new ReportTreeTableContentProvider());

        // enable tooltip helper for treeViewerTestCaseLog
        treeViewerTestSteps.getTree().setToolTipText("");
        ColumnViewerToolTipSupport.enableFor(treeViewerTestSteps);

        testStepFilter = new ReportTestStepTableViewerFilter();

        testStepFilter.setShowInfo(btnFilterTestStepInfo.getSelection());
        testStepFilter.setShowPassed(btnFilterTestStepPassed.getSelection());
        testStepFilter.setShowFailed(btnFilterTestStepFailed.getSelection());
        testStepFilter.setShowError(btnFilterTestStepError.getSelection());

        treeViewerTestSteps.addFilter(testStepFilter);

        tabFolder.setSelection(0);
    }

    private void createTestCaseInformationTabItem(CTabFolder tabFolder) {
        CTabItem tbtmGeneralInformation = new CTabItem(tabFolder, SWT.NONE);
        tbtmGeneralInformation.setText("Information");

        compositeTestCaseInformation = new Composite(tabFolder, SWT.NONE);
        tbtmGeneralInformation.setControl(compositeTestCaseInformation);
        compositeTestCaseInformation.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout gl_compositeTestCaseInformation = new GridLayout(6, false);
        gl_compositeTestCaseInformation.verticalSpacing = 7;
        gl_compositeTestCaseInformation.horizontalSpacing = 15;
        compositeTestCaseInformation.setLayout(gl_compositeTestCaseInformation);

        Label lblSTestCaseId = new Label(compositeTestCaseInformation, SWT.NONE);
        lblSTestCaseId.setText("Test Case ID");
        parentPart.setLabelToBeBold(lblSTestCaseId);

        txtSTestCaseId = new StyledText(compositeTestCaseInformation, SWT.NONE);
        txtSTestCaseId.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));

        Label lblSTCStart = new Label(compositeTestCaseInformation, SWT.NONE);
        lblSTCStart.setText("Start");
        parentPart.setLabelToBeBold(lblSTCStart);

        txtSTestCaseStartTime = new StyledText(compositeTestCaseInformation, SWT.READ_ONLY);
        txtSTestCaseStartTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblSTCEndTime = new Label(compositeTestCaseInformation, SWT.NONE);
        lblSTCEndTime.setText("End");
        parentPart.setLabelToBeBold(lblSTCEndTime);

        txtSTestCaseEndTime = new StyledText(compositeTestCaseInformation, SWT.READ_ONLY);
        txtSTestCaseEndTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblSTCElapsedTime = new Label(compositeTestCaseInformation, SWT.NONE);
        lblSTCElapsedTime.setText("Elapsed");
        parentPart.setLabelToBeBold(lblSTCElapsedTime);

        txtSTestCaseElapsedTime = new StyledText(compositeTestCaseInformation, SWT.READ_ONLY);
        txtSTestCaseElapsedTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblSTCDescription = new Label(compositeTestCaseInformation, SWT.NONE);
        lblSTCDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblSTCDescription.setText("Description");
        parentPart.setLabelToBeBold(lblSTCDescription);

        txtSTestCaseDescription = new StyledText(compositeTestCaseInformation, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        txtSTestCaseDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        txtSTestCaseDescription.setMarginColor(ColorUtil.getTextPlaceholderColor());
        enebleMargin(txtSTestCaseDescription, false);

        Label lblSTCMessage = new Label(compositeTestCaseInformation, SWT.NONE);
        lblSTCMessage.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblSTCMessage.setText("Message");
        parentPart.setLabelToBeBold(lblSTCMessage);

        txtSTestCaseMessage = new StyledText(compositeTestCaseInformation, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        txtSTestCaseMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        txtSTestCaseMessage.setMarginColor(ColorUtil.getTextPlaceholderColor());
        enebleMargin(txtSTestCaseMessage, false);
    }

    private void createTestCaseIntegrationTabItem(CTabFolder tabFolder) {
        CTabItem tbtmTestCaseIntegration = new CTabItem(tabFolder, SWT.NONE);
        tbtmTestCaseIntegration.setText("Integration");

        Composite compositeTestCaseIntegration = new Composite(tabFolder, SWT.NONE);
        tbtmTestCaseIntegration.setControl(compositeTestCaseIntegration);
        compositeTestCaseIntegration.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout glCompositeTestCaseIntegration = new GridLayout(2, false);
        glCompositeTestCaseIntegration.horizontalSpacing = 20;
        compositeTestCaseIntegration.setLayout(glCompositeTestCaseIntegration);

        Composite compositeTestCaseIntegrationToolbar = new Composite(compositeTestCaseIntegration, SWT.NONE);
        compositeTestCaseIntegrationToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeTestCaseIntegrationToolbar.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));

        testCaseLogIntegrationToolbar = new ToolBar(compositeTestCaseIntegrationToolbar, SWT.FLAT | SWT.RIGHT);

        compositeTestCaseLogIntegration = new Composite(compositeTestCaseIntegration, SWT.NONE);
        compositeTestCaseLogIntegration.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeTestCaseLogIntegration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    }

    public Composite createCompositeSelectedTestLog(SashForm sashFormDetails) {
        Composite compositeTestCaseSelectedLog = new Composite(sashFormDetails, SWT.BORDER);
        compositeTestCaseSelectedLog.setLayout(new FillLayout(SWT.HORIZONTAL));

        CTabFolder tabFolder = new CTabFolder(compositeTestCaseSelectedLog, SWT.BORDER);

        createSelectedTestStepInformationTabItem(tabFolder);

        // createSelectedTestLogStackTraceTabItem(tabFolder);

        createSelectedTestStepImageViewTabItem(tabFolder);
        return compositeTestCaseSelectedLog;
    }

    private void createSelectedTestStepInformationTabItem(CTabFolder tabFolder) {
        CTabItem tbtmSTLInformation = new CTabItem(tabFolder, SWT.NONE);
        tbtmSTLInformation.setText("Infomation");

        compositeSTLInformation = new Composite(tabFolder, SWT.NONE);
        tbtmSTLInformation.setControl(compositeSTLInformation);
        GridLayout glCompositeSTLInformation = new GridLayout(6, false);
        glCompositeSTLInformation.horizontalSpacing = 15;
        compositeSTLInformation.setLayout(glCompositeSTLInformation);
        compositeSTLInformation.setBackground(ColorUtil.getWhiteBackgroundColor());

        Label lblSTLName = new Label(compositeSTLInformation, SWT.NONE);
        lblSTLName.setText("Name");
        parentPart.setLabelToBeBold(lblSTLName);

        txtSTLName = new StyledText(compositeSTLInformation, SWT.READ_ONLY);
        txtSTLName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));

        Label lblSTLStart = new Label(compositeSTLInformation, SWT.NONE);
        lblSTLStart.setText("Start");
        parentPart.setLabelToBeBold(lblSTLStart);

        txtSTLStartTime = new StyledText(compositeSTLInformation, SWT.READ_ONLY);
        txtSTLStartTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblSTLEndTime = new Label(compositeSTLInformation, SWT.NONE);
        lblSTLEndTime.setText("End");
        parentPart.setLabelToBeBold(lblSTLEndTime);

        txtSTLEndTime = new StyledText(compositeSTLInformation, SWT.READ_ONLY);
        txtSTLEndTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblSTLElapsedTime = new Label(compositeSTLInformation, SWT.NONE);
        lblSTLElapsedTime.setText("Elapsed");
        parentPart.setLabelToBeBold(lblSTLElapsedTime);

        txtSTLElapsedTime = new StyledText(compositeSTLInformation, SWT.READ_ONLY);
        txtSTLElapsedTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblSTLDescription = new Label(compositeSTLInformation, SWT.NONE);
        lblSTLDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblSTLDescription.setText("Description");
        parentPart.setLabelToBeBold(lblSTLDescription);

        txtSTLDescription = new StyledText(compositeSTLInformation, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        txtSTLDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        txtSTLDescription.setMarginColor(ColorUtil.getTextPlaceholderColor());
        enebleMargin(txtSTLDescription, false);

        Label lblSTLMessage = new Label(compositeSTLInformation, SWT.NONE);
        lblSTLMessage.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblSTLMessage.setText("Message");
        parentPart.setLabelToBeBold(lblSTLMessage);

        txtSTLMessage = new StyledText(compositeSTLInformation, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        txtSTLMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        txtSTLMessage.setMarginColor(ColorUtil.getTextPlaceholderColor());
        enebleMargin(txtSTLMessage, false);

        tabFolder.setSelection(0);
    }

    @SuppressWarnings("unused")
    private void createSelectedTestStepStackTraceTabItem(CTabFolder tabFolder) {
        CTabItem tbtmSTLStackStrace = new CTabItem(tabFolder, SWT.NONE);
        tbtmSTLStackStrace.setText("Stack Trace");

        Composite compositeSTLStackTrace = new Composite(tabFolder, SWT.NONE);
        tbtmSTLStackStrace.setControl(compositeSTLStackTrace);
        compositeSTLStackTrace.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeSTLStackTrace.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtSTLStackTrace = new StyledText(compositeSTLStackTrace, SWT.NONE);
    }

    private void createSelectedTestStepImageViewTabItem(CTabFolder tabFolder) {
        CTabItem tbtmSTLImageView = new CTabItem(tabFolder, SWT.NONE);
        tbtmSTLImageView.setText("Image");

        compositeSTLSImageView = new ScrolledComposite(tabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
        compositeSTLSImageView.setExpandVertical(true);
        compositeSTLSImageView.setExpandHorizontal(true);

        tbtmSTLImageView.setControl(compositeSTLSImageView);
        compositeSTLSImageView.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeSTLSImageView.setBackground(ColorUtil.getWhiteBackgroundColor());

        selectedTestLogCanvas = new Canvas(compositeSTLSImageView, SWT.NONE);
        selectedTestLogCanvas.setBackground(ColorUtil.getWhiteBackgroundColor());
        selectedTestLogCanvas.setLayout(new FillLayout(SWT.HORIZONTAL));

        selectedTestLogCanvas.addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {
                if (selectedTestLogImage != null && !selectedTestLogImage.isDisposed()) {
                    e.gc.drawImage(selectedTestLogImage, 0, 0);
                }
            }
        });

        compositeSTLSImageView.setContent(selectedTestLogCanvas);
    }

    public void loadTestCaseIntegrationToolbar(ReportEntity report, TestSuiteLogRecord testSuiteLogRecord) {
        clearTestCaseIntegrationToolbar();

        for (Entry<String, AbstractReportTestCaseIntegrationView> builderEntry : parentPart
                .getIntegratingCompositeMap().entrySet()) {
            ToolItem item = new ToolItem(testCaseLogIntegrationToolbar, SWT.CHECK);
            item.setText(builderEntry.getKey());
        }

        for (ToolItem item : testCaseLogIntegrationToolbar.getItems()) {
            item.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    ToolItem toolItem = (ToolItem) e.getSource();
                    if (toolItem.getSelection()) {
                        changeTestCaseIntegrationContainer(toolItem.getText());
                    } else {
                        clearTestCaseIntegrationContainer();
                    }
                }

            });
        }

        if (testCaseLogIntegrationToolbar.getItems().length > 0) {
            testCaseLogIntegrationToolbar.getItems()[0].setSelection(true);
            changeTestCaseIntegrationContainer(testCaseLogIntegrationToolbar.getItems()[0].getText());
        }
    }

    private void changeTestCaseIntegrationContainer(String productName) {
        clearTestCaseIntegrationContainer();

        selectedReportTestCaseIntegrationView = parentPart.getIntegratingCompositeMap().get(productName);

        selectedReportTestCaseIntegrationView.createContainer(compositeTestCaseLogIntegration);

        compositeTestCaseLogIntegration.layout(true, true);
        selectedReportTestCaseIntegrationView.changeTestCase((TestCaseLogRecord) parentPart
                .getSelectedTestCaseLogRecord());
    }

    private void clearTestCaseIntegrationContainer() {
        while (compositeTestCaseLogIntegration.getChildren().length > 0) {
            compositeTestCaseLogIntegration.getChildren()[0].dispose();
        }
    }

    private void clearTestCaseIntegrationToolbar() {
        while (testCaseLogIntegrationToolbar.getItems().length > 0) {
            testCaseLogIntegrationToolbar.getItems()[0].dispose();
        }
    }

    public void updateSelectedTestCase(ILogRecord selectedLogRecord) {
        parentPart.clearMouseDownListener(txtSTestCaseId);
        if (selectedLogRecord != null && selectedLogRecord.getId() != null) {
            txtSTestCaseId.setText(selectedLogRecord.getId());
            clickToOpenTestCaseListener(txtSTestCaseId);
        } else {
            txtSTestCaseId.setText("");
        }

        if (selectedLogRecord != null && selectedLogRecord.getStartTime() > 0) {
            txtSTestCaseStartTime.setText(DateUtil.getDateTimeFormatted(selectedLogRecord.getStartTime()));
        } else {
            txtSTestCaseStartTime.setText("");
        }

        if (selectedLogRecord != null && selectedLogRecord.getEndTime() > 0) {
            txtSTestCaseEndTime.setText(DateUtil.getDateTimeFormatted(selectedLogRecord.getEndTime()));
        } else {
            txtSTestCaseEndTime.setText("");
        }

        if (selectedLogRecord != null && selectedLogRecord.getStartTime() > 0 && selectedLogRecord.getEndTime() > 0) {
            txtSTestCaseElapsedTime.setText(DateUtil.getElapsedTime(selectedLogRecord.getStartTime(),
                    selectedLogRecord.getEndTime()));
            StyledString txtSTLElapsedStyleString = new StyledString(txtSTestCaseElapsedTime.getText(),
                    StyledString.COUNTER_STYLER);
            txtSTestCaseElapsedTime.setStyleRanges(txtSTLElapsedStyleString.getStyleRanges());
        } else {
            txtSTestCaseElapsedTime.setText("");
        }

        if (selectedLogRecord != null && selectedLogRecord.getDescription() != null
                && !selectedLogRecord.getDescription().isEmpty()) {
            txtSTestCaseDescription.setText(selectedLogRecord.getDescription());
            enebleMargin(txtSTestCaseDescription, true);
        } else {
            txtSTestCaseDescription.setText("");
            enebleMargin(txtSTestCaseDescription, false);
        }

        if (selectedLogRecord != null && selectedLogRecord.getMessage() != null) {
            txtSTestCaseMessage.setText(selectedLogRecord.getMessage());
            enebleMargin(txtSTestCaseMessage, true);
        } else {
            txtSTestCaseMessage.setText("");
            enebleMargin(txtSTestCaseMessage, false);
        }

        compositeTestCaseInformation.layout();

        if (selectedLogRecord != null) {
            treeViewerTestSteps.setInput(selectedLogRecord.getChildRecords());
        } else {
            treeViewerTestSteps.setInput(null);
        }
        selectedReportTestCaseIntegrationView.changeTestCase((TestCaseLogRecord) parentPart
                .getSelectedTestCaseLogRecord());
        updateSelectedTestStep(null);
    }

    private void clickToOpenTestCaseListener(StyledText styleText) {
        StyleRange range = new StyleRange();
        range.start = 0;
        range.length = styleText.getText().length();
        range.underline = true;
        range.data = styleText.getText();
        range.underlineStyle = SWT.UNDERLINE_LINK;

        styleText.setStyleRanges(new StyleRange[] { range });

        styleText.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                try {
                    StyledText styleText = (StyledText) event.widget;
                    int offset = styleText.getOffsetAtLocation(new Point(event.x, event.y));
                    StyleRange style = styleText.getStyleRangeAtOffset(offset);
                    if (style != null && style.underline && style.underlineStyle == SWT.UNDERLINE_LINK) {
                        TestCaseEntity testCaseEntity = TestCaseController.getInstance().getTestCaseByDisplayId(
                                (String) style.data);
                        if (testCaseEntity != null) {
                            EventBrokerSingleton.getInstance().getEventBroker()
                                    .post(EventConstants.TESTCASE_OPEN, testCaseEntity);
                        } else {
                            MessageDialog.openWarning(null, StringConstants.WARN, "Test case not found.");
                        }
                    }

                } catch (IllegalArgumentException e) {
                    // no character under event.x, event.y
                } catch (Exception e) {
                    MessageDialog.openWarning(null, StringConstants.WARN, "Test case not found.");
                }
            }
        });
    }

    public void updateSelectedTestStep(ILogRecord selectedLogRecord) {
        parentPart.clearMouseDownListener(txtSTLName);
        if (selectedLogRecord != null && selectedLogRecord.getName() != null) {
            txtSTLName.setText(selectedLogRecord.getName());
            if (selectedLogRecord instanceof TestCaseLogRecord) {
                clickToOpenTestCaseListener(txtSTLName);
            }
        } else {
            txtSTLName.setText("");
        }

        if (selectedLogRecord != null && selectedLogRecord.getStartTime() > 0) {
            txtSTLStartTime.setText(DateUtil.getDateTimeFormatted(selectedLogRecord.getStartTime()));
        } else {
            txtSTLStartTime.setText("");
        }

        if (selectedLogRecord != null && selectedLogRecord.getEndTime() > 0) {
            txtSTLEndTime.setText(DateUtil.getDateTimeFormatted(selectedLogRecord.getEndTime()));
        } else {
            txtSTLEndTime.setText("");
        }

        if (selectedLogRecord != null && selectedLogRecord.getStartTime() > 0 && selectedLogRecord.getEndTime() > 0) {
            txtSTLElapsedTime.setText(DateUtil.getElapsedTime(selectedLogRecord.getStartTime(),
                    selectedLogRecord.getEndTime()));
            StyledString txtSTLElapsedStyleString = new StyledString(txtSTLElapsedTime.getText(),
                    StyledString.COUNTER_STYLER);
            txtSTLElapsedTime.setStyleRanges(txtSTLElapsedStyleString.getStyleRanges());
        } else {
            txtSTLElapsedTime.setText("");
        }

        if (selectedLogRecord != null && StringUtils.isNotEmpty(selectedLogRecord.getDescription())) {
            txtSTLDescription.setText(selectedLogRecord.getDescription());
            enebleMargin(txtSTLDescription, true);
        } else {
            txtSTLDescription.setText("");
            enebleMargin(txtSTLDescription, false);
        }

        if (selectedLogRecord != null && StringUtils.isNotEmpty(selectedLogRecord.getMessage())) {
            txtSTLMessage.setText(selectedLogRecord.getMessage());
            enebleMargin(txtSTLMessage, true);
        } else {
            txtSTLMessage.setText("");
            enebleMargin(txtSTLMessage, false);
        }

        compositeSTLInformation.layout();

        if (selectedTestLogImage != null) {
            selectedTestLogImage.dispose();
        }

        if (selectedLogRecord != null && selectedLogRecord instanceof MessageLogRecord) {
            MessageLogRecord messageLog = (MessageLogRecord) selectedLogRecord;
            if (messageLog.getAttachment() != null) {

                selectedTestLogImage = new Image(selectedTestLogCanvas.getDisplay(), getReport().getLocation()
                        + File.separator + messageLog.getAttachment());
                compositeSTLSImageView.setMinSize(new Point(selectedTestLogImage.getBounds().width,
                        selectedTestLogImage.getBounds().height));

            }
        } else {
            compositeSTLSImageView.setMinSize(0, 0);
        }

        selectedTestLogCanvas.getParent().layout(true, true);
        selectedTestLogCanvas.redraw();

    }

    public ReportEntity getReport() {
        return parentPart.getReport();
    }

    public void enebleMargin(StyledText styledText, boolean enable) {
        styledText.setRedraw(false);
        if (enable) {
            styledText.getVerticalBar().setVisible(true);
            styledText.setTopMargin(1);
            styledText.setBottomMargin(1);
            styledText.setLeftMargin(1);
        } else {
            styledText.getVerticalBar().setVisible(false);
            styledText.setTopMargin(0);
            styledText.setBottomMargin(0);
            styledText.setLeftMargin(0);
        }
        styledText.setRedraw(true);
        styledText.layout(true);
    }

}
