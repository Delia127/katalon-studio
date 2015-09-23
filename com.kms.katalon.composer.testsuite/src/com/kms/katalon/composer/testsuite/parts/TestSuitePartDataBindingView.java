package com.kms.katalon.composer.testsuite.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.kms.katalon.composer.components.control.ImageButton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.constants.ToolItemConstants;
import com.kms.katalon.composer.testsuite.listeners.TestDataToolItemListener;
import com.kms.katalon.composer.testsuite.providers.TestDataTreeContentProvider;
import com.kms.katalon.composer.testsuite.providers.TestDataTreeLabelProvider;
import com.kms.katalon.composer.testsuite.providers.VariableTableLabelProvider;
import com.kms.katalon.composer.testsuite.support.TestDataCombinationColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.TestDataIDColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.TestDataIterationColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.VariableTestDataLinkColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.VariableTypeEditingSupport;
import com.kms.katalon.composer.testsuite.support.VariableValueEditingSupport;
import com.kms.katalon.composer.testsuite.tree.TestDataLinkTreeNode;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestSuitePartDataBindingView {
    private TestSuitePartTestCaseView parentView;
    private SashForm sashFormBindingView;
    private Composite compositeTestData;
    private ImageButton btnExpandCompositeTestData;
    private Label lblTestDataInformation;
    private Composite compositeTestDataDetails;
    private ToolBar testDataToolBar;
    private Composite compositeTestDataTreeTable;
    private TreeViewer testDataTreeViewer;
    private boolean isTestDataCompositeExpanded;
    private Composite compositeVariable;
    private TableViewer testCaseVariableTableViewer;

    private Listener layoutTestDataCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            isTestDataCompositeExpanded = !isTestDataCompositeExpanded;
            layoutTestDataComposite();
        }
    };

    public TestSuitePartDataBindingView(TestSuitePartTestCaseView parentView) {
        this.parentView = parentView;
    }

    /* package *//**
     * @wbp.parser.entryPoint
     */
    void createCompositeTestDataAndVariable(SashForm sashForm) {

        sashFormBindingView = new SashForm(sashForm, SWT.VERTICAL);
        sashFormBindingView.setSashWidth(5);
        sashFormBindingView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        sashFormBindingView.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());

        GridLayout glCompositeBindingChild = new GridLayout(1, false);
        glCompositeBindingChild.marginHeight = 0;
        glCompositeBindingChild.marginWidth = 0;
        sashFormBindingView.setLayout(glCompositeBindingChild);

        createCompositeTestData();
        createCompositeVariableBinding();
    }

    private void createCompositeVariableBinding() {
        compositeVariable = new Composite(sashFormBindingView, SWT.NONE);
        compositeVariable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        // compositeVariable.setBackground(ColorUtil.getCompositeBackgroundColor());
        GridLayout gl_compositeVariable = new GridLayout(1, false);
        compositeVariable.setLayout(gl_compositeVariable);

        Composite compositeVariableHeader = new Composite(compositeVariable, SWT.NONE);
        compositeVariableHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeVariableHeader = new GridLayout(1, false);
        glCompositeVariableHeader.marginWidth = 0;
        glCompositeVariableHeader.marginHeight = 0;
        compositeVariableHeader.setLayout(glCompositeVariableHeader);

        Label lblCompositeVariableName = new Label(compositeVariableHeader, SWT.NONE);
        lblCompositeVariableName.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblCompositeVariableName.setText(StringConstants.PA_LBL_VAR_BINDING);

        Composite compositeVariableTable = new Composite(compositeVariable, SWT.NONE);
        compositeVariableTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        testCaseVariableTableViewer = new TableViewer(compositeVariableTable, SWT.BORDER | SWT.FULL_SELECTION);
        Table testCaseVariableTable = testCaseVariableTableViewer.getTable();
        testCaseVariableTable.setHeaderVisible(true);
        testCaseVariableTable.setLinesVisible(true);

        TableViewerColumn variableOrderColumnViewer = new TableViewerColumn(testCaseVariableTableViewer, SWT.NONE);
        TableColumn tblclmnVariableNo = variableOrderColumnViewer.getColumn();
        tblclmnVariableNo.setText(StringConstants.PA_TREE_VIEWER_COL_NO);

        TableViewerColumn variableNameColumnViewer = new TableViewerColumn(testCaseVariableTableViewer, SWT.NONE);
        TableColumn tblclmnVariableName = variableNameColumnViewer.getColumn();
        tblclmnVariableName.setText(StringConstants.PA_TREE_VIEWER_COL_NAME);

        TableViewerColumn variableDefaultValueColumnViewer = new TableViewerColumn(testCaseVariableTableViewer,
                SWT.NONE);
        TableColumn tblclmnVaribaleDefaultValue = variableDefaultValueColumnViewer.getColumn();
        tblclmnVaribaleDefaultValue.setText(StringConstants.PA_TREE_VIEWER_COL_DEFAULT_VAL);

        TableViewerColumn variableTypeColumnViewer = new TableViewerColumn(testCaseVariableTableViewer, SWT.NONE);
        TableColumn tblclmnVariableType = variableTypeColumnViewer.getColumn();
        tblclmnVariableType.setText(StringConstants.PA_TREE_VIEWER_COL_TYPE);
        variableTypeColumnViewer.setEditingSupport(new VariableTypeEditingSupport(testCaseVariableTableViewer, this));

        TableViewerColumn variableTestDataLinkIDViewerColumn = new TableViewerColumn(testCaseVariableTableViewer,
                SWT.NONE);
        TableColumn tblclmnTestDataLinkId = variableTestDataLinkIDViewerColumn.getColumn();
        tblclmnTestDataLinkId.setText(StringConstants.PA_TREE_VIEWER_COL_TEST_DATA);
        variableTestDataLinkIDViewerColumn.setEditingSupport(new VariableTestDataLinkColumnEditingSupport(
                testCaseVariableTableViewer, this));

        TableViewerColumn variableValueColumnViewer = new TableViewerColumn(testCaseVariableTableViewer, SWT.NONE);
        TableColumn tblclmnVariableValue = variableValueColumnViewer.getColumn();
        tblclmnVariableValue.setText(StringConstants.PA_TREE_VIEWER_COL_VALUE);
        variableValueColumnViewer.setEditingSupport(new VariableValueEditingSupport(testCaseVariableTableViewer, this));

        TableColumnLayout tableLayout = new TableColumnLayout();
        // Set layout
        tableLayout.setColumnData(tblclmnVariableNo, new ColumnWeightData(0, 40));
        tableLayout.setColumnData(tblclmnVariableName, new ColumnWeightData(15, 90));
        tableLayout.setColumnData(tblclmnVaribaleDefaultValue, new ColumnWeightData(0, 90));
        tableLayout.setColumnData(tblclmnVariableType, new ColumnWeightData(0, 90));
        tableLayout.setColumnData(tblclmnTestDataLinkId, new ColumnWeightData(30, 100));
        tableLayout.setColumnData(tblclmnVariableValue, new ColumnWeightData(15, 90));
        compositeVariableTable.setLayout(tableLayout);

        testCaseVariableTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        testCaseVariableTableViewer.setLabelProvider(new VariableTableLabelProvider(testCaseVariableTableViewer, this));
        sashFormBindingView.setWeights(new int[] { 4, 6 });
    }

    private void createCompositeTestDataToolbar() {
        Composite compositeTestDataButton = new Composite(compositeTestDataDetails, SWT.NONE);
        compositeTestDataButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeTestDataButton = new GridLayout(1, false);
        glCompositeTestDataButton.marginWidth = 0;
        glCompositeTestDataButton.marginHeight = 0;
        compositeTestDataButton.setLayout(glCompositeTestDataButton);

        testDataToolBar = new ToolBar(compositeTestDataButton, SWT.FLAT | SWT.RIGHT);
        ToolItem tltmAddTestData = new ToolItem(testDataToolBar, SWT.DROP_DOWN);
        tltmAddTestData.setText(ToolItemConstants.ADD);
        tltmAddTestData.setToolTipText(ToolItemConstants.ADD);
        tltmAddTestData.setImage(ImageConstants.IMG_24_ADD);

        ToolItem tltmRemoveTestData = new ToolItem(testDataToolBar, SWT.NONE);
        tltmRemoveTestData.setText(ToolItemConstants.REMOVE);
        tltmRemoveTestData.setToolTipText(ToolItemConstants.REMOVE);
        tltmRemoveTestData.setImage(ImageConstants.IMG_24_REMOVE);

        ToolItem tltmUpTestData = new ToolItem(testDataToolBar, SWT.NONE);
        tltmUpTestData.setText(ToolItemConstants.UP);
        tltmUpTestData.setToolTipText(ToolItemConstants.UP);
        tltmUpTestData.setImage(ImageConstants.IMG_24_UP);

        ToolItem tltmDownTestData = new ToolItem(testDataToolBar, SWT.NONE);
        tltmDownTestData.setText(ToolItemConstants.DOWN);
        tltmDownTestData.setToolTipText(ToolItemConstants.DOWN);
        tltmDownTestData.setImage(ImageConstants.IMG_24_DOWN);

        // ToolItem tltmMapTestData = new ToolItem(testDataToolBar, SWT.NONE);
        // tltmMapTestData.setText(TestDataToolItemConstants.MAP);

        ToolItem tltmMapAllTestData = new ToolItem(testDataToolBar, SWT.NONE);
        tltmMapAllTestData.setText(ToolItemConstants.MAPALL);
        tltmMapAllTestData.setToolTipText(ToolItemConstants.MAPALL);
        tltmMapAllTestData.setImage(ImageConstants.IMG_24_MAP_ALL);
    }

    private void createTestDataTreeTable() {
        compositeTestDataTreeTable = new Composite(compositeTestDataDetails, SWT.NONE);
        compositeTestDataTreeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        testDataTreeViewer = new TreeViewer(compositeTestDataTreeTable, SWT.BORDER | SWT.FULL_SELECTION);
        Tree testDataTable = testDataTreeViewer.getTree();
        testDataTable.setLinesVisible(true);
        testDataTable.setHeaderVisible(true);

        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(testDataTreeViewer, SWT.NONE);
        TreeColumn trclmnNo = treeViewerColumn.getColumn();
        trclmnNo.setText(StringConstants.PA_TREE_VIEWER_COL_NO);

        TreeViewerColumn testDataTableViewerColumnID = new TreeViewerColumn(testDataTreeViewer, SWT.NONE);
        TreeColumn tblclmnTestDataId = testDataTableViewerColumnID.getColumn();
        tblclmnTestDataId.setText(StringConstants.PA_TREE_VIEWER_COL_ID);
        testDataTableViewerColumnID.setEditingSupport(new TestDataIDColumnEditingSupport(testDataTreeViewer, this));

        TreeViewerColumn testDataTableViewerColumnIteration = new TreeViewerColumn(testDataTreeViewer, SWT.NONE);
        TreeColumn tblclmnTestDataIteration = testDataTableViewerColumnIteration.getColumn();
        tblclmnTestDataIteration.setText(StringConstants.PA_TREE_VIEWER_COL_DATA_ITERATION);
        testDataTableViewerColumnIteration.setEditingSupport(new TestDataIterationColumnEditingSupport(
                testDataTreeViewer, this));

        TreeViewerColumn testDataTableViewerColumnCombination = new TreeViewerColumn(testDataTreeViewer, SWT.NONE);
        TreeColumn tblclmnCombination = testDataTableViewerColumnCombination.getColumn();
        tblclmnCombination.setText(StringConstants.PA_TREE_VIEWER_COL_TYPE);
        testDataTableViewerColumnCombination.setEditingSupport(new TestDataCombinationColumnEditingSupport(
                testDataTreeViewer, this));

        // Set layout
        TreeColumnLayout treeLayout = new TreeColumnLayout();
        treeLayout.setColumnData(trclmnNo, new ColumnWeightData(0, 40));
        treeLayout.setColumnData(tblclmnTestDataId, new ColumnWeightData(40, 200));
        treeLayout.setColumnData(tblclmnTestDataIteration, new ColumnWeightData(15, 150));
        treeLayout.setColumnData(tblclmnCombination, new ColumnWeightData(0, 80));

        compositeTestDataTreeTable.setLayout(treeLayout);

        // Set data provider
        testDataTreeViewer.setLabelProvider(new TestDataTreeLabelProvider());
        testDataTreeViewer.setContentProvider(new TestDataTreeContentProvider());
    }

    private void createCompositeTestData() {
        compositeTestData = new Composite(sashFormBindingView, SWT.NONE);
        compositeTestData.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        compositeTestData.setBackground(ColorUtil.getCompositeBackgroundColor());

        GridLayout glCompositeTestData = new GridLayout(1, false);
        glCompositeTestData.marginWidth = 0;
        glCompositeTestData.marginHeight = 0;
        compositeTestData.setLayout(glCompositeTestData);

        Composite compositeTestDataHeader = new Composite(compositeTestData, SWT.NONE);
        GridLayout glCompositeTestDataHeader = new GridLayout(2, false);
        glCompositeTestDataHeader.marginWidth = 0;
        glCompositeTestDataHeader.marginHeight = 0;
        compositeTestDataHeader.setLayout(glCompositeTestDataHeader);
        compositeTestDataHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        compositeTestDataHeader.setCursor(compositeTestDataHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandCompositeTestData = new ImageButton(compositeTestDataHeader, SWT.NONE);
        redrawBtnExpandCompositeTestData();

        lblTestDataInformation = new Label(compositeTestDataHeader, SWT.NONE);
        lblTestDataInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblTestDataInformation.setText(StringConstants.PA_LBL_TEST_DATA);
        lblTestDataInformation.setFont(JFaceResources.getFontRegistry().getBold(""));

        compositeTestDataDetails = new Composite(compositeTestData, SWT.NONE);
        compositeTestDataDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeTestDataDetails = new GridLayout(1, false);
        glCompositeTestDataDetails.marginHeight = 0;
        compositeTestDataDetails.setLayout(glCompositeTestDataDetails);

        createCompositeTestDataToolbar();
        createTestDataTreeTable();
    }

    private void redrawBtnExpandCompositeTestData() {
        if (isTestDataCompositeExpanded) {
            btnExpandCompositeTestData.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
        } else {
            btnExpandCompositeTestData.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
        }
    }

    /* package */void layout() {
        layoutTestDataComposite();
    }

    private void layoutTestDataComposite() {
        Display.getDefault().timerExec(10, new Runnable() {
            @Override
            public void run() {
                if (!isTestDataCompositeExpanded) {
                    int compositeTestDataSize = compositeTestData.getChildren()[0].getSize().y + 5;
                    sashFormBindingView.setWeights(new int[] { compositeTestDataSize,
                            sashFormBindingView.getSize().y - compositeTestDataSize });
                } else {
                    sashFormBindingView.setWeights(new int[] { 4, 6 });
                }
                sashFormBindingView.layout(true, true);
                sashFormBindingView.redraw();
                redrawBtnExpandCompositeTestData();
            }

        });
    }

    /* package */void registerControlModifyListeners() {
        // register tool item listener for test data view
        TestDataToolItemListener testDataToolItemListener = new TestDataToolItemListener(testDataTreeViewer, this);
        for (ToolItem item : testDataToolBar.getItems()) {
            item.addSelectionListener(testDataToolItemListener);
        }

        btnExpandCompositeTestData.addListener(SWT.MouseDown, layoutTestDataCompositeListener);
        lblTestDataInformation.addListener(SWT.MouseDown, layoutTestDataCompositeListener);
    }

    /* package */void refreshTestSuiteAfterTestDataChanged(String oldTestDataId, String newTestDataId) {
        for (TestSuiteTestCaseLink testCaseLink : parentView.getTestSuite().getTestSuiteTestCaseLinks()) {
            for (TestCaseTestDataLink testDataLink : testCaseLink.getTestDataLinks()) {
                if (testDataLink.getTestDataId() == null || !(testDataLink.getTestDataId().equals(oldTestDataId)))
                    continue;
                testDataLink.setTestDataId(newTestDataId);
            }

            if (parentView.getSelectedTestCaseLink() != null
                    && getSelectedTestCaseLink().getTestCaseId().equals(testCaseLink.getTestCaseId())) {
                testDataTreeViewer.cancelEditing();
                testCaseVariableTableViewer.cancelEditing();

                testDataTreeViewer.refresh();
                testCaseVariableTableViewer.refresh();
            }
        }
    }

    public TestDataTreeContentProvider getTestDataContentProvider() {
        return (TestDataTreeContentProvider) testDataTreeViewer.getContentProvider();
    }

    public TestSuiteTestCaseLink getSelectedTestCaseLink() {
        return parentView.getSelectedTestCaseLink();
    }

    public void setDirty(boolean dirty) {
        parentView.setDirty(dirty);
    }

    /* package */void updateSelectedTestCase(IStructuredSelection selection) {

        testCaseVariableTableViewer.cancelEditing();
        testCaseVariableTableViewer.getTable().clearAll();

        testDataTreeViewer.cancelEditing();
        testDataTreeViewer.getTree().clearAll(true);

        if (selection.size() == 1) {
            TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) selection.getFirstElement();
            try {
                TestCaseEntity testCaseEntity = TestCaseController.getInstance().getTestCaseByDisplayId(
                        testCaseLink.getTestCaseId());
                if (testCaseEntity != null) {
                    testDataTreeViewer.setInput(testCaseLink.getTestDataLinks());
                    testCaseVariableTableViewer.setInput(testCaseLink.getVariableLinks());
                } else {
                    testDataTreeViewer.setInput(null);
                    testCaseVariableTableViewer.setInput(null);
                    return;
                }

            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }

        } else {
            testDataTreeViewer.setInput(new TestDataLinkTreeNode[0]);
            testCaseVariableTableViewer.setInput(Collections.EMPTY_LIST);
        }
    }

    /* package */void initExpandedState() {
        isTestDataCompositeExpanded = true;
    }

    @SuppressWarnings("unchecked")
    public List<VariableLink> getVariableLinks() {
        return (List<VariableLink>) testCaseVariableTableViewer.getInput();
    }

    public void refreshVariableTable() {
        testCaseVariableTableViewer.refresh();
    }

    public void refreshVariableLink(VariableLink link) {
        testCaseVariableTableViewer.update(link, null);
    }
}
