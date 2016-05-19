package com.kms.katalon.composer.testcase.ast.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ListExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.model.InputValueTypeUtil;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.providers.UneditableTableCellLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;

public class ListInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultInputValueTypes =  InputValueTypeUtil.getValueTypeOptions(InputValueType.List);

    private ListExpressionWrapper listExpression;

    public ListInputBuilderDialog(Shell parentShell, ListExpressionWrapper listExpression) {
        super(parentShell);
        this.listExpression = listExpression.clone();
    }

    protected void createButtonsForButtonBar(Composite parent) {
        Button btnInsert = createButton(parent, 100, StringConstants.DIA_BTN_INSERT, true);
        btnInsert.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = tableViewer.getTable().getSelectionIndex();
                ExpressionWrapper newExpression = new ConstantExpressionWrapper("", listExpression);
                if (selectionIndex < 0 || selectionIndex >= listExpression.getExpressions().size()) {
                    listExpression.addExpression(newExpression);
                    selectionIndex++;
                } else {
                    listExpression.addExpression(newExpression, selectionIndex);
                }
                tableViewer.refresh();
                tableViewer.getTable().setSelection(selectionIndex);
            }
        });

        Button btnRemove = createButton(parent, 200, StringConstants.DIA_BTN_REMOVE, false);
        btnRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = tableViewer.getTable().getSelectionIndex();
                if (selectionIndex < 0 || selectionIndex >= listExpression.getExpressions().size()) {
                    return;
                }
                listExpression.removeExpression(selectionIndex);
                tableViewer.refresh();
            }
        });

        super.createButtonsForButtonBar(parent);
    }

    @Override
    public ListExpressionWrapper getReturnValue() {
        return listExpression;
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_LIST_INPUT;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnNo = tableViewerColumnNo.getColumn();
        tblclmnColumnNo.setText(StringConstants.DIA_COL_NO);
        tblclmnColumnNo.setWidth(40);
        tableViewerColumnNo.setLabelProvider(new UneditableTableCellLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof ExpressionWrapper) {
                    return String.valueOf(listExpression.getExpressions().indexOf(element) + 1);
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider());
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnValue = tableViewerColumnValue.getColumn();
        tblclmnNewColumnValue.setText(StringConstants.DIA_COL_VALUE);
        tblclmnNewColumnValue.setWidth(170);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer));
    }

    @Override
    public void setInput() {
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(listExpression.getExpressions());
        tableViewer.refresh();
    }
}
