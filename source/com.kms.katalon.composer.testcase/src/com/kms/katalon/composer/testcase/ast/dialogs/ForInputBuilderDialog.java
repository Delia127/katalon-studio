package com.kms.katalon.composer.testcase.ast.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ForStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstContentProviderAdapter;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.providers.UneditableTableCellLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;

public class ForInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String VARIABLE_LABEL = "Variable";

    private static final String COLLECTION_EXPRESSION_LABEL = "Expression";

    private final InputValueType[] defaultInputValueTypes = AstInputValueTypeOptionsProvider.getInputValueTypeOptions(AstInputValueTypeOptionsProvider.FOR_OPTIONS);

    private ForStatementWrapper forStatement;

    public ForInputBuilderDialog(Shell parentShell, ForStatementWrapper forStatement) {
        super(parentShell);
        this.forStatement = forStatement.clone();
    }

    @Override
    public void setInput() {
        tableViewer.setContentProvider(new AstContentProviderAdapter() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement == forStatement) {
                    return new Object[] { forStatement.getVariable(), forStatement.getCollectionExpression() };
                }
                return new Object[0];
            }
        });
        tableViewer.setInput(forStatement);
    }

    @Override
    public ForStatementWrapper getReturnValue() {
        return forStatement;
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_FOR_INPUT;
    }

    @Override
    protected void addTableColumns() {
        addTableColumnObject();

        addTableColumnValueType();

        addTableColumnValue();
    }

    private void addTableColumnValue() {
        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.getColumn().setWidth(300);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != forStatement.getVariable()) {
                    return super.getText(element);
                }
                if (!ForStatementWrapper.isForLoopDummy(forStatement.getVariable())) {
                    return forStatement.getVariable().getName();
                }
                return "";
            }
        });
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer) {
            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element == forStatement.getVariable()) {
                    return new TextCellEditor(tableViewer.getTable());
                }
                return super.getCellEditor(element);
            }

            @Override
            protected Object getValue(Object element) {
                if (element != forStatement.getVariable()) {
                    return super.getValue(element);
                }
                if (!ForStatementWrapper.isForLoopDummy(forStatement.getVariable())) {
                    return forStatement.getVariable().getName();
                }
                return "";
            }

            @Override
            protected void setValue(Object element, Object value) {
                if (element == forStatement.getVariable()) {
                    if (!(value instanceof String)) {
                        return;
                    }
                    ParameterWrapper newVariable = new ParameterWrapper(Object.class, (String) value, forStatement);
                    newVariable.copyProperties(forStatement.getVariable());
                    forStatement.setVariable(newVariable);
                    getViewer().refresh();
                    return;
                }
                // if element is not for statement variable, then it must be collection expression, passing on
                // collection expression to super class
                super.setValue(element, value);
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element != forStatement.getVariable()) {
                    return super.canEdit(element);
                }
                if (!(forStatement.getCollectionExpression() instanceof ClosureListExpressionWrapper)) {
                    return true;
                }
                return false;
            }
        });
    }

    private void addTableColumnValueType() {
        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider());
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(
                tableViewer, defaultInputValueTypes) {
            @Override
            protected boolean canEdit(Object element) {
                if (element != forStatement.getVariable()) {
                    return true;
                }
                return false;
            }
        });
    }

    private void addTableColumnObject() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnObject.getColumn().setText(StringConstants.DIA_COL_OBJ);
        tableViewerColumnObject.getColumn().setWidth(100);
        tableViewerColumnObject.setLabelProvider(new UneditableTableCellLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == forStatement.getCollectionExpression()) {
                    return COLLECTION_EXPRESSION_LABEL;
                } else if (element == forStatement.getVariable()) {
                    return VARIABLE_LABEL;
                }
                return StringUtils.EMPTY;
            }
        });
    }
}
