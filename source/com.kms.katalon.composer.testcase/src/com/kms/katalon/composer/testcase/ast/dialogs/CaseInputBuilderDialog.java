package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CaseStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;

public class CaseInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String EXPRESSION_LABEL = "Expression";

    private final InputValueType[] defaultInputValueTypes = { InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Condition, InputValueType.Binary,
            InputValueType.Property, InputValueType.List, InputValueType.Map, InputValueType.Range,
            InputValueType.Class, InputValueType.String, InputValueType.Number, InputValueType.Boolean,
            InputValueType.Null };

    private CaseStatementWrapper caseStatement;

    public CaseInputBuilderDialog(Shell parentShell, CaseStatementWrapper caseStatement) {
        super(parentShell);
        if (caseStatement == null) {
            throw new IllegalArgumentException();
        }
        this.caseStatement = caseStatement.clone();
    }

    @Override
    protected TableViewer createTable(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        TableViewer tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        return tableViewer;
    }

    @Override
    public void refresh() {
        List<ExpressionWrapper> expressionList = new ArrayList<ExpressionWrapper>();
        expressionList.add(caseStatement.getExpression());
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
    }

    @Override
    public CaseStatementWrapper getReturnValue() {
        return caseStatement;
    }

    @Override
    public void replaceObject(Object orginalObject, Object newObject) {
        if (orginalObject == caseStatement.getExpression() && newObject instanceof ExpressionWrapper) {
            caseStatement.setExpression((ExpressionWrapper) newObject);
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_CASE_INPUT;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnObject.getColumn().setText(StringConstants.DIA_COL_OBJ);
        tableViewerColumnObject.getColumn().setWidth(100);
        tableViewerColumnObject.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == caseStatement.getExpression()) {
                    return EXPRESSION_LABEL;
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
