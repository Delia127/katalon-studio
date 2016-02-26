package com.kms.katalon.composer.testcase.ast.dialogs;

import org.codehaus.groovy.ast.ClassNode;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.components.util.ColumnViewerUtil;

public abstract class AbstractAstBuilderWithTableDialog extends Dialog implements AstBuilderDialog {
    protected TableViewer tableViewer;

    protected AbstractAstBuilderWithTableDialog _instance;

    protected ClassNode scriptClass;

    public AbstractAstBuilderWithTableDialog(Shell parent, ClassNode scriptClass) {
        super(parent);
        _instance = this;
        this.scriptClass = scriptClass;
    }

    protected TableViewer createTable(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        ColumnViewerUtil.setTableActivation(tableViewer);
        return tableViewer;
    }

    /***
     * sub classes need to override this method to add columns to table
     */
    protected abstract void addTableColumns();

    /***
     * sub classes need to override this method to refresh the table
     */
    public abstract void refresh();

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));
        tableViewer = createTable(container);
        addTableColumns();
        refresh();
        return container;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        Button btnOK = createButton(parent, 102, IDialogConstants.OK_LABEL, false);
        btnOK.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _instance.close();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(700, 500);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getDialogTitle());
    }

}
