package com.kms.katalon.composer.testcase.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;

public class InputParameterClass {
    private String fullName;

    private String simpleName;

    private boolean isArray;

    private boolean isEnum;

    private Object[] enumConstants;

    private InputParameterClass componentType;

    private List<InputParameterClass> actualTypeArguments;

    private int modifiers;

    public InputParameterClass(String fullName, String simpleName) {
        this.fullName = fullName;
        this.simpleName = simpleName;
    }

    public InputParameterClass(Type type) {
        Class<?> clazz = null;
        if (type instanceof Class<?>) {
            clazz = (Class<?>) type;
        } else if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() instanceof Class<?>) {
            clazz = (Class<?>) ((ParameterizedType) type).getRawType();
        }
        setFullName(clazz.getName());
        setSimpleName(clazz.getSimpleName());
        setModifiers(clazz.getModifiers());
        if (clazz.isArray() && clazz.getComponentType() != null) {
            setArray(true);
            Class<?> componentType = clazz.getComponentType();
            setComponentType(new InputParameterClass(componentType.getName(),
                    componentType.getSimpleName()));
        }
        if (clazz.isEnum()) {
            setEnum(true);
            setEnumConstants(clazz.getEnumConstants());
        }
        List<InputParameterClass> typeList = new ArrayList<InputParameterClass>();
        if (type instanceof ParameterizedType) {
            for (Type actualTypeArgument : ((ParameterizedType) type).getActualTypeArguments()) {
                if (actualTypeArgument instanceof Class<?>) {
                    typeList.add(new InputParameterClass(actualTypeArgument));
                }
            }
        } else {
            typeList.add(new InputParameterClass(Object.class.getName(), Object.class.getSimpleName()));
        }
        setActualTypeArguments(typeList);
    }

    public InputParameterClass(ClassNodeWrapper classNode) {
        this(classNode.getName(), classNode.getNameWithoutPackage());
        setModifiers(classNode.getModifiers());
        if (classNode.isArray()) {
            setArray(true);
            setComponentType(new InputParameterClass(classNode.getComponentType().getName(),
                    classNode.getComponentType().getNameWithoutPackage()));
        }
        if (classNode.isEnum()) {
            setEnum(true);
            setEnumConstants(classNode.getTypeClass().getEnumConstants());
        }
        List<InputParameterClass> typeList = new ArrayList<InputParameterClass>();
        typeList.add(new InputParameterClass(Object.class.getName(), Object.class.getSimpleName()));
        typeList.add(new InputParameterClass(Object.class.getName(), Object.class.getSimpleName()));
        setActualTypeArguments(typeList);
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean isArray) {
        this.isArray = isArray;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public void setEnum(boolean isEnum) {
        this.isEnum = isEnum;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Object[] getEnumConstants() {
        return enumConstants;
    }

    public void setEnumConstants(Object[] enumConstants) {
        this.enumConstants = enumConstants;
    }

    public InputParameterClass getComponentType() {
        return componentType;
    }

    public void setComponentType(InputParameterClass componentType) {
        this.componentType = componentType;
    }

    public List<InputParameterClass> getActualTypeArguments() {
        return actualTypeArguments;
    }

    public void setActualTypeArguments(List<InputParameterClass> actualTypeArguments) {
        this.actualTypeArguments = actualTypeArguments;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
    
    public String getDisplayText() {
        if (!isArray()) {
            return getSimpleName();
        }
        if (getComponentType() != null) {
            return getComponentType().getSimpleName() + "[]";
        }
        return Object.class.getSimpleName() + "[]";
    }
}
