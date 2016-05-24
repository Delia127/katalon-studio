package com.kms.katalon.composer.testcase.ast.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.editors.OperationComboBoxCellEditor;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.TokenWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstContentProviderAdapter;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.providers.UneditableTableCellLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;

public class BinaryBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String OPERATOR_LABEL = "Operator";

    private static final String RIGHT_EXPRESSION_LABEL = "Right Expression";

    private static final String LEFT_EXPRESSION_LABEL = "Left Expression";

    private static final InputValueType[] defaultValueTypes = AstInputValueTypeOptionsProvider.getInputValueTypeOptions(InputValueType.Binary);

    private BinaryExpressionWrapper binaryExpressionWrapper;

    public BinaryBuilderDialog(Shell parentShell, BinaryExpressionWrapper binaryExpressionWrapper) {
        super(parentShell);
        this.binaryExpressionWrapper = binaryExpressionWrapper.clone();
    }

    @Override
    public void setInput() {
        tableViewer.setContentProvider(new AstContentProviderAdapter() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement == binaryExpressionWrapper) {
                    return new Object[] { binaryExpressionWrapper.getLeftExpression(),
                            binaryExpressionWrapper.getOperation(), binaryExpressionWrapper.getRightExpression() };
                }
                return new Object[0];
            }
        });
        tableViewer.setInput(binaryExpressionWrapper);
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnObject.getColumn().setWidth(100);
        tableViewerColumnObject.getColumn().setText(StringConstants.DIA_COL_OBJ);
        tableViewerColumnObject.setLabelProvider(new UneditableTableCellLabelProvider() {
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
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(
                tableViewer, defaultValueTypes) {
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
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer) {
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
                if (element == binaryExpressionWrapper.getOperation()
                        && value instanceof TokenWrapper
                        && !StringUtils.equals(binaryExpressionWrapper.getOperation().getText(),
                                ((TokenWrapper) value).getText())) {
                    binaryExpressionWrapper.setOperation((TokenWrapper) value);
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
    public BinaryExpressionWrapper getReturnValue() {
        return binaryExpressionWrapper;
    }
}
