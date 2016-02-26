package com.kms.katalon.composer.testcase.groovy.ast;

import groovyjarjarasm.asm.Opcodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;

import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;

public class ClassNodeWrapper extends ASTNodeWrapper {
    protected Class<?> typeClass;
    protected GenericsTypeWrapper[] genericsTypes;
    protected String name;
    protected String nameWithoutPackage;
    protected boolean isThis;
    protected boolean isSuper;
    protected int modifiers;
    protected ClassNodeWrapper componentType = null;
    protected List<ImportNodeWrapper> imports = new ArrayList<ImportNodeWrapper>();
    protected List<MethodNodeWrapper> methods = new ArrayList<MethodNodeWrapper>();
    protected List<FieldNodeWrapper> fields = new ArrayList<FieldNodeWrapper>();

    public ClassNodeWrapper(Class<?> clazz, ASTNodeWrapper parentNodeWrapper) {
        this(new ClassNode(clazz), parentNodeWrapper);
    }

    public ClassNodeWrapper(ClassNode classNode, ASTNodeWrapper parentNodeWrapper) {
        super(classNode, parentNodeWrapper);
        copyProperties(classNode);
    }

    protected void copyProperties(ClassNode classNode) {
        this.name = classNode.getName();
        this.nameWithoutPackage = classNode.getNameWithoutPackage();
        this.isSuper = classNode == ClassNode.SUPER;
        this.isThis = classNode == ClassNode.THIS;
        if (!classNode.isScript()) {
            this.typeClass = AstTreeTableInputUtil.loadType(classNode.getName(), null);
        }
        this.modifiers = classNode.getModifiers();
        if (classNode.getComponentType() != null) {
            this.componentType = new ClassNodeWrapper(classNode.getComponentType(), this);
        }
        if (classNode.getGenericsTypes() != null) {
            genericsTypes = new GenericsTypeWrapper[classNode.getGenericsTypes().length];
            for (int index = 0; index < classNode.getGenericsTypes().length; index++) {
                genericsTypes[index] = new GenericsTypeWrapper(classNode.getGenericsTypes()[index], this);
            }
        }
        imports.clear();
        if (classNode.getModule() != null) {
            for (ImportNode importNode : classNode.getModule().getImports()) {
                imports.add(new ImportNodeWrapper(importNode, this));
            }
        }
        // sort import base on start line
        Collections.sort(imports, new Comparator<ImportNodeWrapper>() {
            @Override
            public int compare(ImportNodeWrapper import_1, ImportNodeWrapper import_2) {
                return Integer.compare(import_1.getLineNumber(), import_2.getLineNumber());
            }

        });
        methods.clear();
        for (MethodNode method : classNode.getMethods()) {
            if (method.getLineNumber() < 0 || method.getName().equals("run")) {
                continue;
            }
            methods.add(new MethodNodeWrapper(method, this));
        }
        fields.clear();
        for (FieldNode fieldNode : classNode.getFields()) {
            if (fieldNode.getLineNumber() < 0) {
                continue;
            }
            fields.add(new FieldNodeWrapper(fieldNode, this));
        }
    }

    public ClassNodeWrapper(ClassNodeWrapper classNodeWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(classNodeWrapper, parentNodeWrapper);
        this.name = classNodeWrapper.getName();
        this.nameWithoutPackage = classNodeWrapper.getNameWithoutPackage();
        this.isSuper = classNodeWrapper.isSuper();
        this.isThis = classNodeWrapper.isThis();
        this.typeClass = classNodeWrapper.getTypeClass();
        this.modifiers = classNodeWrapper.getModifiers();
        if (classNodeWrapper.getComponentType() != null) {
            this.componentType = new ClassNodeWrapper(classNodeWrapper.getComponentType(), this);
        }
        if (classNodeWrapper.getGenericsTypes() != null) {
            genericsTypes = new GenericsTypeWrapper[classNodeWrapper.getGenericsTypes().length];
            for (int index = 0; index < classNodeWrapper.getGenericsTypes().length; index++) {
                genericsTypes[index] = new GenericsTypeWrapper(classNodeWrapper.getGenericsTypes()[index], this);
            }
        }
        imports.clear();
        for (ImportNodeWrapper importNode : classNodeWrapper.getImports()) {
            imports.add(new ImportNodeWrapper(importNode, this));
        }
        methods.clear();
        for (MethodNodeWrapper method : classNodeWrapper.getMethods()) {
            methods.add(new MethodNodeWrapper(method, this));
        }
    }

    @Override
    public String getText() {
        return getNameWithoutPackage();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(imports);
        astNodeWrappers.addAll(fields);
        astNodeWrappers.addAll(methods);
        return astNodeWrappers;
    }

    public String getName() {
        return name;
    }

    public String getNameWithoutPackage() {
        return nameWithoutPackage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameWithoutPackage(String nameWithoutPackage) {
        this.nameWithoutPackage = nameWithoutPackage;
    }

    public boolean isSuper() {
        return isSuper;
    }

    public boolean isThis() {
        return isThis;
    }

    public GenericsTypeWrapper[] getGenericsTypes() {
        return genericsTypes;
    }

    public void setGenericsTypes(GenericsTypeWrapper[] genericsTypes) {
        this.genericsTypes = genericsTypes;
    }

    public void setType(Class<?> newType) {
        copyProperties(new ClassNode(newType));
    }

    public List<MethodNodeWrapper> getMethods() {
        return methods;
    }

    public List<String> getMethodNames() {
        List<String> methodNames = new ArrayList<String>();
        List<MethodNodeWrapper> methods = getMethods();
        for (MethodNodeWrapper method : methods) {
            methodNames.add(method.getName());
        }
        return methodNames;
    }

    public void setMethods(List<MethodNodeWrapper> methods) {
        this.methods = methods;
    }

    public List<FieldNodeWrapper> getFields() {
        return fields;
    }

    public void setFields(List<FieldNodeWrapper> fields) {
        this.fields = fields;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public ClassNodeWrapper getComponentType() {
        return componentType;
    }

    public void setComponentType(ClassNodeWrapper componentType) {
        this.componentType = componentType;
    }

    public List<ImportNodeWrapper> getImports() {
        return imports;
    }

    public void setImports(List<ImportNodeWrapper> imports) {
        this.imports = imports;
    }

    public void addImport(Class<?> classNeedImport) {
        boolean isContained = false;
        for (ImportNodeWrapper importNode : imports) {
            if (importNode.getType().getName().equals(classNeedImport.getName())) {
                isContained = true;
                break;
            }
        }
        if (!isContained) {
            imports.add(new ImportNodeWrapper(classNeedImport, this));
        }
    }

    public boolean isArray() {
        return componentType != null;
    }

    public boolean isEnum() {
        return (getModifiers() & Opcodes.ACC_ENUM) != 0;
    }

}
