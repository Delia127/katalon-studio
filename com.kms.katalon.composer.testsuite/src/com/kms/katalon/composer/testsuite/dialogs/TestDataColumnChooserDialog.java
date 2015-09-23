package com.kms.katalon.composer.testsuite.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.testsuite.constants.StringConstants;

public class TestDataColumnChooserDialog extends Dialog {
    private Table table;
    private TableViewer tableViewer;
    private String[] columnNames;
    private String selectedColumnName;
    
    private void setSelectedColumnName(String selectionName) {
        selectedColumnName = selectionName; 
    }
    
    private void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public TestDataColumnChooserDialog(Shell parentShell, String[] columNames, String selectionName) {
        super(parentShell);
        setColumnNames(columNames);
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
        for (int i = 0; i < columnNames.length; i++) {
            ColumnNameIndexPair pair  = new ColumnNameIndexPair(i, columnNames[i]);
            pairLst.add(pair);
            if (selectedColumnName != null && selectedColumnName.equals(pair.getColumnName()))  {
                selection = pair;
            }
        }
        
        tableViewer.setInput(pairLst);
        if (selection != null) {
            tableViewer.setSelection(new StructuredSelection(selection));
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));
        
        tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        TableViewerColumn tableViewerColumnOrder = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnOrder = tableViewerColumnOrder.getColumn();
        tblclmnColumnOrder.setWidth(60);
        tblclmnColumnOrder.setText("No.");
        tableViewerColumnOrder.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == null ||!(element instanceof ColumnNameIndexPair)) {
                    return "";
                }
                return Integer.toString(((ColumnNameIndexPair) element).getIndex() + 1);
            }
        });
        
        TableViewerColumn tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnName = tableViewerColumnName.getColumn();
        tblclmnColumnName.setWidth(320);
        tblclmnColumnName.setText("Column Name");
        tableViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == null ||!(element instanceof ColumnNameIndexPair)) {
                    return "";
                }
                String columnName = ((ColumnNameIndexPair) element).getColumnName();
                return columnName == null ? "" : columnName;
            }
        });
        
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        
        return container;
    }
    
    public String getSelectedColumnName() {
        return selectedColumnName;
    }

    
    @Override
    protected Point getInitialSize() {
        return new Point(400, 400);
    }
    
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.DIA_SHELL_TEST_DATA_LINK_BROWSER);
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
