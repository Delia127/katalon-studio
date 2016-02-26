package com.kms.katalon.composer.testcase.ast.dialogs;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;

public class KeysInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultInputValueTypes = { InputValueType.String, InputValueType.Key };

    private MethodCallExpressionWrapper methodCallExpression;
    private ArgumentListExpressionWrapper argumentListExpression;

    public KeysInputBuilderDialog(Shell parentShell, MethodCallExpressionWrapper methodCallExpression) {
        super(parentShell);
        if (methodCallExpression == null
                || !(methodCallExpression.getArguments() instanceof ArgumentListExpressionWrapper)) {
            throw new IllegalArgumentException();
        }
        this.methodCallExpression = methodCallExpression.clone();
        this.argumentListExpression = (ArgumentListExpressionWrapper) this.methodCallExpression.getArguments();
    }

    protected void createButtonsForButtonBar(Composite parent) {
        Button btnInsert = createButton(parent, 100, StringConstants.INSERT, true);
        btnInsert.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = tableViewer.getTable().getSelectionIndex();
                ExpressionWrapper newExpression = (ExpressionWrapper) InputValueType.Key
                        .getNewValue(argumentListExpression);
                if (selectionIndex < 0 || selectionIndex >= argumentListExpression.getExpressions().size()) {
                    argumentListExpression.getExpressions().add(newExpression);
                    tableViewer.getTable().setSelection(argumentListExpression.getExpressions().size() - 1);
                } else {
                    argumentListExpression.getExpressions().add(selectionIndex, newExpression);
                    tableViewer.getTable().setSelection(selectionIndex + 1);
                }
                tableViewer.refresh();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Button btnRemove = createButton(parent, 200, StringConstants.DELETE, false);
        btnRemove.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = tableViewer.getTable().getSelectionIndex();
                if (index >= 0 && index < argumentListExpression.getExpressions().size()) {
                    argumentListExpression.getExpressions().remove(index);
                    tableViewer.refresh();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        super.createButtonsForButtonBar(parent);
    }

    @Override
    public MethodCallExpressionWrapper getReturnValue() {
        return methodCallExpression;
    }

    @Override
    public void replaceObject(Object originalObject, Object newObject) {
        int index = argumentListExpression.getExpressions().indexOf(originalObject);
        if (newObject instanceof ExpressionWrapper && argumentListExpression.getExpressions() != null && index >= 0
                && index < argumentListExpression.getExpressions().size()) {
            argumentListExpression.getExpressions().set(index, (ExpressionWrapper) newObject);
            tableViewer.refresh();
        }
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
        tableViewerColumnNo.setLabelProvider(new ColumnLabelProvider() {
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
                defaultInputValueTypes, this));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnValue = tableViewerColumnValue.getColumn();
        tblclmnNewColumnValue.setWidth(200);
        tblclmnNewColumnValue.setText(StringConstants.VALUE);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this));

    }

    @Override
    public void refresh() {
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(argumentListExpression.getExpressions());
        tableViewer.refresh();
    }
}