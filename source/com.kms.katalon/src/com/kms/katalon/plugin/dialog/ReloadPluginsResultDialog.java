package com.kms.katalon.plugin.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.plugin.models.ResultItem;

public class ReloadPluginsResultDialog extends Dialog {

    private List<ResultItem> result;

    protected ReloadPluginsResultDialog(Shell parentShell) {
        super(parentShell);
    }

    public ReloadPluginsResultDialog(Shell shell, List<ResultItem> result) {
        this(shell);
        this.result = result;
    }

    @Override
    protected Control createDialogArea(Composite parent) { 
        Composite body = new Composite(parent, SWT.BORDER);
        body.setLayout(new GridLayout(1, false));
        body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        Label lblPluginDetails = new Label(body, SWT.NONE);
        lblPluginDetails.setText(StringConstants.ReloadPluginsResultDialog_LBL_PLUGIN_DETAILS);
        
        Composite tableComposite = new Composite(body, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tableComposite.setLayout(new FillLayout());
        
        TableViewer tvDetails = new TableViewer(tableComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        tvDetails.setContentProvider(ArrayContentProvider.getInstance());
        Table tDetails = tvDetails.getTable();
        tDetails.setHeaderVisible(true);
        tDetails.setLinesVisible(true);
        
        TableViewerColumn cvPlugin = new TableViewerColumn(tvDetails, SWT.LEFT);
        TableColumn cPlugin = cvPlugin.getColumn();
        cPlugin.setWidth(200);
        cPlugin.setText(StringConstants.ReloadPluginsResultDialog_COL_PLUGIN);
        cvPlugin.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((ResultItem) element).getPlugin().getProduct().getName();
            }
        });
        
        TableViewerColumn cvStatus = new TableViewerColumn(tvDetails, SWT.LEFT);
        TableColumn cStatus = cvStatus.getColumn();
        cStatus.setText(StringConstants.ReloadPluginsResultDialog_COL_STATUS);
        cStatus.setWidth(100);
        cvStatus.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ResultItem item = (ResultItem) element;
                if (item.isInstalled()) {
                    return StringConstants.ReloadPluginsResultDialog_STATUS_INSTALLED;
                } else {
                    return StringConstants.ReloadPluginsResultDialog_STATUS_UNINSTALLED;
                }
            }
        });
        
        tvDetails.setInput(result);
        
        Button btnCancel = new Button(body, SWT.NONE);
        btnCancel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
        btnCancel.setText(IDialogConstants.CANCEL_LABEL);
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ReloadPluginsResultDialog.this.setReturnCode(Dialog.CANCEL);
                ReloadPluginsResultDialog.this.close();
            }
        });
        
        return body;
    }
    
    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.ReloadPluginsResultDialog_DIA_TITLE);
    }
    
    @Override
    protected Point getInitialSize() {
        Point initialSize = super.getInitialSize();
        return new Point(Math.max(500, initialSize.x), Math.max(300, initialSize.y));
    }
    
    
    @Override
    protected boolean isResizable() {
        return true;
    }
}
