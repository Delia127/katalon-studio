package com.kms.katalon.composer.testcase.ast.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstContentProviderAdapter;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;

public class TestDataValueBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String ROW = "Row";

    private static final String COLUMN = "Column";

    private static final String TEST_DATA = "Test Data";

    private final InputValueType[] defaultInputValueTypes = { InputValueType.Variable, InputValueType.TestData,
            InputValueType.String, InputValueType.Number, InputValueType.Boolean, InputValueType.Null };

    private MethodCallExpressionWrapper methodCallExpression;

    public TestDataValueBuilderDialog(Shell parentShell, MethodCallExpressionWrapper methodCallExpression) {
        super(parentShell);
        this.methodCallExpression = methodCallExpression.clone();
    }

    private ExpressionWrapper getRowExpression() {
        return this.methodCallExpression.getArguments().getExpression(1);
    }

    private ExpressionWrapper getColumnExpression() {
        return this.methodCallExpression.getArguments().getExpression(0);
    }

    private ExpressionWrapper getTestDataExpression() {
        return this.methodCallExpression.getObjectExpression();
    }

    @Override
    public void setInput() {
        tableViewer.setContentProvider(new AstContentProviderAdapter() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement == methodCallExpression) {
                    return new Object[] { getTestDataExpression(), getColumnExpression(), getRowExpression() };
                }
                return new Object[0];
            }
        });
        tableViewer.setInput(methodCallExpression);
    }

    @Override
    public MethodCallExpressionWrapper getReturnValue() {
        return methodCallExpression;
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
                if (element == getTestDataExpression()) {
                    return TEST_DATA;
                } else if (element == getColumnExpression()) {
                    return COLUMN;
                } else if (element == getRowExpression()) {
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
                defaultInputValueTypes));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.getColumn().setWidth(300);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer));
    }
}
