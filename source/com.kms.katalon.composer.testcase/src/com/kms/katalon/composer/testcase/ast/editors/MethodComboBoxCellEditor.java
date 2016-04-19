package com.kms.katalon.composer.testcase.ast.editors;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;

public class MethodComboBoxCellEditor extends ComboBoxCellEditor {
    private Method[] methods;

    public MethodComboBoxCellEditor(Composite parent, Class<?> type) {
        super(parent, new String[0]);
        methods = type.getMethods();
        String[] methodSignatures = new String[methods.length];
        for (int index = 0; index < methods.length; index++) {
            methodSignatures[index] = getMethodSignature(methods[index]);
        }
        setItems(methodSignatures);
    }

    /**
     * Accept a {@link MethodCallExpressionWrapper} object
     * 
     * @param a
     * {@link MethodCallExpressionWrapper} object
     */
    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof MethodCallExpressionWrapper);
        MethodCallExpressionWrapper methodCall = (MethodCallExpressionWrapper) value;
        for (int index = 0; index < methods.length; index++) {
            if (compareMethodAndMethodCall(methods[index], methodCall)) {
                super.doSetValue(index);
                return;
            }
        }
        super.doSetValue(0);
    }

    public static boolean compareMethodAndMethodCall(Method methodNode, MethodCallExpressionWrapper methodCall) {
        return methodCall.getMethodAsString().equals(methodNode.getName())
                && compareArguments(methodNode.getParameterTypes(), getMethodCallParams(methodCall));
    }

    /**
     * @return the selected {@link Method}
     */
    @Override
    protected Method doGetValue() {
        Integer selectionIndex = (Integer) super.doGetValue();
        if (selectionIndex < 0 || selectionIndex >= methods.length) {
            return null;
        }
        return methods[selectionIndex];
    }

    public static String getMethodSignature(Method method) {
        if (method == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(method.getName() + "(");
        List<Class<?>> paramClasses = getParamClasses(method);
        for (int j = 0; j < paramClasses.size(); j++) {
            sb.append(getTypeName(paramClasses.get(j)));
            if (j < (paramClasses.size() - 1)) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private static String getTypeName(Class<?> type) {
        if (!type.isArray()) {
            return getTypeNameIfPrimitive(type);
        }
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
    }

    private static String getTypeNameIfPrimitive(Class<?> type) {
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

    public static Class<?>[] getMethodCallParams(MethodCallExpressionWrapper methodCall) {
        ArgumentListExpressionWrapper argumentList = methodCall.getArguments();
        Class<?>[] methodCallParam = new Class<?>[argumentList.getExpressions().size()];
        for (int index = 0; index < argumentList.getExpressions().size(); index++) {
            methodCallParam[index] = argumentList.getExpression(index).getType().getTypeClass();
        }
        return methodCallParam;
    }

    private static boolean compareArguments(Class<?>[] methodClassParams, Class<?>[] methodCallParams) {
        if (methodClassParams.length != methodCallParams.length) {
            return false;
        }
        for (int i = 0; i < methodClassParams.length; i++) {
            Class<?> methodClassParam = changeToPrimitiveTypeIfPossible(methodClassParams[i]);
            Class<?> methodCallParam = changeToPrimitiveTypeIfPossible(methodCallParams[i]);
            if (!ClassUtils.isAssignable(methodCallParam, methodClassParam, true)) {
                return false;
            }
        }
        return true;
    }

    private static Class<?> changeToPrimitiveTypeIfPossible(Class<?> type) {
        if (type == null || type.isPrimitive()) {
            return type;
        }
        Class<?> primitiveType = ClassUtils.wrapperToPrimitive(type);
        if (primitiveType != null) {
            return primitiveType;
        }
        return type;
    }
    
    public static List<Class<?>> getParamClasses(Method method) {
        if (method == null) {
            return Collections.emptyList();
        }
        List<Class<?>> parameterClasses = new ArrayList<Class<?>>();
        for (Type type : method.getGenericParameterTypes()) {
            if (type instanceof Class<?> || isMapType(type)) {
                parameterClasses.add(((Class<?>) type));
            }
        }
        return parameterClasses;
    }

    private static boolean isMapType(Type type) {
        return type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() instanceof Class<?>
                && ((Class<?>) ((ParameterizedType) type).getRawType()).getName().equals(Map.class.getName());
    }
}
