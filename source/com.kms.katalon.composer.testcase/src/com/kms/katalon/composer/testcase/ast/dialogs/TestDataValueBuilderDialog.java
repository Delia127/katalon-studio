package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;

public class TestDataValueBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String ROW = "Row";
    private static final String COLUMN = "Column";
    private static final String TEST_DATA = "Test Data";
    private final InputValueType[] defaultInputValueTypes = { InputValueType.Variable, InputValueType.TestData,
            InputValueType.String, InputValueType.Number, InputValueType.Boolean, InputValueType.Null };

    private MethodCallExpressionWrapper methodCallExpression;
    private ExpressionWrapper testDataExpression;
    private ExpressionWrapper columnExpression;
    private ExpressionWrapper rowExpression;

    public TestDataValueBuilderDialog(Shell parentShell, MethodCallExpressionWrapper methodCallExpression) {
        super(parentShell);
        if (methodCallExpression == null || !AstEntityInputUtil.isTestDataValueArgument(methodCallExpression)) {
            throw new IllegalArgumentException();
        }
        this.methodCallExpression = methodCallExpression.clone();
        testDataExpression = this.methodCallExpression.getObjectExpression();
        columnExpression = ((ArgumentListExpressionWrapper) this.methodCallExpression.getArguments()).getExpression(0);
        rowExpression = ((ArgumentListExpressionWrapper) this.methodCallExpression.getArguments()).getExpression(1);
    }

    @Override
    public void refresh() {
        List<Object> expressionList = new ArrayList<Object>();
        expressionList.add(testDataExpression);
        expressionList.add(columnExpression);
        expressionList.add(rowExpression);

        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
    }

    @Override
    public MethodCallExpressionWrapper getReturnValue() {
        return methodCallExpression;
    }

    @Override
    public void replaceObject(Object orginalObject, Object newObject) {
        if (!(newObject instanceof ExpressionWrapper)) {
            return;
        }
        ExpressionWrapper newExpression = (ExpressionWrapper) newObject;
        if (orginalObject == testDataExpression) {
            methodCallExpression.setObjectExpression(newExpression);
            testDataExpression = newExpression;
            refresh();
        } else if (orginalObject == columnExpression) {
            ((ArgumentListExpressionWrapper) methodCallExpression.getArguments()).getExpressions().set(0,
                    (ExpressionWrapper) newObject);
            columnExpression = newExpression;
            refresh();
        } else if (orginalObject == rowExpression) {
            ((ArgumentListExpressionWrapper) methodCallExpression.getArguments()).getExpressions().set(1,
                    (ExpressionWrapper) newObject);
            rowExpression = newExpression;
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_TEST_DATA_VALUE_INPUT;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnObject.getColumn().setText(StringConstants.DIA_COL_OBJ);
        tableViewerColumnObject.getColumn().setWidth(100);
        tableViewerColumnObject.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == testDataExpression) {
                    return TEST_DATA;
                } else if (element == columnExpression) {
                    return COLUMN;
                } else if (element == rowExpression) {
                    return ROW;
                }
                return StringUtils.EMPTY;
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
