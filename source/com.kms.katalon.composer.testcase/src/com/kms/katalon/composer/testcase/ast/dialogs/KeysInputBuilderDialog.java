package com.kms.katalon.composer.testcase.ast.dialogs;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.providers.UneditableTableCellLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;

public class KeysInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultInputValueTypes = AstInputValueTypeOptionsProvider.getInputValueTypeOptions(InputValueType.Keys);

    private MethodCallExpressionWrapper methodCallExpression;

    private ArgumentListExpressionWrapper argumentListExpression;

    public KeysInputBuilderDialog(Shell parentShell, MethodCallExpressionWrapper methodCallExpression) {
        super(parentShell);
        this.methodCallExpression = methodCallExpression.clone();
        this.argumentListExpression = (ArgumentListExpressionWrapper) this.methodCallExpression.getArguments();
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createInsertButton(parent);
        createRemoveButton(parent);

        super.createButtonsForButtonBar(parent);
    }

    private void createRemoveButton(Composite parent) {
        Button btnRemove = createButton(parent, 200, StringConstants.DELETE, false);
        btnRemove.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = tableViewer.getTable().getSelectionIndex();
                if (index >= 0 && index < argumentListExpression.getExpressions().size()) {
                    argumentListExpression.removeExpression(index);
                    tableViewer.refresh();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private void createInsertButton(Composite parent) {
        Button btnInsert = createButton(parent, 100, StringConstants.INSERT, true);
        btnInsert.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = tableViewer.getTable().getSelectionIndex();
                ExpressionWrapper newExpression = (ExpressionWrapper) InputValueType.Key.getNewValue(argumentListExpression);
                if (selectionIndex < 0 || selectionIndex >= argumentListExpression.getExpressions().size()) {
                    argumentListExpression.addExpression(newExpression);
                    tableViewer.getTable().setSelection(argumentListExpression.getExpressions().size() - 1);
                } else {
                    argumentListExpression.addExpression(newExpression, selectionIndex);
                    tableViewer.getTable().setSelection(selectionIndex + 1);
                }
                tableViewer.refresh();
            }
        });
    }

    @Override
    public MethodCallExpressionWrapper getReturnValue() {
        return methodCallExpression;
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.KEYS_BUILDER_DIALOG_TITLE;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnNo = tableViewerColumnNo.getColumn();
        tblclmnColumnNo.setWidth(40);
        tblclmnColumnNo.setText(StringConstants.NO_);
        tableViewerColumnNo.setLabelProvider(new UneditableTableCellLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof ExpressionWrapper) {
                    return String.valueOf(argumentListExpression.getExpressions().indexOf(element) + 1);
                }
                return "";
            }
        });

        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnValueType = tableViewerColumnValueType.getColumn();
        tblclmnNewColumnValueType.setWidth(200);
        tblclmnNewColumnValueType.setText(StringConstants.KEYS_BUILDER_TABLE_COLUMN_TYPE_LABEL);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider());
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnValue = tableViewerColumnValue.getColumn();
        tblclmnNewColumnValue.setWidth(200);
        tblclmnNewColumnValue.setText(StringConstants.VALUE);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer));

    }

    @Override
    public void setInput() {
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(argumentListExpression.getExpressions());
        tableViewer.refresh();
    }
    
    @Override
    public void create() {
        super.create();
        addUnfocusEventListener();
    }
    
    private void addUnfocusEventListener() {
        Table table = tableViewer.getTable();
        table.addListener(SWT.MouseDown, event -> {
            Point pt = new Point(event.x, event.y);
            TableItem item = table.getItem(pt);
            if (item == null) {
                table.setSelection(-1);
            }
        });
    }
}
