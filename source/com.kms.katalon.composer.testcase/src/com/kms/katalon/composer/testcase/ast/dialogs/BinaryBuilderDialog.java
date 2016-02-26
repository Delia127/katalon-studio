package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.editors.OperationComboBoxCellEditor;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.TokenWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;

public class BinaryBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String OPERATOR_LABEL = "Operator";

    private static final String RIGHT_EXPRESSION_LABEL = "Right Expression";

    private static final String LEFT_EXPRESSION_LABEL = "Left Expression";

    private static final InputValueType[] defaultValueTypes = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.MethodCall,
            InputValueType.Binary, InputValueType.GlobalVariable, InputValueType.TestDataValue, InputValueType.Property };

    private BinaryExpressionWrapper binaryExpressionWrapper;

    public BinaryBuilderDialog(Shell parentShell, BinaryExpressionWrapper binaryExpressionWrapper) {
        super(parentShell);
        if (binaryExpressionWrapper == null) {
            throw new IllegalArgumentException();
        }
        this.binaryExpressionWrapper = binaryExpressionWrapper.clone();
    }

    @Override
    public void refresh() {
        List<ASTNodeWrapper> expressionList = new ArrayList<ASTNodeWrapper>();
        expressionList.add(binaryExpressionWrapper.getLeftExpression());
        expressionList.add(binaryExpressionWrapper.getOperation());
        expressionList.add(binaryExpressionWrapper.getRightExpression());

        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnObject.getColumn().setWidth(100);
        tableViewerColumnObject.getColumn().setText(StringConstants.DIA_COL_OBJ);
        tableViewerColumnObject.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == binaryExpressionWrapper.getLeftExpression()) {
                    return LEFT_EXPRESSION_LABEL;
                } else if (element == binaryExpressionWrapper.getRightExpression()) {
                    return RIGHT_EXPRESSION_LABEL;
                } else if (element == binaryExpressionWrapper.getOperation()) {
                    return OPERATOR_LABEL;
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider());
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultValueTypes, this) {
            @Override
            protected boolean canEdit(Object element) {
                if (element == binaryExpressionWrapper.getOperation()) {
                    return false;
                }
                return super.canEdit(element);
            }
        });

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setWidth(300);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == binaryExpressionWrapper.getOperation()) {
                    return binaryExpressionWrapper.getOperation().getText();
                }
                return super.getText(element);
            }
        });
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this) {
            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element == binaryExpressionWrapper.getOperation()) {
                    return new OperationComboBoxCellEditor((Composite) getViewer().getControl());
                }
                return super.getCellEditor(element);
            }

            @Override
            protected Object getValue(Object element) {
                if (element == binaryExpressionWrapper.getOperation()) {
                    return binaryExpressionWrapper.getOperation();
                }
                return super.getValue(element);
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element == binaryExpressionWrapper.getOperation()) {
                    return true;
                }
                return super.canEdit(element);
            }

            @Override
            protected void setValue(Object element, Object value) {
                if (element == binaryExpressionWrapper.getOperation() && value instanceof TokenWrapper) {
                    getViewer().refresh();
                    return;
                }
                super.setValue(element, value);
            }
        });
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_BINARY_INPUT;
    }

    @Override
    public void replaceObject(Object orginalObject, Object newObject) {
        if (orginalObject == binaryExpressionWrapper.getLeftExpression() && newObject instanceof ExpressionWrapper) {
            binaryExpressionWrapper.setLeftExpression((ExpressionWrapper) newObject);
        } else if (orginalObject == binaryExpressionWrapper.getRightExpression()
                && newObject instanceof ExpressionWrapper) {
            binaryExpressionWrapper.setRightExpression((ExpressionWrapper) newObject);
        } else if (orginalObject == binaryExpressionWrapper.getOperation() && newObject instanceof TokenWrapper) {
            binaryExpressionWrapper.setOperation((TokenWrapper) newObject);
        }
        refresh();
    }

    @Override
    public BinaryExpressionWrapper getReturnValue() {
        return binaryExpressionWrapper;
    }
}
