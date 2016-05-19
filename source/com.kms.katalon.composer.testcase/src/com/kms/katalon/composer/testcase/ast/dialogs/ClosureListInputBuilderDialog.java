package com.kms.katalon.composer.testcase.ast.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.model.InputValueTypeUtil;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.providers.UneditableTableCellLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;

public class ClosureListInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultInputValueTypes = InputValueTypeUtil.getValueTypeOptions(InputValueType.ClosureList);

    private ClosureListExpressionWrapper closureListExpression;

    public ClosureListInputBuilderDialog(Shell parentShell, ClosureListExpressionWrapper closureListExpression) {
        super(parentShell);
        if (closureListExpression == null) {
            throw new IllegalArgumentException();
        }
        this.closureListExpression = closureListExpression.clone();
    }

    @Override
    public ClosureListExpressionWrapper getReturnValue() {
        return closureListExpression;
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_CLOSURE_LIST_INPUT;
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
                    return String.valueOf(closureListExpression.getExpressions().indexOf(element) + 1);
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
        tblclmnNewColumnValue.setWidth(300);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer));

    }

    @Override
    public void setInput() {
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(closureListExpression.getExpressions());
        tableViewer.refresh();
    }
}
