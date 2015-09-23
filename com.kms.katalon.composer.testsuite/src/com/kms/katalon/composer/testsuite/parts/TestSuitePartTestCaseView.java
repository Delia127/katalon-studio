package com.kms.katalon.composer.testsuite.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.custom.AdvancedSearchDialog;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.constants.ToolItemConstants;
import com.kms.katalon.composer.testsuite.listeners.TestCaseTableDragListener;
import com.kms.katalon.composer.testsuite.listeners.TestCaseTableDropListener;
import com.kms.katalon.composer.testsuite.listeners.TestCaseTableKeyListener;
import com.kms.katalon.composer.testsuite.listeners.TestCaseToolItemListener;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableLabelProvider;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewerFilter;
import com.kms.katalon.composer.testsuite.support.TestCaseIdColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.TestCaseIsRunColumnEditingSupport;
import com.kms.katalon.composer.testsuite.transfer.TestSuiteTestCaseLinkTransfer;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuitePartTestCaseView {
    private static final String IS_RUN_COLUMN_HEADER = StringConstants.PA_COL_RUN;

    private static final String PK_COLUMN_HEADER = StringConstants.PA_COL_ID;

    private static final String NUMBER_COLUMN_HEADER = StringConstants.PA_COL_NO;

    private static final String DESCRIPTION_COLUMN_HEADER = StringConstants.PA_COL_DESC;

    private Composite compositeTestCase;
    private TestSuitePart testSuitePart;

    private TestCaseTableViewer testCaseTableViewer;
    private Composite compositeTableSearch;
    private CLabel lblFilter;
    private CLabel lblSearch;
    private Text txtSearch;

    private boolean isSearching;

    private int testSuiteTestCaseSelectedIdx;

    private ScrolledComposite compositeTablePart;

    private TableColumn tblclmnIsRun, tblclId;

    private ToolBar testCaseToolbar;

    private TestSuitePartDataBindingView dataAndVariableView;

    /* package */TestSuitePartTestCaseView(TestSuitePart testSuitePart) {
        this.testSuitePart = testSuitePart;
        this.dataAndVariableView = new TestSuitePartDataBindingView(this);
    }

    /* package *//**
     * @wbp.parser.entryPoint
     */
    ScrolledComposite createCompositeTestCase(Composite compositeMain) {
        compositeTablePart = new ScrolledComposite(compositeMain, SWT.V_SCROLL);
        compositeTablePart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        compositeTablePart.setBackground(ColorUtil.getCompositeBackgroundColor());

        SashForm sashForm = new SashForm(compositeTablePart, SWT.NONE);
        sashForm.setSashWidth(5);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        sashForm.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());

        createCompositeTestCaseButtons(sashForm);
        createCompositeTestCaseSearch();
        createCompositeTestCaseContent(sashForm);

        compositeTablePart.setContent(sashForm);
        compositeTablePart.setExpandHorizontal(true);
        compositeTablePart.setExpandVertical(true);
        compositeTablePart.setBackgroundMode(SWT.INHERIT_DEFAULT); // inherit background color from its parent
        return compositeTablePart;
    }

    private void createCompositeTestCaseContent(SashForm sashForm) {
        Composite compositeTableContent = new Composite(compositeTestCase, SWT.NONE);
        compositeTableContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        testCaseTableViewer = new TestCaseTableViewer(compositeTableContent, SWT.BORDER | SWT.FULL_SELECTION
                | SWT.MULTI, this);
        Table testCaseTable = testCaseTableViewer.getTable();
        testCaseTable.setHeaderVisible(true);
        testCaseTable.setLinesVisible(true);

        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
        TableColumn tblclmnNo = tableViewerColumnNo.getColumn();
        tblclmnNo.setText(NUMBER_COLUMN_HEADER);

        TableViewerColumn tableViewerColumnPK = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
        tblclId = tableViewerColumnPK.getColumn();
        tblclId.setText(PK_COLUMN_HEADER);

        TableViewerColumn tableViewerColumnDescription = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
        TableColumn tblclmnDescription = tableViewerColumnDescription.getColumn();
        tblclmnDescription.setText(DESCRIPTION_COLUMN_HEADER);

        TableViewerColumn tableViewerColumnIsRun = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
        tblclmnIsRun = tableViewerColumnIsRun.getColumn();
        tblclmnIsRun.setText(IS_RUN_COLUMN_HEADER);
        tblclmnIsRun.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                testCaseTableViewer.setIsRunValueAllTestCases();
            }
        });

        // set layout of table composite
        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(tblclmnNo, new ColumnWeightData(0, 40));
        tableLayout.setColumnData(tblclId, new ColumnWeightData(40, 100));
        tableLayout.setColumnData(tblclmnDescription, new ColumnWeightData(15, 100));
        tableLayout.setColumnData(tblclmnIsRun, new ColumnWeightData(0, 80));

        compositeTableContent.setLayout(tableLayout);

        testCaseTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        testCaseTableViewer.setFilters(new ViewerFilter[] { new TestCaseTableViewerFilter() });
        testCaseTableViewer.getTable().setToolTipText("");
        ColumnViewerToolTipSupport.enableFor(testCaseTableViewer, ToolTip.NO_RECREATE);

        tableViewerColumnNo
                .setLabelProvider(new TestCaseTableLabelProvider(TestCaseTableLabelProvider.COLUMN_NO_INDEX));
        tableViewerColumnPK
                .setLabelProvider(new TestCaseTableLabelProvider(TestCaseTableLabelProvider.COLUMN_ID_INDEX));

        tableViewerColumnDescription.setLabelProvider(new TestCaseTableLabelProvider(
                TestCaseTableLabelProvider.COLUMN_DESCRIPTION_INDEX));

        tableViewerColumnIsRun.setLabelProvider(new TestCaseTableLabelProvider(
                TestCaseTableLabelProvider.COLUMN_RUN_INDEX));
        tableViewerColumnIsRun.setEditingSupport(new TestCaseIsRunColumnEditingSupport(testCaseTableViewer, this));

        tableViewerColumnPK.setEditingSupport(new TestCaseIdColumnEditingSupport(testCaseTableViewer, this));

        testCaseTable.addKeyListener(new TestCaseTableKeyListener(testCaseTableViewer));

        createCompositeTestDataAndVariable(sashForm);

        sashForm.setWeights(new int[] { 5, 5 });
        hookDropTestCaseEvent();
        hookDragTestCaseEvent();
    }

    private void createCompositeTestDataAndVariable(SashForm sashForm) {
        dataAndVariableView.createCompositeTestDataAndVariable(sashForm);
    }

    private void hookDropTestCaseEvent() {
        DropTarget dt = new DropTarget(testCaseTableViewer.getTable(), DND.DROP_MOVE);
        List<Transfer> treeEntityTransfers = TransferTypeCollection.getInstance().getTreeEntityTransfer();
        treeEntityTransfers.add(new TestSuiteTestCaseLinkTransfer());
        dt.setTransfer(treeEntityTransfers.toArray(new Transfer[treeEntityTransfers.size()]));
        dt.addDropListener(new TestCaseTableDropListener(testCaseTableViewer, getTestSuite()));
    }

    private void hookDragTestCaseEvent() {
        int operations = DND.DROP_MOVE | DND.DROP_COPY;

        DragSource dragSource = new DragSource(testCaseTableViewer.getTable(), operations);
        dragSource.setTransfer(new Transfer[] { new TestSuiteTestCaseLinkTransfer() });
        dragSource.addDragListener(new TestCaseTableDragListener(testCaseTableViewer, this));
    }

    public TestSuiteEntity getTestSuite() {
        return testSuitePart.getTestSuite();
    }

    private void processTestSuiteTestCaseLinkSelected() {
        if (testCaseTableViewer.getSelection() == null) return;
        if (!(testCaseTableViewer.getSelection() instanceof IStructuredSelection)) return;

        dataAndVariableView.updateSelectedTestCase((IStructuredSelection) testCaseTableViewer.getSelection());
    }

    /* package */void registerControlModifyListeners() {
        dataAndVariableView.registerControlModifyListeners();
        
        lblSearch.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                if (isSearching) {
                    txtSearch.setText("");
                }

                filterTestCaseLinkBySearchedText();
            }
        });

        txtSearch.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    filterTestCaseLinkBySearchedText();
                }
            }
        });

        lblFilter.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                openAdvancedSearchDialog();
            }
        });

        TestCaseToolItemListener testCaseToolItemListener = new TestCaseToolItemListener(testCaseTableViewer);
        for (ToolItem item : testCaseToolbar.getItems()) {
            item.addSelectionListener(testCaseToolItemListener);
        }

        testCaseTableViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                processTestSuiteTestCaseLinkSelected();
            }
        });

        tblclId.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                testCaseTableViewer.refresh(true);
            }
        });
    }

    private void createCompositeTestCaseSearch() {
        compositeTableSearch = new Composite(compositeTestCase, SWT.BORDER);
        compositeTableSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeTableSearch.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout glCompositeTableSearch = new GridLayout(4, false);
        glCompositeTableSearch.marginWidth = 0;
        glCompositeTableSearch.marginHeight = 0;
        compositeTableSearch.setLayout(glCompositeTableSearch);

        txtSearch = new Text(compositeTableSearch, SWT.NONE);
        txtSearch.setMessage(StringConstants.PA_SEARCH_TEXT_DEFAULT_VALUE);
        GridData gdTxtInput = new GridData(GridData.FILL_HORIZONTAL);
        gdTxtInput.grabExcessVerticalSpace = true;
        gdTxtInput.verticalAlignment = SWT.CENTER;
        txtSearch.setLayoutData(gdTxtInput);

        Canvas canvasSearch = new Canvas(compositeTableSearch, SWT.NONE);
        canvasSearch.setLayout(new FillLayout(SWT.HORIZONTAL));
        lblSearch = new CLabel(canvasSearch, SWT.NONE);
        updateStatusSearchLabel();

        lblSearch.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));

        Label seperator = new Label(compositeTableSearch, SWT.SEPARATOR | SWT.VERTICAL);
        GridData gdSeperator = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdSeperator.heightHint = 22;
        seperator.setLayoutData(gdSeperator);

        // label Filter
        lblFilter = new CLabel(compositeTableSearch, SWT.NONE);
        lblFilter.setImage(ImageConstants.IMG_16_ADVANCED_SEARCH);
        lblFilter.setToolTipText(StringConstants.PA_IMAGE_TIP_ADVANCED_SEARCH);
        lblFilter.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
    }

    private void updateStatusSearchLabel() {
        if (isSearching) {
            lblSearch.setImage(ImageConstants.IMG_16_CLOSE_SEARCH);
            lblSearch.setToolTipText(StringConstants.PA_IMAGE_TIP_CLOSE_SEARCH);
        } else {
            lblSearch.setImage(ImageConstants.IMG_16_SEARCH);
            lblSearch.setToolTipText(StringConstants.PA_IMAGE_TIP_SEARCH);
        }
    }

    private void filterTestCaseLinkBySearchedText() {
        if (txtSearch.getText().isEmpty()) {
            isSearching = false;
        } else {
            isSearching = true;
        }

        testCaseTableViewer.setSearchedString(txtSearch.getText());
        testCaseTableViewer.refresh(true);
        updateStatusSearchLabel();
    }

    private void openAdvancedSearchDialog() {
        try {
            Shell shell = new Shell(compositeTableSearch.getShell());
            shell.setSize(0, 0);
            List<String> searchTags = Arrays.asList(TestCaseTreeEntity.SEARCH_TAGS);

            Point pt = compositeTableSearch.toDisplay(1, 1);
            Point location = new Point(pt.x + compositeTableSearch.getBounds().width, pt.y);
            AdvancedSearchDialog dialog = new AdvancedSearchDialog(shell, searchTags.toArray(new String[searchTags
                    .size()]), txtSearch.getText(), location);
            // set position for dialog
            if (dialog.open() == Dialog.OK) {
                txtSearch.setText(dialog.getOutput());
                filterTestCaseLinkBySearchedText();
            }

            shell.getSize();
            shell.dispose();

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void createCompositeTestCaseButtons(SashForm sashForm) {
        compositeTestCase = new Composite(sashForm, SWT.NONE);
        compositeTestCase.setLayout(new GridLayout(1, false));
        compositeTestCase.setBackground(ColorUtil.getCompositeBackgroundColor());
        final Composite compositeTableButtons = new Composite(compositeTestCase, SWT.NONE);
        compositeTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeTableButtons = new GridLayout(1, false);
        compositeTableButtons.setBackground(ColorUtil.getCompositeBackgroundColor());
        glCompositeTableButtons.marginHeight = 0;
        glCompositeTableButtons.marginWidth = 0;
        compositeTableButtons.setLayout(glCompositeTableButtons);

        testCaseToolbar = new ToolBar(compositeTableButtons, SWT.FLAT | SWT.RIGHT);

        ToolItem tltmAddTestCases = new ToolItem(testCaseToolbar, SWT.NONE);
        tltmAddTestCases.setText(StringConstants.PA_TOOLITEM_ADD);
        tltmAddTestCases.setToolTipText(StringConstants.PA_TOOLITEM_ADD);
        tltmAddTestCases.setImage(ImageConstants.IMG_24_ADD);
        tltmAddTestCases.setData(ToolItemConstants.ADD);

        ToolItem tltmRemoveTestCases = new ToolItem(testCaseToolbar, SWT.NONE);
        tltmRemoveTestCases.setText(StringConstants.PA_TOOLITEM_REMOVE);
        tltmRemoveTestCases.setToolTipText(StringConstants.PA_TOOLITEM_REMOVE);
        tltmRemoveTestCases.setImage(ImageConstants.IMG_24_REMOVE);
        tltmRemoveTestCases.setData(ToolItemConstants.REMOVE);

        ToolItem tltmUp = new ToolItem(testCaseToolbar, SWT.NONE);
        tltmUp.setText(StringConstants.PA_TOOLITEM_UP);
        tltmUp.setToolTipText(StringConstants.PA_TOOLITEM_UP);
        tltmUp.setImage(ImageConstants.IMG_24_UP);
        tltmUp.setData(ToolItemConstants.UP);

        ToolItem tltmDown = new ToolItem(testCaseToolbar, SWT.NONE);
        tltmDown.setText(StringConstants.PA_TOOLITEM_DOWN);
        tltmDown.setToolTipText(StringConstants.PA_TOOLITEM_DOWN);
        tltmDown.setImage(ImageConstants.IMG_24_DOWN);
        tltmDown.setData(ToolItemConstants.DOWN);
    }

    /* package */void initExpandedState() {
        isSearching = false;
        dataAndVariableView.initExpandedState();
    }

    /* package */void layout() {
        dataAndVariableView.layout();
    }

    /* package */void refreshTestSuiteAfterTestDataChanged(String oldTestDataId, String newTestDataId) {
        dataAndVariableView.refreshTestSuiteAfterTestDataChanged(oldTestDataId, newTestDataId);
    }

    /* package */boolean beforeSaving() {
        testSuiteTestCaseSelectedIdx = testCaseTableViewer.getTable().getSelectionIndex();

        getTestSuite().getTestSuiteTestCaseLinks().clear();

        for (Object testCaseLink : testCaseTableViewer.getInput()) {
            getTestSuite().getTestSuiteTestCaseLinks().add((TestSuiteTestCaseLink) testCaseLink);
        }
        return true;
    }

    /* package */void afterSaving() {
        if (testCaseTableViewer == null) return;

        Table testCaseTable = testCaseTableViewer.getTable();
        if (testCaseTable == null || testCaseTable.isDisposed()) return;

        if (testSuiteTestCaseSelectedIdx >= 0
                && testSuiteTestCaseSelectedIdx < testCaseTableViewer.getTable().getItemCount()) {
            TestSuiteTestCaseLink selectedTestCaseLink = testCaseTableViewer.getInput().get(
                    testSuiteTestCaseSelectedIdx);
            IStructuredSelection selection = new StructuredSelection(Arrays.asList(selectedTestCaseLink));
            testCaseTableViewer.setSelection(selection);
        }
    }

    public TestSuiteTestCaseLink getSelectedTestCaseLink() {
        return testCaseTableViewer.getSelectedTestCaseLink();
    }

    public void setDirty(boolean dirty) {
        testSuitePart.setDirty(dirty);
    }

    /* package */void loadInput() throws Exception {
        testCaseTableViewer.setInput(new ArrayList<TestSuiteTestCaseLink>(getTestSuite().getTestSuiteTestCaseLinks()));
        processTestSuiteTestCaseLinkSelected();
    }

    public void updateIsRunColumnHeader() {
        boolean isRunAll = testCaseTableViewer.getIsRunAll();
        Image isRunColumnImageHeader;
        if (isRunAll) {
            isRunColumnImageHeader = ImageConstants.IMG_16_CHECKBOX_CHECKED;
        } else {
            isRunColumnImageHeader = ImageConstants.IMG_16_CHECKBOX_UNCHECKED;
        }
        tblclmnIsRun.setImage(isRunColumnImageHeader);
    }

    /* package */void updateTestCaseTable(String oldTestCaseId, TestCaseEntity testCase) throws Exception {
        testCaseTableViewer.updateTestCaseProperties(oldTestCaseId, testCase);
        dataAndVariableView.refreshVariableTable();
    }

}
