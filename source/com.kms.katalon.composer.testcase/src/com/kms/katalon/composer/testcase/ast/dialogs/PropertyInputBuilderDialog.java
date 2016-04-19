package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;

public class PropertyInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultObjectInputValueTypes = { InputValueType.Class, InputValueType.Variable };

    private PropertyExpressionWrapper propertyExpression;

    public PropertyInputBuilderDialog(Shell parentShell, PropertyExpressionWrapper propertyExpression) {
        super(parentShell);
        this.propertyExpression = propertyExpression.clone();
    }

    @Override
    public void setInput() {
        tableViewer.setContentProvider(new ArrayContentProvider());
        List<ASTNodeWrapper> expressionList = new ArrayList<ASTNodeWrapper>();
        expressionList.add(propertyExpression);
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }

    @Override
    public PropertyExpressionWrapper getReturnValue() {
        return propertyExpression;
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_PROPERTY_INPUT;
    }

    @Override
    protected void addTableColumns() {
        addTableColumnObjectType();

        addTableColumnObject();

        addTableColumnProperty();
    }

    private void addTableColumnProperty() {
        TableViewerColumn tableViewerColumnProperty = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnProperty = tableViewerColumnProperty.getColumn();
        tblclmnNewColumnProperty.setText(StringConstants.DIA_COL_PROPERTY);
        tblclmnNewColumnProperty.setWidth(152);
        tableViewerColumnProperty.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == propertyExpression) {
                    return propertyExpression.getPropertyAsString();
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnProperty.setEditingSupport(new EditingSupport(tableViewer) {
            Class<?> type;

            String[] fieldNames;

            @Override
            protected void setValue(Object element, Object value) {
                if (type != null && fieldNames != null && fieldNames.length > 0 && value instanceof Integer
                        && (int) value >= 0 && (int) value < fieldNames.length) {
                    propertyExpression.setProperty(fieldNames[(int) value]);
                    getViewer().refresh();
                    return;
                }
                if (value instanceof String) {
                    propertyExpression.setProperty((String) value);
                    getViewer().refresh();
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (type == null || fieldNames == null || fieldNames.length == 0) {
                    return propertyExpression.getPropertyAsString();
                }
                for (int i = 0; i < fieldNames.length; i++) {
                    if (propertyExpression.getPropertyAsString().equals(fieldNames[i])) {
                        return i;
                    }
                }
                return 0;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (type != null && fieldNames != null && fieldNames.length > 0) {
                    return new ComboBoxCellEditor(tableViewer.getTable(), fieldNames);
                }
                return new TextCellEditor(tableViewer.getTable());
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element != propertyExpression) {
                    return false;
                }
                type = AstKeywordsInputUtil.loadType(propertyExpression.getObjectExpressionAsString(),
                        propertyExpression.getScriptClass());
                if (type != null) {
                    fieldNames = new String[type.getFields().length];
                    for (int index = 0; index < type.getFields().length; index++) {
                        fieldNames[index] = type.getFields()[index].getName();
                    }
                }
                return true;
            }
        });
    }

    private void addTableColumnObject() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnObject = tableViewerColumnObject.getColumn();
        tblclmnNewColumnObject.setWidth(152);
        tblclmnNewColumnObject.setText(StringConstants.DIA_COL_OBJ);
        tableViewerColumnObject.setLabelProvider(new AstInputValueLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == propertyExpression) {
                    return super.getText(propertyExpression.getObjectExpression());
                }
                return "";
            }
        });

        tableViewerColumnObject.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer) {
            @Override
            protected CellEditor getCellEditor(Object element) {
                return super.getCellEditor(propertyExpression.getObjectExpression());
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element == propertyExpression && super.canEdit(propertyExpression.getObjectExpression()));
            }

            @Override
            protected void setValue(Object element, Object value) {
                super.setValue(propertyExpression.getObjectExpression(), value);
            }

            @Override
            protected Object getValue(Object element) {
                return super.getValue(propertyExpression.getObjectExpression());
            }
        });
    }

    private void addTableColumnObjectType() {
        TableViewerColumn tableViewerColumnObjectType = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tableColumnObjectType = tableViewerColumnObjectType.getColumn();
        tableColumnObjectType.setWidth(152);
        tableColumnObjectType.setText(StringConstants.DIA_COL_OBJ_TYPE);
        tableViewerColumnObjectType.setLabelProvider(new AstInputTypeLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == propertyExpression) {
                    return super.getText(propertyExpression.getObjectExpression());
                }
                return "";
            }
        });

        tableViewerColumnObjectType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(
                tableViewer, defaultObjectInputValueTypes) {
            @Override
            protected CellEditor getCellEditor(Object element) {
                return super.getCellEditor(propertyExpression.getObjectExpression());
            }

            @Override
            protected boolean canEdit(Object element) {
                return element == propertyExpression;
            }

            @Override
            protected void setValue(Object element, Object value) {
                super.setValue(propertyExpression.getObjectExpression(), value);
            }

            @Override
            protected Object getValue(Object element) {
                return super.getValue(propertyExpression.getObjectExpression());
            }
        });
    }
}
