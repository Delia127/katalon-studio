package com.kms.katalon.composer.testsuite.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.core.testdata.TestData;

public class TestDataColumnChooserDialog extends Dialog {
    private Table table;
    private TableViewer tableViewer;
    private String selectedColumnName;
    private Link lblStatus;
    private TestData testData;
    private Label lblImage;
    private Composite container;
    private Composite compositeStatus;


    private void setTestData(TestData testData) {
        // TODO Auto-generated method stub
        this.testData = testData;
    }
    
    private void setSelectedColumnName(String selectionName) {
        selectedColumnName = selectionName;
    }

    public TestDataColumnChooserDialog(Shell parentShell, TestData testData, String selectionName) {
        super(parentShell);
        setTestData(testData);
        setSelectedColumnName(selectionName);
    }

    @Override
    public void create() {
        super.create();
        getShell().setText(StringConstants.DIA_MSG_CHOOSE_DATA_COLUMN);
        setInput();
        registerListeners();
        validate();
    }

    private void registerListeners() {
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                validate();
            }
        });

        tableViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                if (getButton(Dialog.OK).isEnabled()) {
                    okPressed();
                }
            }
        });
        
        lblStatus.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                Program.launch(testData.getSourceUrl());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        });
    }

    private void validate() {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if (selection == null || selection.getFirstElement() == null) {
            getButton(OK).setEnabled(false);
            return;
        }

        ColumnNameIndexPair pair = (ColumnNameIndexPair) selection.getFirstElement();
        if (pair.getColumnName() == null) {
            getButton(OK).setEnabled(false);
        } else {
            getButton(OK).setEnabled(true);
        }
        selectedColumnName = pair.getColumnName();

    }

    private void setInput() {
        List<ColumnNameIndexPair> pairLst = new ArrayList<ColumnNameIndexPair>();
        ColumnNameIndexPair selection = null;
        List<Integer> emptyColumnIndexes = new ArrayList<Integer>();
        String[] columnNames;
        try {
            columnNames = testData.getColumnNames();
        } catch (IOException e) {
            return;
        }
        
        for (int i = 0; i < columnNames.length; i++) {
            ColumnNameIndexPair pair = new ColumnNameIndexPair(i, columnNames[i]);
            pairLst.add(pair);
            if (selectedColumnName != null && selectedColumnName.equals(pair.getColumnName())) {
                selection = pair;
            }

            if (pair.getColumnName() == null || pair.getColumnName().isEmpty()) {
                emptyColumnIndexes.add(i + 1);
            }
        }

        if (emptyColumnIndexes.size() > 0) {
            lblStatus.setToolTipText("Click to open");
            lblStatus.setText("Data source <A>" + testData.getSourceUrl() + "</A> has " + emptyColumnIndexes.size()
                    + " empty column(s) name");
            
        } else {
            lblStatus.getParent().setVisible(false);
            ((GridData) compositeStatus.getLayoutData()).exclude = true;
        }

        tableViewer.setInput(pairLst);
        if (selection != null) {
            tableViewer.setSelection(new StructuredSelection(selection));
        }

        container.layout(true, true);
        container.redraw();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        container = (Composite) super.createDialogArea(parent);
        GridLayout glContainer = new GridLayout(1, false);
        glContainer.verticalSpacing = 10;
        container.setLayout(glContainer);

        compositeStatus = new Composite(container, SWT.NONE);
        GridLayout glComposite = new GridLayout(2, false);
        glComposite.marginHeight = 0;
        compositeStatus.setLayout(glComposite);
        compositeStatus.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        lblImage = new Label(compositeStatus, SWT.NONE);
        lblImage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblImage.setImage(ImageConstants.IMG_16_WARN_TABLE_ITEM);

        lblStatus = new Link(compositeStatus, SWT.READ_ONLY | SWT.WRAP);
        lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        

        tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        table.setHeaderVisible(true);

        TableViewerColumn tableViewerColumnOrder = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnOrder = tableViewerColumnOrder.getColumn();
        tblclmnColumnOrder.setWidth(60);
        tblclmnColumnOrder.setText(StringConstants.NO_);
        tableViewerColumnOrder
                .setLabelProvider(new StyledCellLabelProvider(StyledCellLabelProvider.COLORS_ON_SELECTION) {

                    @Override
                    public void update(ViewerCell cell) {
                        super.update(cell);
                        if (cell.getElement() == null || !(cell.getElement() instanceof ColumnNameIndexPair)) {
                            return;
                        }

                        cell.setText(Integer.toString(((ColumnNameIndexPair) cell.getElement()).getIndex() + 1));

                        String columnName = ((ColumnNameIndexPair) cell.getElement()).getColumnName();
                        if (StringUtils.isEmpty(columnName)) {
                            cell.setImage(ImageConstants.IMG_16_WARN_TABLE_ITEM);
                        }
                    }

                    @Override
                    public String getToolTipText(Object element) {
                        String columnName = ((ColumnNameIndexPair) element).getColumnName();
                        if (StringUtils.isEmpty(columnName)) {
                            return StringConstants.DIA_WARN_MSG_EMPTY_COLUMN_NAME;
                        } else {
                            return Integer.toString(((ColumnNameIndexPair) element).getIndex() + 1);
                        }
                    }

                });

        TableViewerColumn tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnName = tableViewerColumnName.getColumn();
        tblclmnColumnName.setWidth(320);
        tblclmnColumnName.setText("Column Name");
        tableViewerColumnName
                .setLabelProvider(new StyledCellLabelProvider(StyledCellLabelProvider.COLORS_ON_SELECTION) {
                    @Override
                    public void update(ViewerCell cell) {
                        cell.setText(getText(cell.getElement()));
                        Object element = cell.getElement();
                        if (element == null || !(element instanceof ColumnNameIndexPair)) {
                            cell.setForeground(ColorUtil.getErrorTableItemForegroundColor());
                        }

                        String columnName = ((ColumnNameIndexPair) element).getColumnName();
                        if (StringUtils.isEmpty(columnName)) {
                            cell.setForeground(ColorUtil.getErrorTableItemForegroundColor());
                        }

                        super.update(cell);
                    }

                    public String getText(Object element) {
                        if (element == null || !(element instanceof ColumnNameIndexPair)) {
                            return "";
                        }
                        String columnName = ((ColumnNameIndexPair) element).getColumnName();
                        return StringUtils.isEmpty(columnName) ? StringConstants.DIA_TABLE_EMPTY_COLUMN : columnName;
                    }

                    @Override
                    public String getToolTipText(Object element) {
                        String columnName = ((ColumnNameIndexPair) element).getColumnName();
                        if (columnName == null || columnName.isEmpty()) {
                            return StringConstants.DIA_WARN_MSG_EMPTY_COLUMN_NAME;
                        } else {
                            return getText(element);
                        }
                    }
                });

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        tableViewer.getTable().setToolTipText("");
        ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);

        return container;
    }

    public String getSelectedColumnName() {
        return selectedColumnName;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(440, 400);
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.DIA_SHELL_TEST_DATA_LINK_BROWSER);
    }

    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
    }

    private class ColumnNameIndexPair {
        private int index;
        private String columnName;

        public ColumnNameIndexPair(int index, String columnName) {
            this.index = index;
            this.columnName = columnName;
        }

        public int getIndex() {
            return index;
        }

        public String getColumnName() {
            return columnName;
        }
    }
}
