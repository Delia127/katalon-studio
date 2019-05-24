package com.kms.katalon.composer.testsuite.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.control.CMenu;
import com.kms.katalon.composer.components.impl.control.ImageButton;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.MenuUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testsuite.constants.ComposerTestsuiteMessageConstants;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.constants.ToolItemConstants;
import com.kms.katalon.composer.testsuite.listeners.TestDataTableDragListener;
import com.kms.katalon.composer.testsuite.listeners.TestDataTableDropListener;
import com.kms.katalon.composer.testsuite.listeners.TestDataToolItemListener;
import com.kms.katalon.composer.testsuite.providers.TestDataTableLabelProvider;
import com.kms.katalon.composer.testsuite.providers.VariableTableLabelProvider;
import com.kms.katalon.composer.testsuite.support.TestDataCombinationColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.TestDataIDColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.TestDataIterationColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.VariableTestDataLinkColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.VariableTypeEditingSupport;
import com.kms.katalon.composer.testsuite.support.VariableValueEditingSupport;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestSuitePartDataBindingView {

    private TestSuitePartTestCaseView parentView;

    private SashForm sashFormBindingView;

    private Composite compositeTestData;

    private ImageButton btnExpandCompositeTestData;

    private Label lblTestDataInformation;

    private Composite compositeTestDataDetails;

    private ToolBar testDataToolBar;

    private Composite compositeTestDataTreeTable;

    private TableViewer testDataTableViewer;

    private boolean isTestDataCompositeExpanded;

    private Composite compositeVariable;

    private TableViewer testCaseVariableTableViewer;

    private Callable<Boolean> enableWhenItemSelected;

    private CMenu menu;

    private Listener layoutTestDataCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            isTestDataCompositeExpanded = !isTestDataCompositeExpanded;
            layoutTestDataComposite();
        }
    };

    private ToolItem setTypeToolItem;

    private ToolItem setTestDataToolItem;

    public TestSuitePartDataBindingView(TestSuitePartTestCaseView parentView) {
        this.parentView = parentView;
    }

    /* package *//**
                  * @wbp.parser.entryPoint
                  */
    void createCompositeTestDataAndVariable(SashForm sashForm) {

        sashFormBindingView = new SashForm(sashForm, SWT.VERTICAL);
        sashFormBindingView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        sashFormBindingView.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());

        GridLayout glCompositeBindingChild = new GridLayout(1, false);
        glCompositeBindingChild.marginHeight = 0;
        glCompositeBindingChild.marginWidth = 0;
        sashFormBindingView.setLayout(glCompositeBindingChild);

        createCompositeTestData();
        createCompositeVariableBinding();
        setTestDataTableSelection();
    }

    private void setTestDataTableSelection() {
        testDataTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                createDynamicGotoTestDataMenu();

            }
        });

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

        createVariableToolBarComposite(compositeVariable);

        Composite compositeVariableTable = new Composite(compositeVariable, SWT.NONE);
        compositeVariableTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        testCaseVariableTableViewer = new TableViewer(compositeVariableTable,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table testCaseVariableTable = testCaseVariableTableViewer.getTable();
        testCaseVariableTable.setHeaderVisible(true);
        testCaseVariableTable.setLinesVisible(true);
        ColumnViewerUtil.setTableActivation(testCaseVariableTableViewer);

        TableViewerColumn variableNotificationColumnViewer = new TableViewerColumn(testCaseVariableTableViewer,
                SWT.NONE);
        TableColumn tblclmnVariableNotification = variableNotificationColumnViewer.getColumn();
        tblclmnVariableNotification.setImage(ImageConstants.IMG_16_NOTIFICATION_HEADER);
        tblclmnVariableNotification.setToolTipText("Notification");

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
        variableTestDataLinkIDViewerColumn
                .setEditingSupport(new VariableTestDataLinkColumnEditingSupport(testCaseVariableTableViewer, this));

        TableViewerColumn variableValueColumnViewer = new TableViewerColumn(testCaseVariableTableViewer, SWT.NONE);
        TableColumn tblclmnVariableValue = variableValueColumnViewer.getColumn();
        tblclmnVariableValue.setText(StringConstants.PA_TREE_VIEWER_COL_VALUE);
        variableValueColumnViewer.setEditingSupport(new VariableValueEditingSupport(testCaseVariableTableViewer, this));

        TableColumnLayout tableLayout = new TableColumnLayout();
        // Set layout
        tableLayout.setColumnData(tblclmnVariableNotification, new ColumnWeightData(0, 30));
        tableLayout.setColumnData(tblclmnVariableNo, new ColumnWeightData(0, 40));
        tableLayout.setColumnData(tblclmnVariableName, new ColumnWeightData(15, 90));
        tableLayout.setColumnData(tblclmnVaribaleDefaultValue, new ColumnWeightData(0, 90));
        tableLayout.setColumnData(tblclmnVariableType, new ColumnWeightData(0, 120));
        tableLayout.setColumnData(tblclmnTestDataLinkId, new ColumnWeightData(30, 100));
        tableLayout.setColumnData(tblclmnVariableValue, new ColumnWeightData(15, 90));
        compositeVariableTable.setLayout(tableLayout);

        testCaseVariableTableViewer.setContentProvider(ArrayContentProvider.getInstance());

        variableNotificationColumnViewer.setLabelProvider(
                new VariableTableLabelProvider(VariableTableLabelProvider.COLUMN_NOTIFICATION_INDEX, this));

        variableOrderColumnViewer
                .setLabelProvider(new VariableTableLabelProvider(VariableTableLabelProvider.COLUMN_NO_INDEX, this));

        variableNameColumnViewer
                .setLabelProvider(new VariableTableLabelProvider(VariableTableLabelProvider.COLUMN_NAME_INDEX, this));

        variableDefaultValueColumnViewer.setLabelProvider(
                new VariableTableLabelProvider(VariableTableLabelProvider.COLUMN_DEFAULT_VALUE_INDEX, this));

        variableTypeColumnViewer
                .setLabelProvider(new VariableTableLabelProvider(VariableTableLabelProvider.COLUMN_TYPE_INDEX, this));

        variableTestDataLinkIDViewerColumn.setLabelProvider(
                new VariableTableLabelProvider(VariableTableLabelProvider.COLUMN_TEST_DATA_ID_INDEX, this));

        variableValueColumnViewer
                .setLabelProvider(new VariableTableLabelProvider(VariableTableLabelProvider.COLUMN_VALUE_INDEX, this));

        testCaseVariableTableViewer.getTable().setToolTipText("");
        testCaseVariableTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                final ISelection selection = testCaseVariableTableViewer.getSelection();
                setTypeToolItem.setEnabled(!selection.isEmpty());
                if (!(selection instanceof StructuredSelection)) {
                    return;
                }
                StructuredSelection structuredSelection = (StructuredSelection) selection;
                boolean isAnyDataVariableSelected = false;
                for (Object selectedObject : structuredSelection.toArray()) {
                    if (!(selectedObject instanceof VariableLink)) {
                        continue;
                    }
                    VariableLink variableLink = (VariableLink) selectedObject;
                    if (variableLink.getType() == VariableType.DATA_COLUMN || variableLink.getType() == VariableType.DATA_COLUMN_INDEX) {
                        isAnyDataVariableSelected = true;
                        break;
                    }
                }
                setTestDataToolItem.setEnabled(isAnyDataVariableSelected);
            }
        });

        ColumnViewerToolTipSupport.enableFor(testCaseVariableTableViewer, ToolTip.NO_RECREATE);

        sashFormBindingView.setWeights(new int[] { 4, 6 });
    }

    private void createVariableToolBarComposite(Composite parentComposite) {
        Composite variableToolBarComposite = new Composite(parentComposite, SWT.NONE);
        variableToolBarComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_compositeToolbar = new GridLayout(1, false);
        gl_compositeToolbar.marginWidth = 0;
        gl_compositeToolbar.marginHeight = 0;
        variableToolBarComposite.setLayout(gl_compositeToolbar);
        variableToolBarComposite.setBackground(ColorUtil.getCompositeBackgroundColor());

        ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        ToolBar toolBar = toolBarManager.createControl(variableToolBarComposite);

        final Image editImage = ImageConstants.IMG_16_EDIT;
        final Image editDisabledImage = ImageConstants.IMG_16_EDIT_DISABLED;

        setTypeToolItem = new ToolItem(toolBar, SWT.DROP_DOWN);
        setTypeToolItem.setText(ComposerTestsuiteMessageConstants.LBL_SET_TYPE);
        setTypeToolItem.setImage(editImage);
        setTypeToolItem.setDisabledImage(editDisabledImage);
        setTypeToolItem.setEnabled(false);

        Menu setTypeMenu = new Menu(setTypeToolItem.getParent().getShell());
        fillSetTypeToolItemMenu(setTypeMenu);
        setTypeToolItem.setData(setTypeMenu);
        setTypeToolItem.addSelectionListener(new ToolItemDropdownSelectionListener());

        setTestDataToolItem = new ToolItem(toolBar, SWT.DROP_DOWN);
        setTestDataToolItem.setText(ComposerTestsuiteMessageConstants.LBL_SET_TEST_DATA);
        setTestDataToolItem.setImage(editImage);
        setTestDataToolItem.setDisabledImage(editDisabledImage);
        setTestDataToolItem.setEnabled(false);

        setTestDataToolItem.addSelectionListener(new ToolItemDropdownSelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!setTestDataToolItem.isEnabled()) {
                    return;
                }
                Menu setTestDataMenu = new Menu(setTestDataToolItem.getParent().getShell());
                setTestDataToolItem.setData(setTestDataMenu);
                fillSetTestDataToolItemMenu(setTestDataMenu);
                super.widgetSelected(e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void fillSetTypeToolItemMenu(Menu setTypeMenu) {
        Arrays.asList(VariableType.values()).stream().forEach(variableType -> addNewMenuItem(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final ISelection selection = testCaseVariableTableViewer.getSelection();
                if (selection.isEmpty() || !(selection instanceof StructuredSelection)) {
                    return;
                }
                StructuredSelection structuredSelection = (StructuredSelection) selection;
                new ArrayList<Object>(structuredSelection.toList()).stream()
                        .filter(VariableLink.class::isInstance)
                        .map(VariableLink.class::cast)
                        .filter(variableLink -> variableType != variableLink.getType())
                        .forEach(variableLink -> {
                            switch (variableType) {
                                case DATA_COLUMN:
                                case DATA_COLUMN_INDEX:
                                    variableLink.setType(variableType);
                                    variableLink.setValue(StringUtils.EMPTY);
                                case DEFAULT:
                                    variableLink.setTestDataLinkId(StringUtils.EMPTY);
                                    variableLink.setValue(StringUtils.EMPTY);
                                    break;
                                case SCRIPT_VARIABLE:
                                    variableLink.setTestDataLinkId(StringUtils.EMPTY);
                                    Object newValue = InputValueType.Null.newValue();
                                    variableLink.setValue(((ASTNodeWrapper) newValue).getInputText());
                                    break;
                            }
                            variableLink.setType(variableType);
                            testCaseVariableTableViewer.update(variableLink, null);
                            setDirty(true);
                        });
                testCaseVariableTableViewer.setSelection(structuredSelection);

            }
        }, setTypeMenu, variableType.toString(), SWT.PUSH));
    }

    @SuppressWarnings("unchecked")
    private void fillSetTestDataToolItemMenu(Menu setTestDataMenu) {
        final TestSuiteTestCaseLink testCaseLink = getSelectedTestCaseLink();
        if (testCaseLink == null || testCaseLink.getTestDataLinks() == null
                || testCaseLink.getTestDataLinks().isEmpty()) {
            return;
        }
        testCaseLink.getTestDataLinks().stream().forEach(testCaseTestDataLink -> addNewMenuItem(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final ISelection selection = testCaseVariableTableViewer.getSelection();
                if (selection.isEmpty() || !(selection instanceof StructuredSelection)) {
                    return;
                }
                StructuredSelection structuredSelection = (StructuredSelection) selection;
                new ArrayList<Object>(structuredSelection.toList()).stream()
                        .filter(VariableLink.class::isInstance)
                        .map(VariableLink.class::cast)
                        .filter(variableLink -> (variableLink.getType() == VariableType.DATA_COLUMN
                                || variableLink.getType() == VariableType.DATA_COLUMN_INDEX)
                                && (!testCaseTestDataLink.getId().equals(variableLink.getTestDataLinkId())))
                        .forEach(variableLink -> {
                            variableLink.setTestDataLinkId(testCaseTestDataLink.getId());
                            variableLink.setValue("");
                            testCaseVariableTableViewer.update(variableLink, null);
                            setDirty(true);
                        });
                testCaseVariableTableViewer.setSelection(structuredSelection);

            }
        }, setTestDataMenu, testCaseTestDataLink.getTestDataId(), SWT.PUSH));
    }

    private static MenuItem addNewMenuItem(SelectionListener selectionListener, Menu actionMenu, String text,
            int type) {
        MenuItem newMenuItem = new MenuItem(actionMenu, type);
        newMenuItem.setText(text);
        newMenuItem.addSelectionListener(selectionListener);
        return newMenuItem;
    }

    private void createCompositeTestDataToolbar() {
        Composite compositeTestDataButton = new Composite(compositeTestDataDetails, SWT.NONE);
        compositeTestDataButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeTestDataButton = new GridLayout(1, false);
        glCompositeTestDataButton.marginWidth = 0;
        glCompositeTestDataButton.marginHeight = 0;
        compositeTestDataButton.setLayout(glCompositeTestDataButton);

        testDataToolBar = new ToolBar(compositeTestDataButton, SWT.FLAT | SWT.RIGHT);
        testDataToolBar.setForeground(ColorUtil.getToolBarForegroundColor());
        ToolItem tltmAddTestData = new ToolItem(testDataToolBar, SWT.DROP_DOWN);
        tltmAddTestData.setText(ToolItemConstants.ADD);
        tltmAddTestData.setToolTipText(ToolItemConstants.ADD);
        tltmAddTestData.setImage(ImageConstants.IMG_16_ADD);

        ToolItem tltmRemoveTestData = new ToolItem(testDataToolBar, SWT.NONE);
        tltmRemoveTestData.setText(ToolItemConstants.REMOVE);
        tltmRemoveTestData.setToolTipText(ToolItemConstants.REMOVE);
        tltmRemoveTestData.setImage(ImageConstants.IMG_16_REMOVE);

        ToolItem tltmUpTestData = new ToolItem(testDataToolBar, SWT.NONE);
        tltmUpTestData.setText(ToolItemConstants.UP);
        tltmUpTestData.setToolTipText(ToolItemConstants.UP);
        tltmUpTestData.setImage(ImageConstants.IMG_16_MOVE_UP);

        ToolItem tltmDownTestData = new ToolItem(testDataToolBar, SWT.NONE);
        tltmDownTestData.setText(ToolItemConstants.DOWN);
        tltmDownTestData.setToolTipText(ToolItemConstants.DOWN);
        tltmDownTestData.setImage(ImageConstants.IMG_16_MOVE_DOWN);

        ToolItem tltmMapAllTestData = new ToolItem(testDataToolBar, SWT.NONE);
        tltmMapAllTestData.setText(ToolItemConstants.MAPALL);
        tltmMapAllTestData.setToolTipText(ComposerTestsuiteMessageConstants.TOOLTIP_MAP_ALL);
        tltmMapAllTestData.setImage(ImageConstants.IMG_16_MAP_ALL);
    }

    private void createTestDataTreeTable() {
        compositeTestDataTreeTable = new Composite(compositeTestDataDetails, SWT.NONE);
        compositeTestDataTreeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        testDataTableViewer = new TableViewer(compositeTestDataTreeTable, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table testDataTable = testDataTableViewer.getTable();
        testDataTable.setLinesVisible(true);
        testDataTable.setHeaderVisible(true);
        ColumnViewerUtil.setTableActivation(testDataTableViewer);

        TableViewerColumn treeViewerColumnNotification = new TableViewerColumn(testDataTableViewer, SWT.NONE);
        TableColumn trclmnNotification = treeViewerColumnNotification.getColumn();
        trclmnNotification.setImage(ImageConstants.IMG_16_NOTIFICATION_HEADER);
        trclmnNotification.setToolTipText(GlobalStringConstants.NOTIFICATION);

        TableViewerColumn treeViewerColumnNo = new TableViewerColumn(testDataTableViewer, SWT.NONE);
        TableColumn trclmnNo = treeViewerColumnNo.getColumn();
        trclmnNo.setText(StringConstants.PA_TREE_VIEWER_COL_NO);

        TableViewerColumn testDataTableViewerColumnID = new TableViewerColumn(testDataTableViewer, SWT.NONE);
        TableColumn tblclmnTestDataId = testDataTableViewerColumnID.getColumn();
        tblclmnTestDataId.setText(StringConstants.PA_TREE_VIEWER_COL_ID);
        testDataTableViewerColumnID.setEditingSupport(new TestDataIDColumnEditingSupport(testDataTableViewer, this));

        TableViewerColumn testDataTableViewerColumnIteration = new TableViewerColumn(testDataTableViewer, SWT.NONE);
        TableColumn tblclmnTestDataIteration = testDataTableViewerColumnIteration.getColumn();
        tblclmnTestDataIteration.setText(StringConstants.PA_TREE_VIEWER_COL_DATA_ITERATION);
        testDataTableViewerColumnIteration
                .setEditingSupport(new TestDataIterationColumnEditingSupport(testDataTableViewer, this));

        TableViewerColumn testDataTableViewerColumnCombination = new TableViewerColumn(testDataTableViewer, SWT.NONE);
        TableColumn tblclmnCombination = testDataTableViewerColumnCombination.getColumn();
        tblclmnCombination.setText(StringConstants.PA_TREE_VIEWER_COL_TYPE);
        testDataTableViewerColumnCombination
                .setEditingSupport(new TestDataCombinationColumnEditingSupport(testDataTableViewer, this));

        // Set layout
        TableColumnLayout treeLayout = new TableColumnLayout();
        treeLayout.setColumnData(trclmnNotification, new ColumnWeightData(0, 30));
        treeLayout.setColumnData(trclmnNo, new ColumnWeightData(0, 40));
        treeLayout.setColumnData(tblclmnTestDataId, new ColumnWeightData(40, 200));
        treeLayout.setColumnData(tblclmnTestDataIteration, new ColumnWeightData(15, 150));
        treeLayout.setColumnData(tblclmnCombination, new ColumnWeightData(0, 80));

        compositeTestDataTreeTable.setLayout(treeLayout);

        testDataTableViewer.getTable().setToolTipText("");
        ColumnViewerToolTipSupport.enableFor(testDataTableViewer, ToolTip.NO_RECREATE);

        testDataTableViewer.setContentProvider(ArrayContentProvider.getInstance());

        // Set data provider
        treeViewerColumnNotification.setLabelProvider(
                new TestDataTableLabelProvider(TestDataTableLabelProvider.COLUMN_NOTIFICATION_INDEX, this));
        treeViewerColumnNo
                .setLabelProvider(new TestDataTableLabelProvider(TestDataTableLabelProvider.COLUMN_ORDER_INDEX, this));
        testDataTableViewerColumnID
                .setLabelProvider(new TestDataTableLabelProvider(TestDataTableLabelProvider.COLUMN_ID_INDEX, this));
        testDataTableViewerColumnIteration.setLabelProvider(
                new TestDataTableLabelProvider(TestDataTableLabelProvider.COLUMN_ITERATION_INDEX, this));
        testDataTableViewerColumnCombination.setLabelProvider(
                new TestDataTableLabelProvider(TestDataTableLabelProvider.COLUMN_COMBINATION_INDEX, this));

        hookDropTestDataEvent();
        hookDragTestDataEvent();
        createTestDataTableContextMenu(testDataTableViewer.getTable());
        setTestDataTableSelection();
    }

    private void createTestDataTableContextMenu(Table table) {
        menu = new CMenu(table, null);
        table.setMenu(menu);

        enableWhenItemSelected = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !testDataTableViewer.getSelection().isEmpty();
            }
        };

    }

    private void hookDropTestDataEvent() {
        DropTarget dt = new DropTarget(testDataTableViewer.getTable(), DND.DROP_MOVE | DND.DROP_COPY);
        // List<Transfer> treeEntityTransfers = new ArrayList<Transfer>();
        List<Transfer> treeEntityTransfers = TransferTypeCollection.getInstance().getTreeEntityTransfer();
        treeEntityTransfers.add(TextTransfer.getInstance());
        dt.setTransfer(treeEntityTransfers.toArray(new Transfer[treeEntityTransfers.size()]));
        dt.addDropListener(new TestDataTableDropListener(testDataTableViewer, this));
    }

    private void hookDragTestDataEvent() {
        int operations = DND.DROP_MOVE | DND.DROP_COPY;
        DragSource dragSource = new DragSource(testDataTableViewer.getTable(), operations);
        dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
        dragSource.addDragListener(new TestDataTableDragListener(testDataTableViewer));
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
        GridLayout glCompositeTestDataHeader = new GridLayout(3, false);
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
            btnExpandCompositeTestData.setImage(ImageConstants.IMG_16_ARROW_DOWN);
        } else {
            btnExpandCompositeTestData.setImage(ImageConstants.IMG_16_ARROW);
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
        TestDataToolItemListener testDataToolItemListener = new TestDataToolItemListener(testDataTableViewer, this);
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
                testDataTableViewer.cancelEditing();
                testCaseVariableTableViewer.cancelEditing();

                testDataTableViewer.refresh();
                testCaseVariableTableViewer.refresh();
            }
        }
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

        testDataTableViewer.cancelEditing();
        testDataTableViewer.getTable().clearAll();

        if (selection.size() == 1) {
            TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) selection.getFirstElement();
            testDataTableViewer.setInput(testCaseLink.getTestDataLinks());

            try {
                if (TestCaseController.getInstance().getTestCaseByDisplayId(testCaseLink.getTestCaseId()) != null) {
                    testCaseVariableTableViewer.setInput(testCaseLink.getVariableLinks());
                } else {
                    testCaseVariableTableViewer.setInput(null);
                }
            } catch (Exception e) {
                testCaseVariableTableViewer.setInput(null);
            }

        } else {
            testDataTableViewer.setInput(null);
            testCaseVariableTableViewer.setInput(null);
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

    private void createDynamicGotoTestDataMenu() {
        ControlUtils.removeOldOpenMenuItem(menu);
        IStructuredSelection selection = (IStructuredSelection) testDataTableViewer.getSelection();
        List<DataFileEntity> dataFileEntities = getListDataFileFromSelection(selection);
        SelectionAdapter openSubMenuSelected = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Object source = e.getSource();
                if (!(source instanceof MenuItem)) {
                    return;
                }
                DataFileEntity dataFileEntity = getDataFileFromMenuItem((MenuItem) source);
                if (dataFileEntity != null) {
                    parentView.openAddedTestData(dataFileEntity);
                }
            }
        };
        if (dataFileEntities.size() == 1) {
            ControlUtils.createOpenMenuWhenSelectOnlyOne(menu, dataFileEntities.get(0), enableWhenItemSelected,
                    openSubMenuSelected);
            return;
        }
        MenuUtils.createOpenTestArtifactsMenu(getMapFileEntityToSelectionAdapter(dataFileEntities, openSubMenuSelected),
                menu);
    }

    private DataFileEntity getDataFileFromMenuItem(MenuItem selectedMenuItem) {
        DataFileEntity selectedTestData = null;
        if (selectedMenuItem.getData() instanceof DataFileEntity) {
            selectedTestData = (DataFileEntity) selectedMenuItem.getData();
        }
        return selectedTestData;
    }

    private List<DataFileEntity> getListDataFileFromSelection(IStructuredSelection selection) {
        List<DataFileEntity> fileEntities = new ArrayList<DataFileEntity>();
        TestDataController controller = TestDataController.getInstance();
        for (Object object : selection.toList()) {
            if (!(object instanceof TestCaseTestDataLink)) {
                continue;
            }
            try {
                fileEntities.add(controller.getTestDataByDisplayId(((TestCaseTestDataLink) object).getTestDataId()));
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        return fileEntities;
    }

    private HashMap<FileEntity, SelectionAdapter> getMapFileEntityToSelectionAdapter(
            List<? extends FileEntity> fileEntities, SelectionAdapter openTestData) {
        HashMap<FileEntity, SelectionAdapter> map = new HashMap<>();
        for (FileEntity fileEntity : fileEntities) {
            if (fileEntity instanceof DataFileEntity) {
                map.put(fileEntity, openTestData);
            }
        }
        return map;
    }

    private class ToolItemDropdownSelectionListener extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent selectionEvent) {
            Object item = selectionEvent.getSource();
            if (!(item instanceof ToolItem)) {
                return;
            }
            ToolItem toolItem = (ToolItem) item;
            if (!toolItem.isEnabled()) {
                return;
            }
            if (toolItem.getData() instanceof Menu) {
                Rectangle rect = toolItem.getBounds();
                Point pt = toolItem.getParent().toDisplay(new Point(rect.x, rect.y));
                Menu menu = (Menu) toolItem.getData();
                menu.setLocation(pt.x, pt.y + rect.height);
                menu.setVisible(true);
            }
        }
    }
}
