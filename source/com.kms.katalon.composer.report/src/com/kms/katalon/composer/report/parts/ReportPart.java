package com.kms.katalon.composer.report.parts;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPartEvent;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.report.constants.ImageConstants;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.composer.report.integration.ReportComposerIntegrationFactory;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.composer.report.parts.integration.ReportTestCaseIntegrationViewBuilder;
import com.kms.katalon.composer.report.parts.integration.TestCaseLogDetailsIntegrationView;
import com.kms.katalon.composer.report.provider.ReportPartTestCaseLabelProvider;
import com.kms.katalon.composer.report.provider.ReportTestCaseTableViewer;
import com.kms.katalon.composer.report.provider.ReportTestCaseTableViewerFilter;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.core.util.internal.DateUtil;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.util.ExecutionUtil;

public class ReportPart implements EventHandler, IComposerPartEvent {

    @Inject
    private IEventBroker eventBroker;

    // Controls
    private StyledText txtTestSuiteId, txtHostName, txtOS, txtPlatform, txtStartTime, txtEndTime, txtRunTime,
            txtKatalonVersion;

    private StyledText txtTotalTestCase, txtTCPasses, txtTCFailures, txtTCIncompleted, txtTCErrors;

    private TestSuiteLogRecord testSuiteLogRecord;

    private ReportTestCaseTableViewer testCaseTableViewer;

    private Text txtTestCaseSearch;

    private CLabel lblTestCaseSearch;

    private ReportTestCaseTableViewerFilter testCaseTableFilter;

    private Button btnFilterTestCasePassed, btnFilterTestCaseFailed, btnFilterTestCaseError,
            btnFilterTestCaseIncomplete;

    private TableViewer runDataTable, executionSettingTable;

    private ReportPartTestLogView testLogView;

    // Fields
    private Map<String, TestCaseLogDetailsIntegrationView> integratingCompositeMap;

    private int selectedTestCaseRecordIndex;

    private boolean isSearching;

    private ReportEntity report;

