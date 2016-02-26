package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.SwitchStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;

public class SwitchInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String EXPRESSION = "Expression";

    private final InputValueType[] defaultInputValueTypes = { InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Condition, InputValueType.Binary,
            InputValueType.Property, InputValueType.List, InputValueType.Map, InputValueType.Range,
            InputValueType.Class, InputValueType.String, InputValueType.Number, InputValueType.Boolean,
            InputValueType.Null };

    private SwitchStatementWrapper switchStatement;

    public SwitchInputBuilderDialog(Shell parentShell, SwitchStatementWrapper switchStatement) {
        super(parentShell);
        if (switchStatement == null) {
            throw new IllegalArgumentException();
        }
        this.switchStatement = switchStatement.clone();
    }

    @Override
    public void refresh() {
        List<ExpressionWrapper> expressionList = new ArrayList<ExpressionWrapper>();
        expressionList.add(switchStatement.getExpression());
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
    }

    @Override
    public SwitchStatementWrapper getReturnValue() {
        return switchStatement;
    }

    @Override
    public void replaceObject(Object orginalObject, Object newObject) {
        if (orginalObject == switchStatement.getExpression() && newObject instanceof ExpressionWrapper) {
            switchStatement.setExpression((ExpressionWrapper) newObject);
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_SWITCH_INPUT;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnObject.getColumn().setText(StringConstants.DIA_COL_OBJ);
        tableViewerColumnObject.getColumn().setWidth(100);
        tableViewerColumnObject.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == switchStatement.getExpression()) {
                    return EXPRESSION;
                }
                return "";
            }
        });

        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider());
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes, this));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.getColumn().setWidth(300);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this));

    }
}
