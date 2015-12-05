package com.kms.katalon.composer.testcase.ast.dialogs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
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

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.editors.InputCellEditor;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.model.ICustomInputValueType;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.core.ast.AstTextValueUtil;
import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.core.testdata.TestData;

public class MethodCallInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultObjectValueTypes = { InputValueType.Class, InputValueType.This,
            InputValueType.String, InputValueType.Number, InputValueType.Boolean, InputValueType.Null,
            InputValueType.Variable, InputValueType.MethodCall, InputValueType.Property };
    private static final String THIS_VARIABLE = "this";
    private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_METHOD_CALL_INPUT;
    private MethodCallExpression methodCallExpression;
    private Class<?> type;
    private Method method;
    private MethodNode methodNode;
    private ClassNode scriptClass;
    private boolean isThisClass;

    public MethodCallInputBuilderDialog(Shell parentShell, MethodCallExpression methodCallExpression,
            ClassNode scriptClass) throws Exception {
        super(parentShell, scriptClass);
        method = null;
        type = null;
        this.scriptClass = scriptClass;
        isThisClass = false;
        if (methodCallExpression != null) {
            this.methodCallExpression = GroovyParser.cloneMethodCallExpression(methodCallExpression);
            loadType(methodCallExpression.getObjectExpression());
            if (type != null) {
                for (Method method : type.getMethods()) {
                    if (compareMethodCall(method, methodCallExpression)) {
                        this.method = method;
                        break;
                    }
                }
            } else if (isThisClass) {
                for (MethodNode methodNode : getRealMethodsFromScriptClass(scriptClass)) {
                    if (compareMethodCall(methodNode, methodCallExpression)) {
                        this.methodNode = methodNode;
                        break;
                    }
                }
            }
        } else {
            this.methodCallExpression = new MethodCallExpression(new ConstantExpression(""), "",
                    new ArgumentListExpression());
        }
    }

    private List<MethodNode> getRealMethodsFromScriptClass(ClassNode scriptClass) {
        List<MethodNode> methodList = new ArrayList<MethodNode>();
        for (MethodNode methodNode : scriptClass.getMethods()) {
            if (methodNode.getLineNumber() > -1 && !methodNode.isScriptBody()) {
                methodList.add(methodNode);
            }
        }
        for (MethodNode methodNode : scriptClass.getSuperClass().getMethods()) {
            if (methodNode.getName().equals("print") || methodNode.getName().equals("println")) {
                methodList.add(methodNode);
            }
        }
        return methodList;
    }

    private void loadType(Expression objectExpression) {
        isThisClass = false;
        if (objectExpression instanceof VariableExpression || objectExpression instanceof PropertyExpression) {
            if (objectExpression.getText().equals(THIS_VARIABLE)) {
                type = null;
                isThisClass = true;
            }
            type = AstTreeTableInputUtil.loadType(objectExpression.getText(), scriptClass);
        } else if (objectExpression instanceof ClassExpression) {
            type = objectExpression.getType().getTypeClass();
        } else if (objectExpression instanceof MethodCallExpression) {
            MethodCallExpression parentMethodCall = (MethodCallExpression) objectExpression;
            if (AstTreeTableInputUtil.isTestDataArgument(parentMethodCall)) {
                type = TestData.class;
            }
        }
    }

    private String getTypeName(Class<?> type) {
        if (type.isArray()) {
            try {
                Class<?> cl = type;
                int dimensions = 0;
                while (cl.isArray()) {
                    dimensions++;
                    cl = cl.getComponentType();
                }
                StringBuffer sb = new StringBuffer();
                sb.append(cl.getName());
                for (int i = 0; i < dimensions; i++) {
                    sb.append("[]");
                }
                return sb.toString();
            } catch (Throwable e) { /* FALLTHRU */
            }
        }
        return getTypeNameIfPrimitive(type);
    }

    private String getTypeNameIfPrimitive(Class<?> type) {
        if (type == Boolean.class) {
            return Boolean.TYPE.getName();
        } else if (type == Byte.class) {
            return Byte.TYPE.getName();
        } else if (type == Character.class) {
            return Character.TYPE.getName();
        } else if (type == Short.class) {
            return Short.TYPE.getName();
        } else if (type == Integer.class) {
            return Integer.TYPE.getName();
        } else if (type == Long.class) {
            return Long.TYPE.getName();
        } else if (type == Float.class) {
            return Float.TYPE.getName();
        } else if (type == Double.class) {
            return Double.TYPE.getName();
        }
        return type.getName();
    }

    private String getMethodSignature(Method method) {
        if (method != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(method.getName() + "(");
            List<Class<?>> paramClasses = AstTreeTableInputUtil.getParamClasses(method);
            for (int j = 0; j < paramClasses.size(); j++) {
                sb.append(getTypeName(paramClasses.get(j)));
                if (j < (paramClasses.size() - 1)) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            return sb.toString();
        }
        return "";
    }

    private String getMethodSignature(MethodNode method) {
        if (method != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(method.getName() + "(");
            List<Class<?>> paramClasses = AstTreeTableInputUtil.getParamClasses(method);
            for (int j = 0; j < paramClasses.size(); j++) {
                sb.append(getTypeName(paramClasses.get(j)));
                if (j < (paramClasses.size() - 1)) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            return sb.toString();
        }
        return "";
    }

    private boolean compareMethodCall(Method method, MethodCallExpression methodCall) {
        if (method == null || methodCall == null) {
            return false;
        }
        if (!method.getName().equals(methodCall.getMethodAsString())) {
            return false;
        }

        if (!(methodCall.getArguments() instanceof ArgumentListExpression)) {
            return false;
        }
        ArgumentListExpression expressionList = (ArgumentListExpression) methodCall.getArguments();

        if (method.getParameterTypes().length != expressionList.getExpressions().size()) {
            return false;
        }

        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> methodParameter = method.getParameterTypes()[i];
            Class<?> methodArgument = expressionList.getExpression(i).getType().getTypeClass();
            if (!ClassUtils.isAssignable(methodParameter, methodArgument, true)) {
                return false;
            }
        }
        return true;
    }

    private boolean compareMethodCall(MethodNode method, MethodCallExpression methodCall) {
        if (method == null || methodCall == null) {
            return false;
        }
        if (!method.getName().equals(methodCall.getMethodAsString())) {
            return false;
        }

        if (!(methodCall.getArguments() instanceof ArgumentListExpression)) {
            return false;
        }
        ArgumentListExpression expressionList = (ArgumentListExpression) methodCall.getArguments();

        if (method.getParameters().length != expressionList.getExpressions().size()) {
            return false;
        }

        for (int i = 0; i < method.getParameters().length; i++) {
            Class<?> methodParameter = method.getParameters()[i].getType().getTypeClass();
            Class<?> methodArgument = expressionList.getExpression(i).getType().getTypeClass();
            if (!ClassUtils.isAssignable(methodArgument, methodParameter, true)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MethodCallExpression getReturnValue() {
        return methodCallExpression;
    }

    @Override
    public void changeObject(Object originalObject, Object newObject) {
        if (originalObject == methodCallExpression.getObjectExpression() && newObject instanceof Expression) {
            methodCallExpression.setObjectExpression((Expression) newObject);
            method = null;
            methodNode = null;
            type = null;
            methodCallExpression.setMethod(AstTreeTableEntityUtil.getNewMethodCallExpression().getMethod());
            ArgumentListExpression argumentListExpression = new ArgumentListExpression();
            methodCallExpression.setArguments(argumentListExpression);
            try {
                loadType((Expression) newObject);
                if (isThisClass && !getRealMethodsFromScriptClass(scriptClass).isEmpty()) {
                    methodNode = getRealMethodsFromScriptClass(scriptClass).get(0);
                    methodCallExpression.setMethod(new ConstantExpression(methodNode.getName()));
                    AstTreeTableInputUtil.generateMethodCallArguments(methodCallExpression, methodNode);
                } else if (type != null && type.getMethods().length > 0) {
                    method = type.getMethods()[0];
                    methodCallExpression.setMethod(new ConstantExpression(method.getName()));
                    AstTreeTableInputUtil.generateMethodCallArguments(methodCallExpression, method);

                }
                tableViewer.update(methodCallExpression, null);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    @Override
    public String getDialogTitle() {
        return DIALOG_TITLE;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnObjectType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnObjectType.getColumn().setText(StringConstants.DIA_COL_OBJ_TYPE);
        tableViewerColumnObjectType.getColumn().setWidth(100);
        tableViewerColumnObjectType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass) {
            @Override
            public String getText(Object element) {
                if (element == methodCallExpression) {
                    return super.getText(methodCallExpression.getObjectExpression());
                }
                return "";
            }
        });
        tableViewerColumnObjectType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultObjectValueTypes, ICustomInputValueType.TAG_METHOD_CALL, this, scriptClass) {
            @Override
            protected Object getValue(Object element) {
                if (element == methodCallExpression) {
                    return super.getValue(methodCallExpression.getObjectExpression());
                }
                return null;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element == methodCallExpression) {
                    return super.getCellEditor(methodCallExpression.getObjectExpression());
                }
                return null;
            }

            @Override
            protected void setValue(Object element, Object value) {
                if (element == methodCallExpression) {
                    super.setValue(methodCallExpression.getObjectExpression(), value);
                }
            }
        });

        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnClass = tableViewerColumnObject.getColumn();
        tblclmnNewColumnClass.setText(StringConstants.DIA_COL_OBJ);
        tblclmnNewColumnClass.setWidth(152);
        tableViewerColumnObject.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == methodCallExpression && methodCallExpression.getObjectExpression() != null) {
                    return AstTextValueUtil.getTextValue(methodCallExpression.getObjectExpression());
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnObject
                .setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this, scriptClass) {
                    @Override
                    protected Object getValue(Object element) {
                        if (element == methodCallExpression) {
                            return super.getValue(methodCallExpression.getObjectExpression());
                        }
                        return null;
                    }

                    @Override
                    protected CellEditor getCellEditor(Object element) {
                        if (element == methodCallExpression) {
                            return super.getCellEditor(methodCallExpression.getObjectExpression());
                        }
                        return null;
                    }

                    @Override
                    protected void setValue(Object element, Object value) {
                        if (element == methodCallExpression) {
                            super.setValue(methodCallExpression.getObjectExpression(), value);
                        }
                    }
                });

        TableViewerColumn tableViewerColumnMethod = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnMethod = tableViewerColumnMethod.getColumn();
        tblclmnNewColumnMethod.setText(StringConstants.DIA_COL_METHOD);
        tblclmnNewColumnMethod.setWidth(152);
        tableViewerColumnMethod.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == methodCallExpression && method != null) {
                    return getMethodSignature(method);
                } else if (isThisClass && methodNode != null) {
                    return getMethodSignature(methodNode);
                } else if (type == null) {
                    return methodCallExpression.getMethod().getText();
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnMethod.setEditingSupport(new EditingSupport(tableViewer) {
            private List<String> methodNames = new ArrayList<String>();

            @Override
            protected void setValue(Object element, Object value) {
                if (element == methodCallExpression) {
                    if (value instanceof Integer) {
                        try {
                            int index = (int) value;
                            if (index >= 0 && index < methodNames.size()) {
                                String selectedMethodName = methodNames.get(index);
                                if (isThisClass) {
                                    for (MethodNode childMethod : getRealMethodsFromScriptClass(scriptClass)) {
                                        if (getMethodSignature(childMethod).equals(selectedMethodName)) {
                                            methodNode = childMethod;
                                            methodCallExpression.setMethod(new ConstantExpression(methodNode.getName()));
                                            AstTreeTableInputUtil.generateMethodCallArguments(methodCallExpression,
                                                    methodNode);
                                            tableViewer.update(element, null);
                                            return;
                                        }
                                    }
                                } else if (type != null) {
                                    for (Method childMethod : type.getMethods()) {
                                        if (getMethodSignature(childMethod).equals(selectedMethodName)) {
                                            method = childMethod;
                                            methodCallExpression.setMethod(new ConstantExpression(method.getName()));
                                            AstTreeTableInputUtil.generateMethodCallArguments(methodCallExpression,
                                                    method);
                                            tableViewer.update(element, null);
                                            return;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LoggerSingleton.logError(e);
                        }
                    } else if (value instanceof String && type == null) {
                        methodCallExpression.setMethod(new ConstantExpression(value));
                        tableViewer.update(element, null);
                        return;
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (type != null || isThisClass) {
                    if (isThisClass && methodNode != null) {
                        String currentMethodSignature = getMethodSignature(methodNode);
                        for (int i = 0; i < methodNames.size(); i++) {
                            if (currentMethodSignature.equals(methodNames.get(i))) {
                                return i;
                            }
                        }
                    } else if (method != null) {
                        String currentMethodSignature = getMethodSignature(method);
                        for (int i = 0; i < methodNames.size(); i++) {
                            if (currentMethodSignature.equals(methodNames.get(i))) {
                                return i;
                            }
                        }
                    }
                    return 0;
                } else if (methodCallExpression != null) {
                    return AstTreeTableValueUtil.getValue(methodCallExpression.getMethod(), scriptClass);
                }
                return null;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (type != null || isThisClass) {
                    try {
                        methodNames.clear();
                        if (type != null) {
                            for (Method method : type.getMethods()) {
                                methodNames.add(getMethodSignature(method));
                            }
                        } else {
                            for (MethodNode method : getRealMethodsFromScriptClass(scriptClass)) {
                                methodNames.add(getMethodSignature(method));
                            }
                        }
                        return new ComboBoxCellEditor(tableViewer.getTable(), methodNames
                                .toArray(new String[methodNames.size()]));
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
                } else {
                    return new TextCellEditor(tableViewer.getTable());
                }
                return null;
            }

            @Override
            protected boolean canEdit(Object element) {
                return true;
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
                    return AstTextValueUtil.getTextValue(methodCallExpression.getArguments());
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnInput.setEditingSupport(new EditingSupport(tableViewer) {

            @Override
            protected void setValue(Object element, Object value) {
                if (element != null && element == methodCallExpression && value instanceof List<?>) {
                    try {
                        List<?> inputParameters = (List<?>) value;
                        ArgumentListExpression argumentListExpression = new ArgumentListExpression();
                        for (int i = 0; i < inputParameters.size(); i++) {
                            argumentListExpression.addExpression(AstTreeTableInputUtil
                                    .getArgumentExpression((InputParameter) inputParameters.get(i)));
                        }
                        methodCallExpression.setArguments(argumentListExpression);
                        tableViewer.update(element, null);
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element == methodCallExpression
                        && methodCallExpression.getArguments() instanceof ArgumentListExpression) {
                    try {
                        if (isThisClass) {
                            return AstTreeTableInputUtil.generateInputParameters(
                                    (ArgumentListExpression) methodCallExpression.getArguments(), methodNode);
                        } else {
                            return AstTreeTableInputUtil.generateInputParameters(
                                    (ArgumentListExpression) methodCallExpression.getArguments(), method);
                        }
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
                }
                return StringUtils.EMPTY;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element == methodCallExpression
                        && methodCallExpression.getArguments() instanceof ArgumentListExpression) {
                    return new InputCellEditor(tableViewer.getTable(), AstTextValueUtil
                            .getTextValue(methodCallExpression.getArguments()), scriptClass);
                }
                return null;
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element == methodCallExpression && ((isThisClass && methodNode != null && methodNode
                        .getParameters().length > 0) || (method != null && method.getParameterTypes().length > 0)));
            }
        });
    }

    @Override
    public void refresh() {
        List<Expression> expressionList = new ArrayList<Expression>();
        expressionList.add(methodCallExpression);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }
}
