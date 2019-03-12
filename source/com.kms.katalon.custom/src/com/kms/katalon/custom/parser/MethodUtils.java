package com.kms.katalon.custom.parser;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;

import groovyjarjarasm.asm.Type;

public class MethodUtils {

    public static String getParametersDescriptor(MethodNode methodNode) {
        String typesClassName = Arrays.stream(methodNode.getParameters())
                .map(Parameter::getType)
                .map(ClassNode::getName)
                .collect(Collectors.joining(","));
        return typesClassName;
    }
    
    public static String getParametersDescriptor(List<Type> types) {
        String typesClassName = types.stream()
                .map(Type::getClassName)
                .collect(Collectors.joining(","));
        return typesClassName;
    }
}