    private final class MapDataKeyLabelProvider extends ColumnLabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof Entry) {
                return String.valueOf(((Entry<?, ?>) element).getKey());
            }
            return "";
        }
    }

    private final class MapDataKeyEditingSupport extends EditingSupport {
        private MapDataKeyEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }

        @Override
        protected void setValue(Object element, Object value) {
            // do nothing
        }

        @Override
        protected Object getValue(Object element) {
            if (element instanceof Entry) {
                return ((Entry<?, ?>) element).getKey();
            }
            return null;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return new TextCellEditor((Composite) getViewer().getControl());
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }
    }

    private final class MapDataLabelValueProvider extends ColumnLabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof Entry) {
                return String.valueOf(((Entry<?, ?>) element).getValue());
            }
            return "";
        }
    }

    private final class MapDataValueEditingSupport extends EditingSupport {
        private MapDataValueEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }

        @Override
        protected void setValue(Object element, Object value) {
            // do nothing
        }

        @Override
        protected Object getValue(Object element) {
            if (element instanceof Entry) {
                return ((Entry<?, ?>) element).getValue();
            }
            return null;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return new TextCellEditor((Composite) getViewer().getControl());
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }
    }

    @PostConstruct
    public void init(Composite parent, ReportEntity report) {
        this.report = report;
        testLogView = new ReportPartTestLogView(this);
        isSearching = false;
        registerListeners();
        createControls(parent);
        registerControlModifyListeners();
        updateInput(report);
    }

    private void registerControlModifyListeners() {
        testCaseTableViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ILogRecord selectedLogRecord = (ILogRecord) getSelectedTestCaseLogRecord();

                if (selectedLogRecord == null) {
                    return;
                }

                testLogView.updateSelectedTestCase(selectedLogRecord);
            }
        });

        btnFilterTestCasePassed.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testCaseTableFilter.showPassed(btnFilterTestCasePassed.getSelection());
                testCaseTableViewer.refresh();
                testLogView.updateSelectedTestCase(getSelectedTestCaseLogRecord());
            }
        });

        btnFilterTestCaseFailed.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testCaseTableFilter.showFailed(btnFilterTestCaseFailed.getSelection());
                testCaseTableViewer.refresh();
                testLogView.updateSelectedTestCase(getSelectedTestCaseLogRecord());
            }
        });

        btnFilterTestCaseError.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testCaseTableFilter.showError(btnFilterTestCaseError.getSelection());
                testCaseTableViewer.refresh();
                testLogView.updateSelectedTestCase(getSelectedTestCaseLogRecord());
            }
        });

        btnFilterTestCaseIncomplete.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testCaseTableFilter.showIncomplete(btnFilterTestCaseIncomplete.getSelection());
                testCaseTableViewer.refresh();
                testLogView.updateSelectedTestCase(getSelectedTestCaseLogRecord());
            }
        });

        txtTestCaseSearch.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    filterTestLogBySearchedText();
                }
            }
        });

        lblTestCaseSearch.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if (isSearching) {
                    txtTestCaseSearch.setText("");
                }

                filterTestLogBySearchedText();
            }
        });

        testCaseTableViewer.getTable().addListener(SWT.MouseDown, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if (event.button == 3) {
                    createTestCaseTableContextMenuByIntegrationViews();
                }

            }

        });
        testLogView.registerControlModifyListener();
    }

    private void createTestCaseTableContextMenuByIntegrationViews() {
        Table testCaseTable = testCaseTableViewer.getTable();
        if (testCaseTable.getMenu() != null) {
            testCaseTable.getMenu().dispose();
        }

        Menu contextMenu = new Menu(testCaseTable);
        testCaseTable.setMenu(contextMenu);
        for (TestCaseLogDetailsIntegrationView integrationView : integratingCompositeMap.values()) {
            if (integrationView != null) {
                integrationView.createTableContextMenu(contextMenu, testCaseTableViewer.getSelection());
            }
        }
    }

    public void clearMouseDownListener(StyledText styleText) {
        while (styleText.getListeners(SWT.MouseDown).length > 1) {
            styleText.removeListener(SWT.MouseDown,
                    styleText.getListeners(SWT.MouseDown)[styleText.getListeners(SWT.MouseDown).length - 1]);
        }
    }

    private void registerTxtTestSuiteClickListener() {
        StyleRange range = new StyleRange();
        range.start = 0;
        range.length = txtTestSuiteId.getText().length();
        range.underline = true;
        range.data = txtTestSuiteId.getText();
        range.underlineStyle = SWT.UNDERLINE_LINK;

        txtTestSuiteId.setStyleRanges(new StyleRange[] { range });

        txtTestSuiteId.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                try {
                    int offset = txtTestSuiteId.getOffsetAtLocation(new Point(event.x, event.y));
                    StyleRange style = txtTestSuiteId.getStyleRangeAtOffset(offset);
                    if (style != null && style.underline && style.underlineStyle == SWT.UNDERLINE_LINK) {
                        TestSuiteEntity testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(
                                (String) style.data, ProjectController.getInstance().getCurrentProject());
                        if (testSuite != null) {
                            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.TEST_SUITE_OPEN,
                                    testSuite);
                        }
                    }

                } catch (IllegalArgumentException e) {
                    // no character under event.x, event.y
                } catch (Exception e) {
                    MessageDialog.openWarning(null, "Warning", "Test suite not found.");
                }
            }
        });
    }

    public void updateInput(ReportEntity report) {
        try {
            this.report = report;

            if (report == null)
                return;

            this.testSuiteLogRecord = LogRecordLookup.getInstance().getTestSuiteLogRecord(report);

            if (testSuiteLogRecord == null) {
                return;
            }

            try {
                TestSuiteEntity testSuite = ReportController.getInstance().getTestSuiteByReport(report);

                clearMouseDownListener(txtTestSuiteId);

                if (testSuite != null) {
                    txtTestSuiteId.setText(testSuite.getIdForDisplay());
                    registerTxtTestSuiteClickListener();
                } else {
                    txtTestSuiteId.setText(testSuiteLogRecord.getSource());
                }
            } catch (Exception e) {
                txtTestSuiteId.setText(testSuiteLogRecord.getSource());
            }

            if (testSuiteLogRecord.getHostName() != null && !testSuiteLogRecord.getHostName().isEmpty()) {
                txtHostName.setText(testSuiteLogRecord.getHostName());
            } else {
                txtHostName.setText(ReportUtil.getHostName());
            }

            if (testSuiteLogRecord.getOs() != null && !testSuiteLogRecord.getOs().isEmpty()) {
                txtOS.setText(testSuiteLogRecord.getOs());
            } else {
                txtOS.setText(ReportUtil.getOs());
            }

            txtKatalonVersion.setText(testSuiteLogRecord.getAppVersion());

            if (testSuiteLogRecord.getBrowser() != null && !testSuiteLogRecord.getBrowser().isEmpty()) {
                txtPlatform.setText(testSuiteLogRecord.getBrowser());
            } else {
                txtPlatform.setText(testSuiteLogRecord.getDevicePlatform());
            }

            int totalTestCases = testSuiteLogRecord.getTotalTestCases();
            txtTotalTestCase.setText(Integer.toString(totalTestCases));
            txtTCPasses.setText(Integer.toString(testSuiteLogRecord.getTotalPassedTestCases()));
            txtTCFailures.setText(Integer.toString(testSuiteLogRecord.getTotalFailedTestCases()));
            txtTCErrors.setText(Integer.toString(testSuiteLogRecord.getTotalErrorTestCases()));
            txtTCIncompleted.setText(Integer.toString(testSuiteLogRecord.getTotalIncompleteTestCases()));

            txtStartTime.setText(DateUtil.getDateTimeFormatted(testSuiteLogRecord.getStartTime()));
            txtEndTime.setText(DateUtil.getDateTimeFormatted(testSuiteLogRecord.getEndTime()));

            StyledString styleStringElapsed = new StyledString(
                    DateUtil.getElapsedTime(testSuiteLogRecord.getStartTime(), testSuiteLogRecord.getEndTime()),
                    StyledString.COUNTER_STYLER);
            txtRunTime.setText(styleStringElapsed.getString());
            txtRunTime.setStyleRanges(styleStringElapsed.getStyleRanges());

            integratingCompositeMap = new LinkedHashMap<String, TestCaseLogDetailsIntegrationView>();

            for (Entry<String, ReportTestCaseIntegrationViewBuilder> builderEntry : ReportComposerIntegrationFactory
                    .getInstance().getIntegrationViewMap().entrySet()) {
                ReportTestCaseIntegrationViewBuilder builder = builderEntry.getValue();
                if (!builder.isIntegrationEnabled(ProjectController.getInstance().getCurrentProject())) {
                    continue;
                }
                TestCaseLogDetailsIntegrationView integrationDetails = builder.getIntegrationDetails(report,
                        testSuiteLogRecord);
                if (integrationDetails != null) {
                    integratingCompositeMap.put(builderEntry.getKey(), integrationDetails);
                }
            }

            createTestCaseTableContextMenuByIntegrationViews();

            testCaseTableViewer.setInput(testSuiteLogRecord.getChildRecords());

            testLogView.loadTestCaseIntegrationToolbar(report, testSuiteLogRecord);
            runDataTable.setInput(
                    testSuiteLogRecord.getRunData() != null ? testSuiteLogRecord.getRunData().entrySet() : null);
            runDataTable.refresh();
            File executionSettingFile = ReportController.getInstance().getExecutionSettingFile(report.getLocation());
            executionSettingTable.setInput(
                    ExecutionUtil.readRunConfigSettingFromFile(executionSettingFile.getAbsolutePath()).entrySet());
            executionSettingTable.refresh();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public ILogRecord getSelectedTestCaseLogRecord() {
        StructuredSelection selection = (StructuredSelection) testCaseTableViewer.getSelection();
        if (selection == null || selection.size() != 1) {
            return null;
        }
        return (ILogRecord) selection.getFirstElement();
    }

    private void createCompositeTestCaseFilter(Composite compositeTestCaseTree) {
        Composite compositeTestCaseFilter = new Composite(compositeTestCaseTree, SWT.NONE);
        GridLayout gl_compositeTestCaseFilter = new GridLayout(1, false);
        gl_compositeTestCaseFilter.marginHeight = 0;
        compositeTestCaseFilter.setLayout(gl_compositeTestCaseFilter);
        compositeTestCaseFilter.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Composite compositeTestCaseFilterSelection = new Composite(compositeTestCaseFilter, SWT.NONE);
        compositeTestCaseFilterSelection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        compositeTestCaseFilterSelection.setLayout(new GridLayout(4, false));

        btnFilterTestCasePassed = new Button(compositeTestCaseFilterSelection, SWT.CHECK);
        btnFilterTestCasePassed.setText("Passed");
        btnFilterTestCasePassed.setImage(ImageConstants.IMG_16_PASSED);
        btnFilterTestCasePassed.setSelection(true);

        btnFilterTestCaseFailed = new Button(compositeTestCaseFilterSelection, SWT.CHECK);
        btnFilterTestCaseFailed.setText("Failed");
        btnFilterTestCaseFailed.setImage(ImageConstants.IMG_16_FAILED);
        btnFilterTestCaseFailed.setSelection(true);

        btnFilterTestCaseError = new Button(compositeTestCaseFilterSelection, SWT.CHECK);
        btnFilterTestCaseError.setText("Error");
        btnFilterTestCaseError.setImage(ImageConstants.IMG_16_ERROR);
        btnFilterTestCaseError.setSelection(true);

        btnFilterTestCaseIncomplete = new Button(compositeTestCaseFilterSelection, SWT.CHECK);
        btnFilterTestCaseIncomplete.setText("Incomplete");
        btnFilterTestCaseIncomplete.setImage(ImageConstants.IMG_16_INCOMPLETE);
        btnFilterTestCaseIncomplete.setSelection(true);

        Composite compositeTableTestCaseSearch = new Composite(compositeTestCaseFilter, SWT.BORDER);
        compositeTableTestCaseSearch.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        compositeTableTestCaseSearch.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout gl_compositeTableTestCaseSearch = new GridLayout(2, false);
        gl_compositeTableTestCaseSearch.marginWidth = 0;
        gl_compositeTableTestCaseSearch.marginHeight = 0;
        compositeTableTestCaseSearch.setLayout(gl_compositeTableTestCaseSearch);

        txtTestCaseSearch = new Text(compositeTableTestCaseSearch, SWT.NONE);
        txtTestCaseSearch.setMessage(StringConstants.PA_SEARCH_TEXT_DEFAULT_VALUE);
        GridData gd_txtTestCaseSearch = new GridData(GridData.FILL_HORIZONTAL);
        gd_txtTestCaseSearch.grabExcessVerticalSpace = true;
        gd_txtTestCaseSearch.verticalAlignment = SWT.CENTER;
        txtTestCaseSearch.setLayoutData(gd_txtTestCaseSearch);

        Canvas canvasTestCaseSearch = new Canvas(compositeTableTestCaseSearch, SWT.NONE);
        canvasTestCaseSearch.setLayout(new FillLayout(SWT.HORIZONTAL));
        lblTestCaseSearch = new CLabel(canvasTestCaseSearch, SWT.NONE);

        lblTestCaseSearch.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));

        updateStatusSearchLabel();
    }

    private void filterTestLogBySearchedText() {
        if (txtTestCaseSearch.getText().isEmpty()) {
            isSearching = false;
        } else {
            isSearching = true;
        }

        testCaseTableViewer.setSearchedString(txtTestCaseSearch.getText());
        testCaseTableViewer.refresh(true);
        updateStatusSearchLabel();
    }

    private void updateStatusSearchLabel() {
        if (isSearching) {
            lblTestCaseSearch
                    .setImage(com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_CLOSE_SEARCH);
            lblTestCaseSearch.setToolTipText(GlobalStringConstants.CLEAR);
        } else {
            lblTestCaseSearch.setImage(com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_SEARCH);
            lblTestCaseSearch.setToolTipText(GlobalStringConstants.SEARCH);
        }
    }

    private void createCompositeTestCaseTableDetails(Composite compositeTestCaseTable) {
        Composite compositeTestCaseTableDetails = new Composite(compositeTestCaseTable, SWT.NONE);
        compositeTestCaseTableDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableColumnLayout tclCompositeTestCaseTableDetails = new TableColumnLayout();
        compositeTestCaseTableDetails.setLayout(tclCompositeTestCaseTableDetails);

        testCaseTableViewer = new ReportTestCaseTableViewer(compositeTestCaseTableDetails,
                SWT.FULL_SELECTION | SWT.MULTI);
        Table table = testCaseTableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableViewerColumn tableViewerColumnOrder = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
        TableColumn tblclmnTCOrder = tableViewerColumnOrder.getColumn();
        tblclmnTCOrder.setText("No.");
        tableViewerColumnOrder.setLabelProvider(new ReportPartTestCaseLabelProvider());
        tclCompositeTestCaseTableDetails.setColumnData(tblclmnTCOrder, new ColumnWeightData(0, 40));

        TableViewerColumn tableViewerColumnName = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
        TableColumn tblclmnTCName = tableViewerColumnName.getColumn();
        tblclmnTCName.setText("Name");
        tableViewerColumnName.setLabelProvider(new ReportPartTestCaseLabelProvider());
        tclCompositeTestCaseTableDetails.setColumnData(tblclmnTCName, new ColumnWeightData(80, 0));

        createIntegrationColumns(tclCompositeTestCaseTableDetails);

        testCaseTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        testCaseTableViewer.getTable().setToolTipText("");
        ColumnViewerToolTipSupport.enableFor(testCaseTableViewer);

        testCaseTableFilter = new ReportTestCaseTableViewerFilter();
        testCaseTableFilter.showPassed(btnFilterTestCasePassed.getSelection());
        testCaseTableFilter.showFailed(btnFilterTestCaseFailed.getSelection());
        testCaseTableFilter.showError(btnFilterTestCaseError.getSelection());
        testCaseTableFilter.showIncomplete(btnFilterTestCaseIncomplete.getSelection());

        testCaseTableViewer.addFilter(testCaseTableFilter);
    }

    private void createIntegrationColumns(TableColumnLayout tableLayout) {
        for (ReportTestCaseIntegrationViewBuilder builder : ReportComposerIntegrationFactory.getInstance()
                .getSortedBuilder()) {
            if (!builder.isIntegrationEnabled(ProjectController.getInstance().getCurrentProject())) {
                continue;
            }
            TableViewerColumn viewerColumn = (TableViewerColumn) builder.getTestCaseIntegrationColumn(report).createIntegrationColumn(
                    testCaseTableViewer,
                    testCaseTableViewer.getTable().getColumnCount());
            tableLayout.setColumnData(viewerColumn.getColumn(), new ColumnWeightData(0, 32));
        }
    }

    private void createCompositeTestCaseTable(Composite sashFormSummary) {
        Composite compositeTestCaseTable = new Composite(sashFormSummary, SWT.BORDER);
        compositeTestCaseTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeTestCaseTable = new GridLayout(1, false);
        glCompositeTestCaseTable.marginHeight = 0;
        glCompositeTestCaseTable.marginWidth = 0;
        compositeTestCaseTable.setLayout(glCompositeTestCaseTable);

        Composite compositeTestCaseTableHeader = new Composite(compositeTestCaseTable, SWT.NONE);
        GridLayout gl_compositeTestCaseTableHeader = new GridLayout(1, false);
        compositeTestCaseTableHeader.setLayout(gl_compositeTestCaseTableHeader);
        compositeTestCaseTableHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label lblTestCaseTable = new Label(compositeTestCaseTableHeader, SWT.NONE);
        lblTestCaseTable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblTestCaseTable.setText("Test Cases Table");
        setLabelToBeBold(lblTestCaseTable);
        createCompositeTestCaseFilter(compositeTestCaseTable);
        createCompositeTestCaseTableDetails(compositeTestCaseTable);
    }

    private void createCompositeSummary(Composite sashFormSummary) {
        final CTabFolder tabFolder = new CTabFolder(sashFormSummary, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
        tabFolder.setSelectionBackground(
                Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

        createSummaryTabControl(tabFolder);
        createExecutionSettingTabControls(tabFolder);
        createRunDataTabControls(tabFolder);

        tabFolder.setSelection(0);
    }

    private void createSummaryTabControl(final CTabFolder tabFolder) {
        final CTabItem tbtmSummary = new CTabItem(tabFolder, SWT.NONE);
        tbtmSummary.setText(StringConstants.TITLE_SUMMARY);

        final ScrolledComposite scrolledComposite = new ScrolledComposite(tabFolder,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setMinSize(420, 200);
        scrolledComposite.setBackground(ColorUtil.getWhiteBackgroundColor());

        Composite compositeSummary = new Composite(scrolledComposite, SWT.NONE);
        compositeSummary.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
        GridLayout gl_compositeSummary = new GridLayout(1, false);
        gl_compositeSummary.horizontalSpacing = 0;
        gl_compositeSummary.verticalSpacing = 0;
        gl_compositeSummary.marginHeight = 0;
        gl_compositeSummary.marginWidth = 0;
        gl_compositeSummary.marginRight = 10;
        compositeSummary.setLayout(gl_compositeSummary);
        compositeSummary.setBackground(ColorUtil.getWhiteBackgroundColor());

        scrolledComposite.setContent(compositeSummary);
        tbtmSummary.setControl(scrolledComposite);

        Composite compositeSummaryDetails = new Composite(compositeSummary, SWT.NONE);
        compositeSummaryDetails.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout gl_compositeSummaryDetails = new GridLayout(4, false);
        gl_compositeSummaryDetails.verticalSpacing = 7;
        gl_compositeSummaryDetails.horizontalSpacing = 15;
        compositeSummaryDetails.setLayout(gl_compositeSummaryDetails);
        compositeSummaryDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Label lblTestSuiteId = new Label(compositeSummaryDetails, SWT.NONE);
        lblTestSuiteId.setText("Test Suite ID");
        setLabelToBeBold(lblTestSuiteId);
        lblTestSuiteId.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtTestSuiteId = new StyledText(compositeSummaryDetails, SWT.NONE);
        txtTestSuiteId.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));

        Label lblHostName = new Label(compositeSummaryDetails, SWT.NONE);
        lblHostName.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblHostName.setText(StringConstants.LBL_HOST_NAME);
        setLabelToBeBold(lblHostName);
        lblHostName.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtHostName = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtHostName.setEditable(false);
        txtHostName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblOS = new Label(compositeSummaryDetails, SWT.NONE);
        lblOS.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblOS.setText(StringConstants.LBL_OS);
        setLabelToBeBold(lblOS);
        lblOS.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtOS = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtOS.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblKatalonVersion = new Label(compositeSummaryDetails, SWT.NONE);
        lblKatalonVersion.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblKatalonVersion.setText(StringConstants.LBL_KATALON_VERSION);
        setLabelToBeBold(lblKatalonVersion);
        lblKatalonVersion.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtKatalonVersion = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtKatalonVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPlatform = new Label(compositeSummaryDetails, SWT.NONE);
        lblPlatform.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblPlatform.setText(StringConstants.LBL_PLATFORM);
        setLabelToBeBold(lblPlatform);
        lblPlatform.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtPlatform = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtPlatform.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblStart = new Label(compositeSummaryDetails, SWT.NONE);
        lblStart.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblStart.setText(StringConstants.REPORT_TABLE_START_TIME_COLUMN_HEADER);
        setLabelToBeBold(lblStart);
        lblStart.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtStartTime = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtStartTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblEnd = new Label(compositeSummaryDetails, SWT.NONE);
        lblEnd.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblEnd.setText(StringConstants.REPORT_TABLE_END_TIME_COLUMN_HEADER);
        setLabelToBeBold(lblEnd);
        lblEnd.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtEndTime = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtEndTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblRuntime = new Label(compositeSummaryDetails, SWT.NONE);
        lblRuntime.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblRuntime.setText(StringConstants.REPORT_TABLE_ELAPSED_TIME_COLUMN_HEADER);
        setLabelToBeBold(lblRuntime);
        lblRuntime.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtRunTime = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtRunTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        new Label(compositeSummaryDetails, SWT.NONE);
        new Label(compositeSummaryDetails, SWT.NONE);

        Label lblTotalTC = new Label(compositeSummaryDetails, SWT.NONE);
        lblTotalTC.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblTotalTC.setText("Total TC");
        setLabelToBeBold(lblTotalTC);
        lblTotalTC.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtTotalTestCase = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtTotalTestCase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        new Label(compositeSummaryDetails, SWT.NONE);
        new Label(compositeSummaryDetails, SWT.NONE);

        Label lblPassed = new Label(compositeSummaryDetails, SWT.NONE);
        lblPassed.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblPassed.setText("Passed");
        setLabelToBeBold(lblPassed);
        lblPassed.setForeground(ColorUtil.getPassedLogBackgroundColor());
        lblPassed.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtTCPasses = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtTCPasses.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        txtTCPasses.setForeground(ColorUtil.getPassedLogBackgroundColor());

        Label lblFailed = new Label(compositeSummaryDetails, SWT.NONE);
        lblFailed.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblFailed.setText("Failed");
        setLabelToBeBold(lblFailed);
        lblFailed.setForeground(ColorUtil.getFailedLogBackgroundColor());
        lblFailed.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtTCFailures = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtTCFailures.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        txtTCFailures.setForeground(ColorUtil.getFailedLogBackgroundColor());

        Label lblError = new Label(compositeSummaryDetails, SWT.NONE);
        lblError.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblError.setText("Error");
        setLabelToBeBold(lblError);
        lblError.setForeground(ColorUtil.getErrorLogBackgroundColor());
        lblError.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtTCErrors = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtTCErrors.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        txtTCErrors.setForeground(ColorUtil.getErrorLogBackgroundColor());

        Label lblIncompleted = new Label(compositeSummaryDetails, SWT.NONE);
        lblIncompleted.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblIncompleted.setText("Incomplete");
        setLabelToBeBold(lblIncompleted);
        lblIncompleted.setForeground(ColorUtil.getIncompleteLogColor());
        lblIncompleted.setBackground(ColorUtil.getWhiteBackgroundColor());

        txtTCIncompleted = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
        txtTCIncompleted.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        txtTCIncompleted.setForeground(ColorUtil.getIncompleteLogColor());
    }

    private void createExecutionSettingTabControls(CTabFolder tabFolder) {
        final CTabItem tbtmExecutionSetting = new CTabItem(tabFolder, SWT.NONE);
        tbtmExecutionSetting.setText(StringConstants.TITLE_EXECUTION_SETTINGS);

        Composite compositeExecutionSetting = new Composite(tabFolder, SWT.BORDER);
        tbtmExecutionSetting.setControl(compositeExecutionSetting);
        compositeExecutionSetting.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        compositeExecutionSetting.setLayout(tableColumnLayout);

        executionSettingTable = new TableViewer(compositeExecutionSetting, SWT.MULTI | SWT.FULL_SELECTION);
        executionSettingTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        executionSettingTable.getTable().setLinesVisible(true);
        executionSettingTable.getTable().setHeaderVisible(true);
        executionSettingTable.setContentProvider(new ArrayContentProvider());

        TableViewerColumn tableColumnRunDataKey = new TableViewerColumn(executionSettingTable, SWT.NONE);
        tableColumnRunDataKey.getColumn().setText(StringConstants.COLUMN_LBL_RUN_DATA_KEY);
        tableColumnRunDataKey.setLabelProvider(new MapDataKeyLabelProvider());
        tableColumnRunDataKey.setEditingSupport(new MapDataKeyEditingSupport(executionSettingTable));

        tableColumnLayout.setColumnData(tableColumnRunDataKey.getColumn(),
                new ColumnWeightData(50, tableColumnRunDataKey.getColumn().getWidth()));

        TableViewerColumn tableColumnRunDataValue = new TableViewerColumn(executionSettingTable, SWT.NONE);
        tableColumnRunDataValue.getColumn().setText(StringConstants.COLUMN_LBL_RUN_DATA_VALUE);
        tableColumnRunDataValue.setLabelProvider(new MapDataLabelValueProvider());
        tableColumnRunDataValue.setEditingSupport(new MapDataValueEditingSupport(executionSettingTable));

        tableColumnLayout.setColumnData(tableColumnRunDataValue.getColumn(),
                new ColumnWeightData(50, tableColumnRunDataValue.getColumn().getWidth()));
    }

    private void createRunDataTabControls(final CTabFolder tabFolder) {
        final CTabItem tbtmRunData = new CTabItem(tabFolder, SWT.NONE);
        tbtmRunData.setText(StringConstants.TITLE_EXECUTION_ENVIRONMENT);

        Composite compositeRunData = new Composite(tabFolder, SWT.BORDER);
        tbtmRunData.setControl(compositeRunData);
        compositeRunData.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        compositeRunData.setLayout(tableColumnLayout);

        runDataTable = new TableViewer(compositeRunData, SWT.MULTI | SWT.FULL_SELECTION);
        runDataTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        runDataTable.getTable().setLinesVisible(true);
        runDataTable.getTable().setHeaderVisible(true);
        runDataTable.setContentProvider(new ArrayContentProvider());

        TableViewerColumn tableColumnRunDataKey = new TableViewerColumn(runDataTable, SWT.NONE);
        tableColumnRunDataKey.getColumn().setText(StringConstants.COLUMN_LBL_RUN_DATA_KEY);
        tableColumnRunDataKey.setLabelProvider(new MapDataKeyLabelProvider());
        tableColumnRunDataKey.setEditingSupport(new MapDataKeyEditingSupport(runDataTable));

        tableColumnLayout.setColumnData(tableColumnRunDataKey.getColumn(),
                new ColumnWeightData(50, tableColumnRunDataKey.getColumn().getWidth()));

        TableViewerColumn tableColumnRunDataValue = new TableViewerColumn(runDataTable, SWT.NONE);
        tableColumnRunDataValue.getColumn().setText(StringConstants.COLUMN_LBL_RUN_DATA_VALUE);
        tableColumnRunDataValue.setLabelProvider(new MapDataLabelValueProvider());
        tableColumnRunDataValue.setEditingSupport(new MapDataValueEditingSupport(runDataTable));

        tableColumnLayout.setColumnData(tableColumnRunDataValue.getColumn(),
                new ColumnWeightData(50, tableColumnRunDataValue.getColumn().getWidth()));
    }

    private void createControls(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setBounds(0, 0, 561, 500);
        composite.setLayout(new GridLayout(1, false));

        SashForm sashForm = new SashForm(composite, SWT.NONE);
        sashForm.setSashWidth(5);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        SashForm sashFormSummary = new SashForm(sashForm, SWT.VERTICAL);
        sashFormSummary.setSashWidth(5);

        createCompositeTestCaseTable(sashFormSummary);
        createCompositeSummary(sashFormSummary);

        SashForm sashFormDetails = new SashForm(sashForm, SWT.VERTICAL);
        sashFormDetails.setSashWidth(5);

        testLogView.createCompositeTestStepTree(sashFormDetails);
        testLogView.createCompositeSelectedTestLog(sashFormDetails);

        sashFormDetails.setWeights(new int[] { 6, 4 });

        sashFormSummary.setWeights(new int[] { 75, 25 });

        sashForm.setWeights(new int[] { 35, 65 });
    }

    public void setLabelToBeBold(Label label) {
        label.setFont(JFaceResources.getFontRegistry().getBold(""));
    }

    private void registerListeners() {
        eventBroker.subscribe(EventConstants.REPORT_UPDATED, this);
    }

    public MPart getMPart() {
        return null;
    }

    public ReportEntity getReport() {
        return report;
    }

    public void setDirty(boolean dirty) {
        getMPart().setDirty(true);
    }

    public Map<String, TestCaseLogDetailsIntegrationView> getIntegratingCompositeMap() {
        return integratingCompositeMap;
    }

    public void prepareBeforeReloading() {
        if (testCaseTableViewer == null) {
            return;
        }
        selectedTestCaseRecordIndex = testCaseTableViewer.getTable().getSelectionIndex();
    }

    public void prepareAfterReloading() {
        if (selectedTestCaseRecordIndex >= 0) {
            testCaseTableViewer.setSelection(new StructuredSelection(
                    Arrays.asList(testSuiteLogRecord.getChildRecords()[selectedTestCaseRecordIndex])));
        }
    }

    @Override
    public void handleEvent(org.osgi.service.event.Event event) {
        if (event.getTopic().equals(EventConstants.REPORT_UPDATED)) {
            try {
                Object[] objects = (Object[]) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (objects == null || objects.length != 2) {
                    return;
                }

                String updatedReportId = (String) objects[0];
                if (updatedReportId == null) {
                    return;
                }

                if (updatedReportId.equals(report.getId())) {
                    prepareBeforeReloading();
                    updateInput((ReportEntity) objects[1]);
                    prepareAfterReloading();
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    @Override
    public String getEntityId() {
        return getReport().getIdForDisplay();
    }

    @Override
    @Inject
    @Optional
    public void onSelect(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) org.osgi.service.event.Event event) {
        EventUtil.post(EventConstants.PROPERTIES_ENTITY, null);
    }

    @Override
    @Inject
    @Optional
    public void onChangeEntityProperties(
            @UIEventTopic(EventConstants.PROPERTIES_ENTITY_UPDATED) org.osgi.service.event.Event event) {
        // do nothing
    }

    @Override
    @PreDestroy
    public void onClose() {
        eventBroker.unsubscribe(this);
    }
}
