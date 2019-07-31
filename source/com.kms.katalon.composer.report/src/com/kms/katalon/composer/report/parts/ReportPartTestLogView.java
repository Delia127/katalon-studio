package com.kms.katalon.composer.report.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.control.StyledTextMessage;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ImageUtil;
import com.kms.katalon.composer.report.constants.ComposerReportMessageConstants;
import com.kms.katalon.composer.report.constants.ImageConstants;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.composer.report.dialog.AdvancedSearchTestLogDialog;
import com.kms.katalon.composer.report.integration.ReportComposerIntegrationFactory;
import com.kms.katalon.composer.report.parts.integration.ReportTestCaseIntegrationViewBuilder;
import com.kms.katalon.composer.report.parts.integration.TestCaseChangedEventListener;
import com.kms.katalon.composer.report.parts.integration.TestCaseLogDetailsIntegrationView;
import com.kms.katalon.composer.report.parts.integration.TestLogIntegrationColumn;
import com.kms.katalon.composer.report.provider.ReportPartTestStepLabelProvider;
import com.kms.katalon.composer.report.provider.ReportTestStepTableViewerFilter;
import com.kms.katalon.composer.report.provider.ReportTestStepTreeViewer;
import com.kms.katalon.composer.report.provider.ReportTreeTableContentProvider;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.MessageLogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.util.internal.DateUtil;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class ReportPartTestLogView {

    private static final int IMAGE_VIEW_TAB_ITEM_IDX = 1;

    private Button btnFilterTestStepInfo, btnFilterTestStepPassed, btnFilterTestStepFailed, btnFilterTestStepError,
            btnFilterTestStepIncomplete, btnFilterTestStepWarning, btnFilterTestStepNotRun;

    private StyledText txtTestLogSearch;

    private CLabel lblTestLogSearch, lblTestLogAdvancedSearch;

    private ReportTestStepTreeViewer treeViewerTestSteps;

    private StyledText txtSTestCaseId, txtSTestCaseStartTime, txtSTestCaseEndTime, txtSTestCaseElapsedTime,
            txtSTestCaseDescription, txtSTestCaseMessage;

    private Composite compositeTestCaseLogIntegration;

    private ToolBar testCaseLogIntegrationToolbar;

    private Composite compositeSTLInformation;

    private StyledText txtSTLStartTime, txtSTLEndTime, txtSTLElapsedTime, txtSTLDescription, txtSTLMessage, txtSTLName;

    private ScrolledComposite compositeSTLSImageView;

    private Canvas selectedTestLogCanvas;

    private Image selectedTestLogImage, drawnImage;

    @SuppressWarnings("unused")
    private StyledText txtSTLStackTrace;

    private ReportPart parentPart;

    private TestCaseLogDetailsIntegrationView selectedReportTestCaseIntegrationView;

    private ToolItem tltmCollapseAllLogs, tltmExpandAllLogs;

    private ReportTestStepTableViewerFilter testStepFilter;

    private boolean isSearching;

    private ToolBar testLogToolbar;

    private CTabFolder tabFolder;

    private CTabItem tbtmTestLog;

    private Composite compositeTestCaseInformation;

    private List<TestCaseChangedEventListener> testCaseChangedEventListeners;

    private ToolItem tltmResetImageSize;

    private ToolItem tltmFitScreen;

    private CTabFolder selectedTestLogTabFolder;

    private ToolBar imageToolbar;

    private ImageScreenMode imageScreenMode = ImageScreenMode.FIT_SCREEN;

    public ReportPartTestLogView(ReportPart parentPart) {
        this.parentPart = parentPart;
        this.isSearching = false;
        testCaseChangedEventListeners = new ArrayList<>();
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

        treeViewerTestSteps.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                TreeViewer viewer = (TreeViewer) event.getViewer();
                IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
                Object selectedNode = thisSelection.getFirstElement();
                viewer.setExpandedState(selectedNode, !viewer.getExpandedState(selectedNode));
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
                testStepFilter.showInfo(btnFilterTestStepInfo.getSelection());
                treeViewerTestSteps.refresh(true);
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        btnFilterTestStepPassed.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testStepFilter.showPassed(btnFilterTestStepPassed.getSelection());
                treeViewerTestSteps.refresh(true);
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        btnFilterTestStepFailed.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testStepFilter.showFailed(btnFilterTestStepFailed.getSelection());
                treeViewerTestSteps.refresh(true);
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        btnFilterTestStepError.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testStepFilter.showError(btnFilterTestStepError.getSelection());
                treeViewerTestSteps.refresh(true);
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        btnFilterTestStepIncomplete.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testStepFilter.showIncomplete(btnFilterTestStepIncomplete.getSelection());
                treeViewerTestSteps.refresh(true);
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        btnFilterTestStepWarning.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                testStepFilter.showWarning(btnFilterTestStepWarning.getSelection());
                treeViewerTestSteps.refresh(true);
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        btnFilterTestStepNotRun.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                testStepFilter.showNotRun(btnFilterTestStepNotRun.getSelection());
                treeViewerTestSteps.refresh(true);
                updateSelectedTestStep(getSelectedTestStep());
            }
        });

        txtTestLogSearch.addKeyListener(new KeyAdapter() {
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

        lblTestLogAdvancedSearch.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                Shell shell = new Shell(lblTestLogAdvancedSearch.getShell().getDisplay());
                Point pt = lblTestLogAdvancedSearch.toDisplay(1, 1);
                shell.setSize(0, 0);
                AdvancedSearchTestLogDialog dialog = new AdvancedSearchTestLogDialog(shell);
                shell.setLocation(pt.x - shell.getBounds().width - 52, pt.y + shell.getBounds().height + 52);

                if (dialog.open() == Dialog.OK && isSearching) {
                    treeViewerTestSteps.refresh(false);
                }
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

        tltmFitScreen.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                imageScreenMode = ImageScreenMode.FIT_SCREEN;
                drawImage();
            }
        });

        tltmResetImageSize.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                imageScreenMode = ImageScreenMode.FULL_SIZE;
                tltmFitScreen.setSelection(false);
                drawImage();
            }
        });

        selectedTestLogTabFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                imageToolbar.setVisible(selectedTestLogTabFolder.getSelectionIndex() == IMAGE_VIEW_TAB_ITEM_IDX);
            }
        });
    }

    private void drawImage() {
        if (drawnImage == null) {
            return;
        }
        switch (imageScreenMode) {
            case FIT_SCREEN: {
                tltmFitScreen.setSelection(true);
                tltmResetImageSize.setSelection(false);
                Rectangle bounds = compositeSTLSImageView.getClientArea();
                drawnImage = ImageUtil.resize(selectedTestLogImage, bounds.width, bounds.height);

                compositeSTLSImageView
                        .setMinSize(new Point(drawnImage.getBounds().width, drawnImage.getBounds().height));
                selectedTestLogCanvas.redraw();
                break;
            }
            case FULL_SIZE: {
                tltmFitScreen.setSelection(false);
                tltmResetImageSize.setSelection(true);

                drawnImage = selectedTestLogImage;

                compositeSTLSImageView
                        .setMinSize(new Point(drawnImage.getBounds().width, drawnImage.getBounds().height));
                selectedTestLogCanvas.redraw();

                break;
            }
        }
    }

    private ILogRecord getSelectedTestStep() {
        StructuredSelection selection = (StructuredSelection) treeViewerTestSteps.getSelection();
        if (selection == null || selection.size() != 1)
            return null;
        return (ILogRecord) selection.getFirstElement();
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

        Composite compositeTestCaseLogTreeDetails = new Composite(compositeTestLogTree, SWT.NONE);
        compositeTestCaseLogTreeDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeTestCaseLogTreeDetails = new GridLayout(1, false);
        glCompositeTestCaseLogTreeDetails.marginWidth = 0;
        glCompositeTestCaseLogTreeDetails.marginHeight = 0;
        compositeTestCaseLogTreeDetails.setLayout(glCompositeTestCaseLogTreeDetails);

        tabFolder = new CTabFolder(compositeTestCaseLogTreeDetails, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tabFolder.setSelectionBackground(
                Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

        createTestLogTabItem(tabFolder);
        createTestCaseInformationTabItem(tabFolder);
        createTestCaseIntegrationTabItem(tabFolder);

        return compositeTestLogTree;
    }

    private Button createFilteringButton(Composite parent, String name, Image image, boolean defaultSeletion) {
        Button filteringButton = new Button(parent, SWT.CHECK);
        filteringButton.setText(StringUtils.defaultIfEmpty(name, StringUtils.EMPTY));
        if (image != null) {
            filteringButton.setImage(image);
        }
        filteringButton.setSelection(defaultSeletion);

        return filteringButton;
    }

    private void createCompositeFilterTestLog(Composite parent) {
        Composite compositeTestLogFilter = new Composite(parent, SWT.NONE);
        compositeTestLogFilter.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeTestLogFilter = new GridLayout(2, false);
        glCompositeTestLogFilter.marginWidth = 0;
        glCompositeTestLogFilter.marginHeight = 0;
        compositeTestLogFilter.setLayout(glCompositeTestLogFilter);

        Composite compositeFilterDetails = new Composite(compositeTestLogFilter, SWT.NONE);
        compositeFilterDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
        compositeFilterDetails.setLayout(new GridLayout(7, false));

        btnFilterTestStepInfo = createFilteringButton(compositeFilterDetails, StringConstants.INFO,
                ImageConstants.IMG_16_INFO, true);
        btnFilterTestStepPassed = createFilteringButton(compositeFilterDetails, StringConstants.PASSED,
                ImageConstants.IMG_16_PASSED, true);
        btnFilterTestStepFailed = createFilteringButton(compositeFilterDetails, StringConstants.FAILED,
                ImageConstants.IMG_16_FAILED, true);
        btnFilterTestStepError = createFilteringButton(compositeFilterDetails, StringConstants.ERROR,
                ImageConstants.IMG_16_ERROR, true);
        btnFilterTestStepIncomplete = createFilteringButton(compositeFilterDetails, StringConstants.INCOMPLETE,
                ImageConstants.IMG_16_INCOMPLETE, true);
        btnFilterTestStepWarning = createFilteringButton(compositeFilterDetails, StringConstants.WARN,
                ImageConstants.IMG_16_WARNING, true);
        btnFilterTestStepNotRun = createFilteringButton(compositeFilterDetails, StringConstants.NOT_RUN,
                ImageConstants.IMG_16_NOT_RUN, true);

        Composite compositeTestLogSearch = new Composite(compositeTestLogFilter, SWT.BORDER);
        compositeTestLogSearch.setBackground(ColorUtil.getWhiteBackgroundColor());
        compositeTestLogSearch.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeTestLogSearch = new GridLayout(3, false);
        glCompositeTestLogSearch.verticalSpacing = 0;
        glCompositeTestLogSearch.horizontalSpacing = 0;
        glCompositeTestLogSearch.marginWidth = 0;
        glCompositeTestLogSearch.marginHeight = 0;
        compositeTestLogSearch.setLayout(glCompositeTestLogSearch);

        txtTestLogSearch = new StyledText(compositeTestLogSearch, SWT.SINGLE);
        GridData gdTxtTestCaseSearch = new GridData(GridData.FILL_HORIZONTAL);
        gdTxtTestCaseSearch.grabExcessVerticalSpace = true;
        gdTxtTestCaseSearch.verticalAlignment = SWT.CENTER;
        txtTestLogSearch.setLayoutData(gdTxtTestCaseSearch);

        StyledTextMessage styledTextMessage = new StyledTextMessage(txtTestLogSearch);
        styledTextMessage.setMessage(StringConstants.PA_SEARCH_TEXT_DEFAULT_VALUE);

        Canvas cvsTestLogSearch = new Canvas(compositeTestLogSearch, SWT.NONE);
        GridLayout glCvsTestLogSearch = new GridLayout(3, false);
        glCvsTestLogSearch.horizontalSpacing = 0;
        glCvsTestLogSearch.marginWidth = 0;
        glCvsTestLogSearch.marginHeight = 0;
        cvsTestLogSearch.setLayout(glCvsTestLogSearch);

        lblTestLogSearch = new CLabel(cvsTestLogSearch, SWT.NONE);
        lblTestLogSearch.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
        updateStatusSearchLabel();

        Label lblSeparator = new Label(cvsTestLogSearch, SWT.SEPARATOR);
        GridData gdLblSeparator = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblSeparator.heightHint = 18;
        lblSeparator.setLayoutData(gdLblSeparator);

        lblTestLogAdvancedSearch = new CLabel(cvsTestLogSearch, SWT.NONE);
        lblTestLogAdvancedSearch.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
        lblTestLogAdvancedSearch
                .setImage(com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_ADVANCED_SEARCH);
        updateStatusSearchLabel();

        createTestLogTableToolbar(compositeTestLogFilter);
    }

    private Display getDisplay() {
        return parentPart.getDisplay() ;
    }

    private void createTestLogTableToolbar(Composite parent) {
        testLogToolbar = new ToolBar(parent, SWT.NONE);
        testLogToolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        testLogToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        tltmCollapseAllLogs = new ToolItem(testLogToolbar, SWT.NONE);
        tltmCollapseAllLogs.setToolTipText("Collapse All Logs");
        tltmCollapseAllLogs.setImage(
                ImageUtil.loadImage(Platform.getBundle("org.eclipse.ui"), "icons/full/elcl16/collapseall.png"));

        tltmExpandAllLogs = new ToolItem(testLogToolbar, SWT.NONE);
        tltmExpandAllLogs.setToolTipText("Expand All Logs");
        tltmExpandAllLogs
                .setImage(ImageUtil.loadImage(Platform.getBundle("org.eclipse.ui"), "icons/full/elcl16/expandall.png"));
    }

    private void createTestLogTable(Composite parent) {
        Composite compositeTestLogTable = new Composite(parent, SWT.NONE);
        compositeTestLogTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tbtmTestLog.setControl(parent);
        TreeColumnLayout tclCompositeTestLogTable = new TreeColumnLayout();
        compositeTestLogTable.setLayout(tclCompositeTestLogTable);

        treeViewerTestSteps = new ReportTestStepTreeViewer(compositeTestLogTable, SWT.FULL_SELECTION);
        Tree treeTestCaseLog = treeViewerTestSteps.getTree();
        treeTestCaseLog.setLinesVisible(ControlUtils.shouldLineVisble(treeTestCaseLog.getDisplay()));
        treeTestCaseLog.setHeaderVisible(true);

        TreeViewerColumn treeViewerColumnLogItem = new TreeViewerColumn(treeViewerTestSteps, SWT.NONE);
        TreeColumn trclmnTestLogItem = treeViewerColumnLogItem.getColumn();
        trclmnTestLogItem.setText("Item");
        tclCompositeTestLogTable.setColumnData(trclmnTestLogItem, new ColumnWeightData(45, 200));
        treeViewerColumnLogItem.setLabelProvider(
                new ReportPartTestStepLabelProvider(ReportPartTestStepLabelProvider.CLMN_TEST_LOG_ITEM_IDX, this));

        TreeViewerColumn treeViewerColumnLogDescription = new TreeViewerColumn(treeViewerTestSteps, SWT.NONE);
        TreeColumn trclmnTestLogDescription = treeViewerColumnLogDescription.getColumn();
        trclmnTestLogDescription.setText("Description");
        tclCompositeTestLogTable.setColumnData(trclmnTestLogDescription, new ColumnWeightData(30, 170));
        treeViewerColumnLogDescription.setLabelProvider(new ReportPartTestStepLabelProvider(
                ReportPartTestStepLabelProvider.CLMN_TEST_LOG_DESCRIPTION_IDX, this));

        TreeViewerColumn treeViewerColumnElapsedTime = new TreeViewerColumn(treeViewerTestSteps, SWT.NONE);
        TreeColumn trclmnTestLogElapsedTime = treeViewerColumnElapsedTime.getColumn();
        trclmnTestLogElapsedTime.setText("Elapsed");
        tclCompositeTestLogTable.setColumnData(trclmnTestLogElapsedTime, new ColumnWeightData(0, 80));
        treeViewerColumnElapsedTime.setLabelProvider(
                new ReportPartTestStepLabelProvider(ReportPartTestStepLabelProvider.CLMN_TEST_LOG_ELAPSED_IDX, this));

        TreeViewerColumn treeViewerColumnAttachment = new TreeViewerColumn(treeViewerTestSteps, SWT.NONE);
        TreeColumn trclmnTestLogAttachment = treeViewerColumnAttachment.getColumn();
        trclmnTestLogAttachment.setText("");
        trclmnTestLogAttachment.setImage(ImageConstants.IMG_16_ATTACHMENT);
        tclCompositeTestLogTable.setColumnData(trclmnTestLogAttachment, new ColumnWeightData(0, 40));
        treeViewerColumnAttachment.setLabelProvider(new ReportPartTestStepLabelProvider(
                ReportPartTestStepLabelProvider.CLMN_TEST_LOG_ATTACHMENT_IDX, this));
        createTestLogIntegrationColumns(tclCompositeTestLogTable);
        treeViewerTestSteps.setContentProvider(new ReportTreeTableContentProvider());

        // enable tooltip helper for treeViewerTestCaseLog
        treeViewerTestSteps.getTree().setToolTipText("");
        ColumnViewerToolTipSupport.enableFor(treeViewerTestSteps);

        testStepFilter = new ReportTestStepTableViewerFilter();

        treeViewerTestSteps.addFilter(testStepFilter);

        testStepFilter.showInfo(btnFilterTestStepInfo.getSelection());
        testStepFilter.showPassed(btnFilterTestStepPassed.getSelection());
        testStepFilter.showFailed(btnFilterTestStepFailed.getSelection());
        testStepFilter.showError(btnFilterTestStepError.getSelection());
        testStepFilter.showIncomplete(btnFilterTestStepIncomplete.getSelection());
        testStepFilter.showWarning(btnFilterTestStepWarning.getSelection());
        testStepFilter.showNotRun(btnFilterTestStepNotRun.getSelection());
    }

    private void createTestLogIntegrationColumns(TreeColumnLayout tableLayout) {
        ReportEntity report = getReport();
        for (ReportTestCaseIntegrationViewBuilder builder : ReportComposerIntegrationFactory.getInstance()
                .getSortedBuilder()) {
            if (!builder.isIntegrationEnabled(ProjectController.getInstance().getCurrentProject())) {
                continue;
            }
            TestLogIntegrationColumn testLogIntegrationColumn = builder.getTestLogIntegrationColumn(report,
                    parentPart.getTestSuiteLogRecord());
            if (testLogIntegrationColumn == null) {
                continue;
            }
            TreeViewerColumn viewerColumn = (TreeViewerColumn) testLogIntegrationColumn
                    .createIntegrationColumn(treeViewerTestSteps, treeViewerTestSteps.getTree().getColumnCount());
            tableLayout.setColumnData(viewerColumn.getColumn(), new ColumnWeightData(0, 32));
            testCaseChangedEventListeners.add(testLogIntegrationColumn);
        }
    }

    private void createTestLogTabItem(CTabFolder tabFolder) {
        tbtmTestLog = new CTabItem(tabFolder, SWT.NONE);
        tbtmTestLog.setText("Test Log");

        Composite compositeTestLog = new Composite(tabFolder, SWT.NONE);
        compositeTestLog.setLayout(new GridLayout(1, false));

        createCompositeFilterTestLog(compositeTestLog);

        createTestLogTable(compositeTestLog);

        tabFolder.setSelection(0);
    }

    private void createTestCaseInformationTabItem(CTabFolder tabFolder) {
        CTabItem tbtmGeneralInformation = new CTabItem(tabFolder, SWT.NONE);
        tbtmGeneralInformation.setText(ComposerReportMessageConstants.LBL_INFORMATION);

        compositeTestCaseInformation = new Composite(tabFolder, SWT.NONE);
        tbtmGeneralInformation.setControl(compositeTestCaseInformation);
        compositeTestCaseInformation.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout glCompositeTestCaseInformation = new GridLayout(6, false);
        glCompositeTestCaseInformation.verticalSpacing = 7;
        glCompositeTestCaseInformation.horizontalSpacing = 15;
        compositeTestCaseInformation.setLayout(glCompositeTestCaseInformation);

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
        enableMargin(txtSTestCaseDescription, false);

        Label lblSTCMessage = new Label(compositeTestCaseInformation, SWT.NONE);
        lblSTCMessage.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblSTCMessage.setText("Message");
        parentPart.setLabelToBeBold(lblSTCMessage);

        txtSTestCaseMessage = new StyledText(compositeTestCaseInformation, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        txtSTestCaseMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        txtSTestCaseMessage.setMarginColor(ColorUtil.getTextPlaceholderColor());
        enableMargin(txtSTestCaseMessage, false);
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

        testCaseLogIntegrationToolbar = new ToolBar(compositeTestCaseIntegrationToolbar, SWT.FLAT | SWT.VERTICAL);
        testCaseLogIntegrationToolbar.setForeground(ColorUtil.getToolBarForegroundColor());

        compositeTestCaseLogIntegration = new Composite(compositeTestCaseIntegration, SWT.NONE);
        compositeTestCaseLogIntegration.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeTestCaseLogIntegration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    }

    public Composite createCompositeSelectedTestLog(SashForm sashFormDetails) {
        Composite compositeTestCaseSelectedLog = new Composite(sashFormDetails, SWT.BORDER);
        compositeTestCaseSelectedLog.setLayout(new FillLayout(SWT.HORIZONTAL));

        selectedTestLogTabFolder = new CTabFolder(compositeTestCaseSelectedLog, SWT.BORDER);

        createSelectedTestStepInformationTabItem(selectedTestLogTabFolder);

        // createSelectedTestLogStackTraceTabItem(tabFolder);

        createSelectedTestStepImageViewTabItem(selectedTestLogTabFolder);

        imageToolbar = new ToolBar(selectedTestLogTabFolder, SWT.NONE);
        imageToolbar.setForeground(ColorUtil.getToolBarForegroundColor());

        tltmFitScreen = new ToolItem(imageToolbar, SWT.CHECK);
        tltmFitScreen.setImage(ImageConstants.IMG_16_FIT_SCREEN);
        tltmFitScreen.setToolTipText("Fit to View");

        tltmResetImageSize = new ToolItem(imageToolbar, SWT.CHECK);
        tltmResetImageSize.setImage(ImageConstants.IMG_16_FULL_SIZE);
        tltmResetImageSize.setToolTipText("Show Full Size");

        selectedTestLogTabFolder.setTopRight(imageToolbar);

        imageToolbar.setVisible(selectedTestLogTabFolder.getSelectionIndex() == IMAGE_VIEW_TAB_ITEM_IDX);
        return compositeTestCaseSelectedLog;
    }

    private void createSelectedTestStepInformationTabItem(CTabFolder tabFolder) {
        CTabItem tbtmSTLInformation = new CTabItem(tabFolder, SWT.NONE);
        tbtmSTLInformation.setText(ComposerReportMessageConstants.LBL_INFORMATION);

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
        enableMargin(txtSTLDescription, false);

        Label lblSTLMessage = new Label(compositeSTLInformation, SWT.NONE);
        lblSTLMessage.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblSTLMessage.setText("Message");
        parentPart.setLabelToBeBold(lblSTLMessage);

        txtSTLMessage = new StyledText(compositeSTLInformation, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        txtSTLMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        txtSTLMessage.setMarginColor(ColorUtil.getTextPlaceholderColor());
        enableMargin(txtSTLMessage, false);

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
                if (drawnImage != null && !drawnImage.isDisposed()) {
                    e.gc.drawImage(drawnImage, 0, 0);
                }
            }
        });

        compositeSTLSImageView.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event event) {
                selectedTestLogCanvas.redraw();
                drawImage();
            }
        });

        compositeSTLSImageView.setContent(selectedTestLogCanvas);
    }

    public void loadTestCaseIntegrationToolbar(ReportEntity report, TestSuiteLogRecord testSuiteLogRecord) {
        clearTestCaseIntegrationToolbar();

        for (Entry<String, TestCaseLogDetailsIntegrationView> builderEntry : parentPart.getIntegratingCompositeMap()
                .entrySet()) {
            if (builderEntry.getValue() != null) {
                ToolItem item = new ToolItem(testCaseLogIntegrationToolbar, SWT.CHECK);
                item.setText(builderEntry.getKey());
            }
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
        for (ToolItem item : testCaseLogIntegrationToolbar.getItems()) {
            if (!productName.equals(item.getText())) {
                item.setSelection(false);
            }
        }
        clearTestCaseIntegrationContainer();

        selectedReportTestCaseIntegrationView = parentPart.getIntegratingCompositeMap().get(productName);
        if (selectedReportTestCaseIntegrationView == null) {
            return;
        }
        selectedReportTestCaseIntegrationView.createContainer(compositeTestCaseLogIntegration);

        compositeTestCaseLogIntegration.layout(true, true);
        if (parentPart.getSelectedTestCaseLogRecord() != null) {
            selectedReportTestCaseIntegrationView
                    .changeTestCase((TestCaseLogRecord) parentPart.getSelectedTestCaseLogRecord());
        }
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
            txtSTestCaseElapsedTime
                    .setText(DateUtil.getElapsedTime(selectedLogRecord.getStartTime(), selectedLogRecord.getEndTime()));
            StyledString txtSTLElapsedStyleString = new StyledString(txtSTestCaseElapsedTime.getText(),
                    StyledString.COUNTER_STYLER);
            txtSTestCaseElapsedTime.setStyleRanges(txtSTLElapsedStyleString.getStyleRanges());
        } else {
            txtSTestCaseElapsedTime.setText("");
        }

        if (selectedLogRecord != null && selectedLogRecord.getDescription() != null
                && !selectedLogRecord.getDescription().isEmpty()) {
            txtSTestCaseDescription.setText(StringEscapeUtils.unescapeJava(selectedLogRecord.getDescription()));
            enableMargin(txtSTestCaseDescription, true);
        } else {
            txtSTestCaseDescription.setText("");
            enableMargin(txtSTestCaseDescription, false);
        }

        if (selectedLogRecord != null && selectedLogRecord.getMessage() != null) {
            txtSTestCaseMessage.setText(selectedLogRecord.getMessage());
            enableMargin(txtSTestCaseMessage, true);
        } else {
            txtSTestCaseMessage.setText("");
            enableMargin(txtSTestCaseMessage, false);
        }

        compositeTestCaseInformation.layout();

        for (TestCaseChangedEventListener listener : testCaseChangedEventListeners) {
            listener.changeTestCase((TestCaseLogRecord) selectedLogRecord);
        }
        if (selectedLogRecord != null) {
            treeViewerTestSteps.setInput(selectedLogRecord.getChildRecords());
        } else {
            treeViewerTestSteps.setInput(null);
        }
        if (selectedReportTestCaseIntegrationView != null) {
            selectedReportTestCaseIntegrationView
                    .changeTestCase((TestCaseLogRecord) parentPart.getSelectedTestCaseLogRecord());
        }
        updateSelectedTestStep(null);
    }

    private void clickToOpenTestCaseListener(StyledText styleText) {
        StyleRange range = new StyleRange();
        range.start = 0;
        range.length = styleText.getText().length();
        range.underline = true;
        range.data = styleText.getText();
        range.underlineStyle = SWT.UNDERLINE_LINK;
        range.foreground = ColorUtil.getHyperlinkTextColor();

        styleText.setStyleRanges(new StyleRange[] { range });

        styleText.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                try {
                    StyledText styleText = (StyledText) event.widget;
                    int offset = styleText.getOffsetAtLocation(new Point(event.x, event.y));
                    StyleRange style = styleText.getStyleRangeAtOffset(offset);
                    if (style != null && style.underline && style.underlineStyle == SWT.UNDERLINE_LINK) {
                        TestCaseEntity testCaseEntity = TestCaseController.getInstance()
                                .getTestCaseByDisplayId((String) style.data);
                        if (testCaseEntity != null) {
                            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.TESTCASE_OPEN,
                                    testCaseEntity);
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
            txtSTLElapsedTime
                    .setText(DateUtil.getElapsedTime(selectedLogRecord.getStartTime(), selectedLogRecord.getEndTime()));
            StyledString txtSTLElapsedStyleString = new StyledString(txtSTLElapsedTime.getText(),
                    StyledString.COUNTER_STYLER);
            txtSTLElapsedTime.setStyleRanges(txtSTLElapsedStyleString.getStyleRanges());
        } else {
            txtSTLElapsedTime.setText("");
        }

        if (selectedLogRecord != null && StringUtils.isNotEmpty(selectedLogRecord.getDescription())) {
            txtSTLDescription.setText(StringEscapeUtils.unescapeJava(selectedLogRecord.getDescription()));
            enableMargin(txtSTLDescription, true);
        } else {
            txtSTLDescription.setText("");
            enableMargin(txtSTLDescription, false);
        }

        if (selectedLogRecord != null && StringUtils.isNotEmpty(selectedLogRecord.getMessage())) {
            txtSTLMessage.setText(selectedLogRecord.getMessage());
            enableMargin(txtSTLMessage, true);
        } else {
            txtSTLMessage.setText("");
            enableMargin(txtSTLMessage, false);
        }

        compositeSTLInformation.layout();

        if (selectedTestLogImage != null) {
            drawnImage.dispose();
            selectedTestLogImage.dispose();
        }

        if (selectedLogRecord != null && selectedLogRecord instanceof MessageLogRecord) {
            MessageLogRecord messageLog = (MessageLogRecord) selectedLogRecord;
            if (messageLog.getAttachment() != null) {

                selectedTestLogImage = new Image(selectedTestLogCanvas.getDisplay(),
                        PathUtil.relativeToAbsolutePath(messageLog.getAttachment(), getReport().getLocation()));
                drawnImage = selectedTestLogImage;
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

    public void enableMargin(StyledText styledText, boolean enable) {
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

    private static enum ImageScreenMode {
        FIT_SCREEN, FULL_SIZE
    }
}
