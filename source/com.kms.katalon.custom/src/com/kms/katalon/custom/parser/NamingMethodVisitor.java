package com.kms.katalon.custom.parser;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import static java.lang.reflect.Modifier.isStatic;
import groovyjarjarasm.asm.Type;
import static java.util.stream.Collectors.toList;

public class NamingMethodVisitor extends ClassVisitor {
    
    private Map<String, ParameterNameMethodVisitor> methodVisitorMap;

    private Class clazz;

    NamingMethodVisitor(Class clazz) {
        super(Opcodes.ASM5);
        this.clazz = clazz;
        this.methodVisitorMap = new HashMap<>();
        for (Method method : clazz.getMethods()) {
            List<Type> parameterTypes = Arrays.stream(method.getParameterTypes())
                .map(Type::getType)
                .collect(toList());
            
            String typesClassName = MethodUtils.getParamtersDescriptor(parameterTypes);
            String methodName = method.getName() + "#" + typesClassName;
            ParameterNameMethodVisitor methodVisitor = new ParameterNameMethodVisitor(isStatic(method.getModifiers()), parameterTypes);
            this.methodVisitorMap.put(methodName, methodVisitor);
        }
    }

    List<String> getParameterNames(String methodNameAndParams) {
        ParameterNameMethodVisitor methodVisitor = this.methodVisitorMap.get(methodNameAndParams);
        if (methodVisitor != null) {
            return methodVisitor.getParamterNames();
        }
        return new ArrayList<>();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        String typesClassName = Arrays.stream(Type.getArgumentTypes(desc))
        .map(Type::getClassName)
        .collect(Collectors.joining(","));
        
        ParameterNameMethodVisitor methodVisitor = this.methodVisitorMap.get(name + "#" + typesClassName);
        return methodVisitor;
    }
}

class ParameterNameMethodVisitor extends MethodVisitor {
    private final boolean isStatic;

    private final List<Type> parameterTypes;

    private final String[] slotNames;

    ParameterNameMethodVisitor(boolean isStatic, List<Type> parameterTypes) {
        super(Opcodes.ASM5);
        this.isStatic = isStatic;
        this.parameterTypes = parameterTypes;

        int parameterSlots = 0;
        if (!isStatic) {
            parameterSlots++;
        }
        for (Type parameterType : parameterTypes) {
            parameterSlots += parameterType.getSize();
        }
        slotNames = new String[parameterSlots];
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if (index < slotNames.length) {
            slotNames[index] = name;
        }
    }
    List<String> getParamterNames() {
        int slot = 0;
        if (!isStatic) {
            slot++;
        }

        List<String> result = new ArrayList<>();
        for (Type parameterType : parameterTypes) {
            String slotName = slotNames[slot];
            if (slotName == null) {
                // symbols not present or only partially defined
                return Collections.unmodifiableList(result);
            }
            result.add(slotName);

            slot += parameterType.getSize();
        }

        return Collections.unmodifiableList(result);
    }
}
