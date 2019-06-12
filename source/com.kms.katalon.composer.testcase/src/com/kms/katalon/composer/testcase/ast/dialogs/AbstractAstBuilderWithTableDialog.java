package com.kms.katalon.composer.testcase.ast.dialogs;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;

public abstract class AbstractAstBuilderWithTableDialog extends AbstractAstBuilderDialog {
    protected TableViewer tableViewer;

    public AbstractAstBuilderWithTableDialog(Shell parent) {
        super(parent);
    }

    protected TableViewer createTable(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        table.setHeaderVisible(true);
        setTableActivation(tableViewer);
        return tableViewer;
    }
    
    protected void setTableActivation(TableViewer tableViewer) {
        ColumnViewerUtil.setTableActivation(tableViewer);
    }

    /***
     * sub classes need to override this method to add columns to table
     */
    protected abstract void addTableColumns();

    /**
     * Refresh the input
     */
    protected abstract void setInput();

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));
        tableViewer = createTable(container);
        addTableColumns();
        ColumnViewerToolTipSupport.enableFor(tableViewer);
        setInput();
        return container;
    }
    
    @Override
    protected void okPressed() {
        tableViewer.applyEditorValue();
        super.okPressed();
    }
}
