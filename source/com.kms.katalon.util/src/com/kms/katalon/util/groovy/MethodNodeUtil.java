package com.kms.katalon.util.groovy;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.eclipse.jdt.core.Signature;

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

    public static String[] getParameterTypes(MethodNode method) {
        return Arrays.asList(method.getParameters()).stream()
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
