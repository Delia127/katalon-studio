package com.kms.katalon.custom.keyword;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.eclipse.persistence.internal.libraries.asm.ClassReader;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.libraries.asm.tree.ClassNode;
import org.eclipse.persistence.internal.libraries.asm.tree.LocalVariableNode;
import org.eclipse.persistence.internal.libraries.asm.tree.MethodNode;

import com.kms.katalon.core.annotation.Keyword;

public class KeywordMethod {
    private final static String CLASS_FILE_EXTENSION = ".class";
    private String name;
    private KeywordParameter[] parameters;
    private Class<?> returnType = Void.TYPE;
    private Keyword keywordAnnotation;
    
    public KeywordMethod(Method method) {
        this.name = method.getName();
        this.keywordAnnotation = method.getAnnotation(Keyword.class);
        this.returnType = method.getReturnType();
        getParameterNameAndType(method);
        
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void getParameterNameAndType(Method method) {
        Class clazz = method.getDeclaringClass();
        InputStream is = clazz.getClassLoader().getResourceAsStream(
                Type.getType(clazz).getInternalName() + CLASS_FILE_EXTENSION);
        ClassNode classNode = new ClassNode();
        ClassReader classReader = null;
        try {
            classReader = new ClassReader(is);
        } catch (IOException e) {
            // IO Error, return
        }
        if (classReader == null) {
            return;
        }
        classReader.accept(classNode, 0);

        for (MethodNode node : (List<MethodNode>) classNode.methods) {
            if (!node.name.equals(method.getName())) {
                continue;
            }
            if (!Type.getMethodDescriptor(method).equals(node.desc)) {
                continue;
            }
            parameters = new KeywordParameter[method.getParameterTypes().length];

            List<LocalVariableNode> localVariables = node.localVariables;
            Collections.sort(localVariables, new Comparator<LocalVariableNode>() {
                @Override
                public int compare(LocalVariableNode arg0, LocalVariableNode arg1) {
                    if (arg0 != null && arg1 != null) {
                        return arg0.index - arg1.index;
                    }
                    return 0;
                }

            });
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                LocalVariableNode localVariableNode = localVariables.get(i);
                parameters[i] = new KeywordParameter(localVariableNode.name,
                        method.getParameterTypes()[i].isPrimitive()
                                ? ClassUtils.primitiveToWrapper(method.getParameterTypes()[i])
                                : method.getParameterTypes()[i]);
            }
        }
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KeywordParameter[] getParameters() {
        return parameters;
    }

    public void setParameters(KeywordParameter[] paramters) {
        this.parameters = paramters;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Keyword getKeywordAnnotation() {
        return keywordAnnotation;
    }

    public void setKeywordAnnotation(Keyword keywordAnnotation) {
        this.keywordAnnotation = keywordAnnotation;
    }
    
    public boolean checkParametersAssignable(String[] paramTypes) {
        for (int i = 0; i < this.getParameters().length; i++) {
            if (!Object.class.getName().equals(paramTypes[i])
                    && !this.getParameters()[i].isClassAssignable(paramTypes[i])) {
                return false;
            }
        }
        return true;
    }
}
