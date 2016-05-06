package com.kms.katalon.composer.testcase.ast.dialogs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.testcase.ast.editors.InputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.MethodComboBoxCellEditor;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.model.InputValueTypeUtil;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;

public class MethodCallInputBuilderDialog extends AbstractAstBuilderWithTableDialog {

    private final InputValueType[] defaultObjectValueTypes = InputValueTypeUtil.getValueTypeOptions(InputValueType.MethodCall);

    private MethodCallExpressionWrapper methodCallExpression;

    public MethodCallInputBuilderDialog(Shell parentShell, MethodCallExpressionWrapper methodCallExpression) {
        super(parentShell);
        this.methodCallExpression = methodCallExpression.clone();
    }

    @Override
    public ASTNodeWrapper getReturnValue() {
        return methodCallExpression;
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_METHOD_CALL_INPUT;
    }

    @Override
    protected void addTableColumns() {
        addTableColumnObjectType();

        addTableColumnObject();

        addTableColumnMethod();

        addTableColumnInput();
    }

    private void addTableColumnObjectType() {
        TableViewerColumn tableViewerColumnObjectType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnObjectType.getColumn().setText(StringConstants.DIA_COL_OBJ_TYPE);
        tableViewerColumnObjectType.getColumn().setWidth(100);
        tableViewerColumnObjectType.setLabelProvider(new AstInputTypeLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == methodCallExpression) {
                    return super.getText(methodCallExpression.getObjectExpression());
                }
                return "";
            }
        });
        tableViewerColumnObjectType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(
                tableViewer, defaultObjectValueTypes) {
            @Override
            protected Object getValue(Object element) {
                return super.getValue(methodCallExpression.getObjectExpression());
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return super.getCellEditor(methodCallExpression.getObjectExpression());
            }

            @Override
            protected void setValue(Object element, Object value) {
                super.setValue(methodCallExpression.getObjectExpression(), value);
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element == methodCallExpression && super.canEdit(methodCallExpression.getObjectExpression()));
            }
        });
    }

    private void addTableColumnObject() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnClass = tableViewerColumnObject.getColumn();
        tblclmnNewColumnClass.setText(StringConstants.DIA_COL_OBJ);
        tblclmnNewColumnClass.setWidth(152);
        tableViewerColumnObject.setLabelProvider(new AstInputValueLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == methodCallExpression) {
                    return super.getText(methodCallExpression.getObjectExpression());
                }
                return "";
            }
        });

        tableViewerColumnObject.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer) {
            @Override
            protected Object getValue(Object element) {
                return super.getValue(methodCallExpression.getObjectExpression());
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return super.getCellEditor(methodCallExpression.getObjectExpression());
            }

            @Override
            protected void setValue(Object element, Object value) {
                super.setValue(methodCallExpression.getObjectExpression(), value);
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element == methodCallExpression && super.canEdit(methodCallExpression.getObjectExpression()));
            }

            @Override
            protected void handleUpdateInputSuccessfully() {
                resetDefaultMethod(methodCallExpression);
                super.handleUpdateInputSuccessfully();
            }
        });
    }

    private void addTableColumnMethod() {
        TableViewerColumn tableViewerColumnMethod = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnMethod = tableViewerColumnMethod.getColumn();
        tblclmnNewColumnMethod.setText(StringConstants.DIA_COL_METHOD);
        tblclmnNewColumnMethod.setWidth(152);
        tableViewerColumnMethod.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == methodCallExpression) {
                    return methodCallExpression.getMethodAsString();
                }
                return "";
            }
        });

        tableViewerColumnMethod.setEditingSupport(new EditingSupport(tableViewer) {
            private Class<?> type = null;

            @Override
            protected void setValue(Object element, Object value) {
                if (type != null && value instanceof Method) {
                    Method newMethod = (Method) value;
                    if (MethodComboBoxCellEditor.compareMethodAndMethodCall(newMethod, methodCallExpression)) {
                        return;
                    }
                    methodCallExpression.setMethod(newMethod.getName());
                    AstKeywordsInputUtil.generateMethodCallArguments(methodCallExpression, newMethod);
                    getViewer().refresh();
                } else if (value instanceof String) {
                    methodCallExpression.setMethod((String) value);
                    methodCallExpression.setArguments(new ArgumentListExpressionWrapper(methodCallExpression));
                    getViewer().refresh();
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (type != null) {
                    return methodCallExpression;
                }
                return methodCallExpression.getMethodAsString();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                type = getObjectType(methodCallExpression);
                if (type != null) {
                    return new MethodComboBoxCellEditor(tableViewer.getTable(), type);
                }
                return new TextCellEditor(tableViewer.getTable());
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element == methodCallExpression);
            }
        });
    }

    private void addTableColumnInput() {
        TableViewerColumn tableViewerColumnInput = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnInput = tableViewerColumnInput.getColumn();
        tblclmnNewColumnInput.setText(StringConstants.DIA_COL_INPUT);
        tblclmnNewColumnInput.setWidth(170);
        tableViewerColumnInput.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == methodCallExpression) {
                    return methodCallExpression.getArguments().getText();
                }
                return "";
            }
        });

        tableViewerColumnInput.setEditingSupport(new EditingSupport(tableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (!(value instanceof List<?>)) {
                    return;
                }
                List<?> inputParameters = (List<?>) value;
                ArgumentListExpressionWrapper argumentListExpression = new ArgumentListExpressionWrapper(
                        methodCallExpression);
                for (int i = 0; i < inputParameters.size(); i++) {
                    if (!(inputParameters.get(i) instanceof InputParameter)) {
                        continue;
                    }
                    InputParameter inputParameter = (InputParameter) inputParameters.get(i);
                    argumentListExpression.addExpression(inputParameter.getValueAsExpression());
                }
                methodCallExpression.setArguments(argumentListExpression);
                tableViewer.refresh();
            }

            @Override
            protected Object getValue(Object element) {
                Method method = findMethod(getObjectType(methodCallExpression), methodCallExpression);
                if (method == null) {
                    return null;
                }
                return AstKeywordsInputUtil.generateInputParameters(method,
                        (ArgumentListExpressionWrapper) methodCallExpression.getArguments());
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new InputCellEditor(tableViewer.getTable(), methodCallExpression.getArguments().getText(),
                        methodCallExpression.getArguments());
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element == methodCallExpression && MethodComboBoxCellEditor.getMethodCallParams(methodCallExpression).length > 0);
            }
        });
    }

    public void resetDefaultMethod(MethodCallExpressionWrapper methodCall) {
        Class<?> type = getObjectType(methodCall);
        if (type != null && type.getMethods().length > 0) {
            Method methodNode = type.getMethods()[0];
            methodCall.setMethod(methodNode.getName());
            AstKeywordsInputUtil.generateMethodCallArguments(methodCall, methodNode);
            return;
        }
        methodCall.setMethod(MethodCallExpressionWrapper.TO_STRING_METHOD_NAME);
        methodCall.setArguments(new ArgumentListExpressionWrapper(methodCall));
    }

    public Class<?> getObjectType(MethodCallExpressionWrapper methodCall) {
        if (methodCall.getObjectExpression().getText().equals(MethodCallExpressionWrapper.THIS_VARIABLE)) {
            return methodCall.getScriptClass().getTypeClass();
        }
        return AstKeywordsInputUtil.loadType(methodCall.getObjectExpression().getText(), methodCall.getScriptClass());
    }

    private static Method findMethod(Class<?> type, MethodCallExpressionWrapper methodCall) {
        if (type == null || methodCall == null) {
            return null;
        }
        for (int index = 0; index < type.getMethods().length; index++) {
            if (MethodComboBoxCellEditor.compareMethodAndMethodCall(type.getMethods()[index], methodCall)) {
                return type.getMethods()[index];
            }
        }
        return null;
    }

    @Override
    public void setInput() {
        List<ExpressionWrapper> expressionList = new ArrayList<ExpressionWrapper>();
        expressionList.add(methodCallExpression);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }
}
