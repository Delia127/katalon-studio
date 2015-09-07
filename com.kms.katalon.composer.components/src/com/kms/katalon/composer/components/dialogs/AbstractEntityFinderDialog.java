package com.kms.katalon.composer.components.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.constants.StringConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;

public abstract class AbstractEntityFinderDialog extends Dialog{
    private static final String DEFAULT_LABEL_VALUE = StringConstants.DIA_DEFAULT_LBL_VAL;
	private static final String COLUMN_OBJECT_NAME_HEADER = StringConstants.DIA_OBJ_HEADER_NAME;
    private static final String COLUMN_OBJECT_LOCATION_HEADER = StringConstants.DIA_OBJ_HEADER_LOC;

    protected String message = StringConstants.DIA_MESSAGE;
    protected String title;
    
    private Table table;
    private TableViewer tableViewer;
    
    private String searchText = "";
    private Object selectedObject;
    
    private AbstractEntityFinderDialog _instance;
    
    private List<Object> input;
    
    private ColumnLabelProvider[] columnLabelProviders;
    
    private ViewerFilter filter;

    protected AbstractEntityFinderDialog(Shell parentShell) {
        super(parentShell);
        _instance = this;
        columnLabelProviders = new ColumnLabelProvider[2];
    }

    @SuppressWarnings({ "static-access", "restriction" })
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        createMessageArea(container);
        createFilterText(container);

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setText(DEFAULT_LABEL_VALUE);

        tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableViewerColumn tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnName = tableViewerColumnName.getColumn();
        tblclmnName.setWidth(100);
        tblclmnName.setText(COLUMN_OBJECT_NAME_HEADER);
        tblclmnName.setWidth(250);
        tableViewerColumnName.setLabelProvider(columnLabelProviders[0]);

        TableViewerColumn tableViewerColumnPK = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnPK = tableViewerColumnPK.getColumn();
        tblclmnPK.setWidth(100);
        tblclmnPK.setText(COLUMN_OBJECT_LOCATION_HEADER);
        tblclmnPK.setWidth(317);
        tableViewerColumnPK.setLabelProvider(columnLabelProviders[1]);

        //hook double click to table item event
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            
            @Override
            public void doubleClick(DoubleClickEvent event) {
                selectedObject = ((StructuredSelection) event.getSelection()).getFirstElement();
                _instance.close();
            }
        });
        
        //set table data
        try {

            tableViewer.setContentProvider(new ArrayContentProvider().getInstance());
            tableViewer.setInput(input);

            if (input.size() > 0) {
                setSelectionIndex(0);
            }

            tableViewer.addFilter(filter);
            
            tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    // TODO Auto-generated method stub
                    selectedObject = ((StructuredSelection) event.getSelection()).getFirstElement();
                }
            });
            setInitObject();

        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }

        return container;
    }
    
    protected abstract void setInitObject() throws Exception;

    protected Label createMessageArea(Composite composite) {
        Label label = new Label(composite, SWT.NONE);
        if (message != null) {
            label.setText(message);
        }

        GridData data = new GridData();
        data.grabExcessVerticalSpace = false;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.BEGINNING;
        label.setLayoutData(data);

        return label;
    }

    protected Text createFilterText(Composite parent) {
        Text text = new Text(parent, SWT.BORDER);

        GridData data = new GridData();
        data.grabExcessVerticalSpace = false;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.BEGINNING;
        text.setLayoutData(data);
        text.setFont(parent.getFont());

        text.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                Text source = (Text) e.getSource();
                searchText = source.getText();

                tableViewer.refresh();

                // update selection index
                if (table.getItems().length > 0) {
                    setSelectionIndex(0);
                }
            }

        });

        text.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.ARROW_DOWN) {
                    table.setFocus();
                }

            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub

            }
        });

        return text;
    }

    // overriding this methods allows you to set the
    // title of the custom dialog
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if (title != null) {
            newShell.setText(title);
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(600, 500);
    }

    public Object getSelectedValue() {
        return selectedObject;
    }

    protected void setSelectionIndex(int index) {
    	table.setSelection(index);
        selectedObject = (Object) table.getItem(index).getData();
    }
    
    protected void setColumnLabelProviders(ColumnLabelProvider[] columnLabelProviders) {
        this.columnLabelProviders = columnLabelProviders;
    }
    
    protected void setTitle(String title) {
        this.title = title;
    }
    
    protected void setInput(List<Object> input) {
        this.input = input;
    }
    
    protected List<Object> getInput() {
        return input;
    }
    
    protected void setViewerFilter(ViewerFilter filter) {
        this.filter = filter;
    }
    
    protected String getSearchText() {
        return searchText;
    }

}
