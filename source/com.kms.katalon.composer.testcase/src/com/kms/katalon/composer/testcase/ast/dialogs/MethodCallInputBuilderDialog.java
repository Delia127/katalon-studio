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
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;

public class MethodCallInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String TO_STRING_METHOD_NAME = "toString";

    private final InputValueType[] defaultObjectValueTypes = { InputValueType.Class, InputValueType.This,
            InputValueType.String, InputValueType.Number, InputValueType.Boolean, InputValueType.Null,
            InputValueType.Variable, InputValueType.MethodCall, InputValueType.Property };

    private static final String THIS_VARIABLE = "this";
    private MethodCallExpressionWrapper methodCallExpression;

    public MethodCallInputBuilderDialog(Shell parentShell, MethodCallExpressionWrapper methodCallExpression) {
        super(parentShell);
        if (methodCallExpression == null) {
            throw new IllegalArgumentException();
        }
        this.methodCallExpression = methodCallExpression.clone();
    }

    @Override
    public MethodCallExpressionWrapper getReturnValue() {
        return methodCallExpression;
    }

    @Override
    public void replaceObject(Object originalObject, Object newObject) {
        if (originalObject == methodCallExpression.getObjectExpression() && newObject instanceof ExpressionWrapper) {
            methodCallExpression.setObjectExpression((ExpressionWrapper) newObject);
            setDefaultMethod(methodCallExpression);
            tableViewer.update(methodCallExpression, null);
        }
    }

    private void setDefaultMethod(MethodCallExpressionWrapper methodCallExpression) {
        Class<?> type = getObjectType(methodCallExpression);
        if (type != null && type.getMethods().length > 0) {
            Method methodNode = type.getMethods()[0];
            methodCallExpression.setMethod(methodNode.getName());
            AstTreeTableInputUtil.generateMethodCallArguments(methodCallExpression, methodNode);
            return;
        }
        methodCallExpression.setMethod(TO_STRING_METHOD_NAME);
        methodCallExpression.setArguments(new ArgumentListExpressionWrapper(methodCallExpression));
    }

    private static Class<?> getObjectType(MethodCallExpressionWrapper methodCallExpression) {
        if (methodCallExpression.getObjectExpression() instanceof VariableExpressionWrapper) {
            return null;
        }
        if (methodCallExpression.getObjectExpression().getText().equals(THIS_VARIABLE)) {
            return methodCallExpression.getScriptClass().getTypeClass();
        }
        return AstTreeTableInputUtil.loadType(methodCallExpression.getObjectExpression().getText(),
                methodCallExpression.getScriptClass());
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_METHOD_CALL_INPUT;
    }

    @Override
    protected void addTableColumns() {
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
        tableViewerColumnObjectType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultObjectValueTypes, this) {
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

        tableViewerColumnObject.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this) {
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
            @Override
            protected void setValue(Object element, Object value) {
                Class<?> type = getObjectType(methodCallExpression);
                if (type != null && value instanceof Method) {
                    Method newMethod = (Method) value;
                    if (MethodComboBoxCellEditor.compareMethodAndMethodCall(newMethod, methodCallExpression)) {
                        return;
                    }
                    methodCallExpression.setMethod(newMethod.getName());
                    AstTreeTableInputUtil.generateMethodCallArguments(methodCallExpression, newMethod);
                    getViewer().refresh();
                } else if (value instanceof String) {
                    methodCallExpression.setMethod((String) value);
                    methodCallExpression.setArguments(new ArgumentListExpressionWrapper(methodCallExpression));
                    getViewer().refresh();
                }
            }

            @Override
            protected Object getValue(Object element) {
                Class<?> type = getObjectType(methodCallExpression);
                if (type != null) {
                    return methodCallExpression;
                }
                return methodCallExpression.getMethodAsString();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                Class<?> type = getObjectType(methodCallExpression);
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
                    argumentListExpression.addExpression(AstTreeTableInputUtil.getArgumentExpression(
                            (InputParameter) inputParameters.get(i), argumentListExpression));
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
                return AstTreeTableInputUtil.generateInputParameters(
                        (ArgumentListExpressionWrapper) methodCallExpression.getArguments(), method);
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new InputCellEditor(tableViewer.getTable(), methodCallExpression.getArguments().getText(),
                        methodCallExpression.getArguments());
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element == methodCallExpression
                        && MethodComboBoxCellEditor.getMethodCallParams(methodCallExpression).length > 0) {
                    return true;
                }
                return false;
            }
        });
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
    public void refresh() {
        List<ExpressionWrapper> expressionList = new ArrayList<ExpressionWrapper>();
        expressionList.add(methodCallExpression);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }
}
