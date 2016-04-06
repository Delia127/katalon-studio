package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ForStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;

public class ForInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String INDEX_VARIABLE_NAME = "index";

    private static final String VARIABLE_LABEL = "Variable";

    private static final String COLLECTION_EXPRESSION_LABEL = "Expression";

    private final InputValueType[] defaultInputValueTypes = { InputValueType.Range, InputValueType.ClosureList,
            InputValueType.List, InputValueType.Map, InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.Property };

    private ForStatementWrapper forStatement;

    public ForInputBuilderDialog(Shell parentShell, ForStatementWrapper forStatement) {
        super(parentShell);
        if (forStatement == null) {
            throw new IllegalArgumentException();
        }
        this.forStatement = forStatement.clone();
    }

    @Override
    public void refresh() {
        List<ASTNodeWrapper> expressionList = new ArrayList<ASTNodeWrapper>();
        expressionList.add(forStatement.getVariable());
        expressionList.add(forStatement.getCollectionExpression());
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }

    @Override
    public ForStatementWrapper getReturnValue() {
        return forStatement;
    }

    @Override
    public void replaceObject(Object originalObject, Object newObject) {
        if (originalObject == forStatement.getCollectionExpression() && newObject instanceof ExpressionWrapper) {
            ExpressionWrapper newCollectionExpression = (ExpressionWrapper) newObject;
            ParameterWrapper variable = forStatement.getVariable();
            if (newCollectionExpression instanceof ClosureListExpressionWrapper) {
                variable = new ParameterWrapper(ForStatement.FOR_LOOP_DUMMY, forStatement);
            } else if (ForStatementWrapper.isForLoopDummy(variable)) {
                variable = new ParameterWrapper(Object.class, INDEX_VARIABLE_NAME, forStatement);
            }
            variable.copyProperties(forStatement.getVariable());
            forStatement.setVariable(variable);
            forStatement.setCollectionExpression(newCollectionExpression);
            refresh();
        } else if (originalObject == forStatement.getVariable() && newObject instanceof ParameterWrapper) {
            forStatement.setVariable((ParameterWrapper) newObject);
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_FOR_INPUT;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnObject.getColumn().setText(StringConstants.DIA_COL_OBJ);
        tableViewerColumnObject.getColumn().setWidth(100);
        tableViewerColumnObject.setLabelProvider(new ColumnLabelProvider() {
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

        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider());
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes, this) {
            @Override
            protected boolean canEdit(Object element) {
                if (element != forStatement.getVariable()) {
                    return true;
                }
                return false;
            }
        });

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
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this) {
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
                    parentDialog.replaceObject(element, newVariable);
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
}
