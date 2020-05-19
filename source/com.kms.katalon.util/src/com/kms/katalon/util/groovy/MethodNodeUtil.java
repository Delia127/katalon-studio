package com.kms.katalon.util.groovy;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.eclipse.jdt.core.Signature;

import com.kms.katalon.util.TypeUtil;

public class MethodNodeUtil {

    public static String getDescriptor(MethodNode methodNode) {
        StringBuilder builder = new StringBuilder();
        builder.append(methodNode.getDeclaringClass().getName());
        builder.append(".");
        builder.append(methodNode.getName());
        builder.append("(");

        if (methodNode.getParameters().length > 0) {
            for (Parameter p : methodNode.getParameters()) {
                ClassNode type = p.getType();
                if (ClassHelper.isPrimitiveType(type)) {
                    type = ClassHelper.getWrapper(p.getType());
                }
                String typeName = type.getName();
                builder.append(typeName);
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append(")");

        return builder.toString();
    }

    public static boolean matchMethod(MethodNode methodToMatch, String className, String methodName,
            String[] parameterTypes) {
        String[] parameterTypes1 = getParameterTypes(methodToMatch);
        String className1 = methodToMatch.getDeclaringClass().getName();
        String methodName1 = methodToMatch.getName();
        return className1.equals(className) && methodName1.equals(methodName)
                && TypeUtil.areSameTypes(parameterTypes1, parameterTypes);
    }
    
    public static boolean matchMethodWithLooseParamTypesChecking(MethodNode methodToMatch, String className, String methodName,
            String[] parameterTypes) {
        String[] parameterTypes1 = getParameterTypes(methodToMatch);
        String className1 = methodToMatch.getDeclaringClass().getName();
        String methodName1 = methodToMatch.getName();
        return className1.equals(className) && methodName1.equals(methodName)
                && TypeUtil.areSameTypesWithLooseTypeChecking(parameterTypes1, parameterTypes);
    }

    public static boolean matchMethod(MethodNode methodToMatch, String className, String methodName, int numOfParams) {
        String className1 = methodToMatch.getDeclaringClass().getName();
        String methodName1 = methodToMatch.getName();
        int numOfParams1 = methodToMatch.getParameters().length;
        return className1.equals(className) && methodName1.equals(methodName) && numOfParams1 == numOfParams;
    }

    public static String[] getParameterTypes(MethodNode method) {
        return Stream.of(method.getParameters())
                .map(p -> p.getType().getName())
                .map(t -> {
                    try {
                        return Signature.toString(t);
                    } catch (Exception e) {
                        return t;
                    }
                }).collect(Collectors.toList())
                .toArray(new String[method.getParameters().length]);
    }
}
