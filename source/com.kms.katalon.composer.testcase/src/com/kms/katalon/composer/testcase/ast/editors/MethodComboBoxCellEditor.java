package com.kms.katalon.composer.testcase.ast.editors;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;

public class MethodComboBoxCellEditor extends ComboBoxCellEditor {
    private List<Method> methods = new ArrayList<Method>();

    private boolean staticOnly;

    private ClassLoader classLoader;

    public MethodComboBoxCellEditor(Composite parent, Class<?> type, boolean staticOnly, ClassLoader classLoader) {
        super(parent, new String[0]);
        this.staticOnly = staticOnly;
        this.classLoader = classLoader;
        if (type != null) {
            pupulateMethodListWithTypeMethods(type);
        } else {
            populateMethodListWithGroovyDefaultMethods();
        }
        Collections.sort(methods, new Comparator<Method>() {
            @Override
            public int compare(Method method_1, Method method_2) {
                if (method_1 == null || method_2 == null) {
                    return 0;
                }
                int methodNameComparison = method_1.getName().compareTo(method_2.getName());
                if (methodNameComparison != 0) {
                    return methodNameComparison;
                }
                return method_1.getParameterTypes().length - method_2.getParameterTypes().length;
            }
        });
        String[] methodSignatures = new String[methods.size()];
        for (int index = 0; index < methods.size(); index++) {
            methodSignatures[index] = getMethodSignature(methods.get(index));
        }
        setItems(methodSignatures);
    }

    private void pupulateMethodListWithTypeMethods(Class<?> type) {
        for (Method method : type.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && (!staticOnly || Modifier.isStatic(method.getModifiers()))) {
                methods.add(method);
            }
        }
    }

    private void populateMethodListWithGroovyDefaultMethods() {
        pupulateMethodListWithTypeMethods(DefaultGroovyMethods.class);
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
        int bestMatchIndex = 0;
        for (int index = 0; index < methods.size(); index++) {
            switch (compareMethodAndMethodCall(methods.get(index), methodCall, classLoader)) {
                case EQUAL_NAME_ONLY:
                    bestMatchIndex = index;
                    break;
                case EQUAL_NAME_AND_PARAM:
                    super.doSetValue(index);
                    return;
                default:
                    break;
            }
        }
        super.doSetValue(bestMatchIndex);
    }

    public static MethodComparation compareMethodAndMethodCall(Method methodNode,
            MethodCallExpressionWrapper methodCall, ClassLoader classLoader) {
        if (!methodCall.getMethodAsString().equals(methodNode.getName())) {
            return MethodComparation.NOT_EQUAL_NAME;
        }
        if (compareArguments(methodNode.getParameterTypes(), getMethodCallParams(methodCall, classLoader), classLoader)) {
            return MethodComparation.EQUAL_NAME_AND_PARAM;
        }
        return MethodComparation.EQUAL_NAME_ONLY;
    }

    /**
     * @return the selected {@link Method}
     */
    @Override
    protected Method doGetValue() {
        Integer selectionIndex = (Integer) super.doGetValue();
        if (selectionIndex < 0 || selectionIndex >= methods.size()) {
            return null;
        }
        return methods.get(selectionIndex);
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

    public static Class<?>[] getMethodCallParams(MethodCallExpressionWrapper methodCall, ClassLoader classLoader) {
        ArgumentListExpressionWrapper argumentList = methodCall.getArguments();
        Class<?>[] methodCallParam = new Class<?>[argumentList.getExpressions().size()];
        for (int index = 0; index < argumentList.getExpressions().size(); index++) {
            methodCallParam[index] = argumentList.getExpression(index).resolveType(classLoader);
        }
        return methodCallParam;
    }

    private static boolean compareArguments(Class<?>[] methodClassParams, Class<?>[] methodCallParams,
            ClassLoader classLoader) {
        if (methodClassParams.length != methodCallParams.length) {
            return false;
        }
        for (int i = 0; i < methodClassParams.length; i++) {
            Class<?> methodCallParam = changeToPrimitiveTypeIfPossible(methodCallParams[i], classLoader);
            Class<?> methodClassParam = changeToPrimitiveTypeIfPossible(methodClassParams[i], classLoader);
            if (!ClassUtils.isAssignable(methodClassParam, methodCallParam, true)) {
                return false;
            }
        }
        return true;
    }

    private static Class<?> changeToPrimitiveTypeIfPossible(Class<?> type, ClassLoader classLoader) {
        if (type == null) {
            return null;
        }
        try {
            Class<?> wrapper = ClassUtils.primitiveToWrapper(type);
            Class<?> numberClass = getNumberClass(classLoader);
            if (numberClass.isAssignableFrom(wrapper)) {
                wrapper = numberClass;
            }
            return classLoader.loadClass(wrapper.getName());
        } catch (ClassNotFoundException e) {
            return type;
        }
    }

    private static Class<?> getNumberClass(ClassLoader classLoader) {
        try {
            return classLoader.loadClass(Number.class.getName());
        } catch (ClassNotFoundException e) {
            return Number.class;
        }
    }

    public static List<Class<?>> getParamClasses(Method method) {
        if (method == null) {
            return Collections.emptyList();
        }
        List<Class<?>> parameterClasses = new ArrayList<Class<?>>();
        for (Type type : method.getGenericParameterTypes()) {
            if (type instanceof Class<?>) {
                parameterClasses.add(((Class<?>) type));
                continue;
            }
            if (isMapType(type)) {
                parameterClasses.add(Map.class);
            }
        }
        return parameterClasses;
    }

    private static boolean isMapType(Type type) {
        return type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() instanceof Class<?>
                && ((Class<?>) ((ParameterizedType) type).getRawType()).getName().equals(Map.class.getName());
    }

    public enum MethodComparation {
        NOT_EQUAL_NAME, EQUAL_NAME_ONLY, EQUAL_NAME_AND_PARAM
    }
}
