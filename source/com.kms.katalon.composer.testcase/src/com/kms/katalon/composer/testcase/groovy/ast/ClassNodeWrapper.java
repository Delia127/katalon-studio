package com.kms.katalon.composer.testcase.groovy.ast;

import groovyjarjarasm.asm.Opcodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;

import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;

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
            this.typeClass = AstKeywordsInputUtil.loadType(classNode.getName(), null);
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
        if (classNode.getModule() != null) {
            copyImports(classNode);
        }
        copyMethods(classNode);
        copyFields(classNode);
    }

    private void copyFields(ClassNode classNode) {
        fields.clear();
        for (FieldNode fieldNode : classNode.getFields()) {
            if (fieldNode.getLineNumber() < 0) {
                continue;
            }
            fields.add(new FieldNodeWrapper(fieldNode, this));
        }
    }

    private void copyMethods(ClassNode classNode) {
        methods.clear();
        for (MethodNode method : classNode.getMethods()) {
            if (method.getLineNumber() < 0 || ("run".equals(method.getName()))) {
                continue;
            }
            methods.add(new MethodNodeWrapper(method, this));
        }
    }

    private void copyImports(ClassNode classNode) {
        imports.clear();
        ModuleNode module = classNode.getModule();
        addImportNodeWrappers(module.getImports());
        addImportNodeWrappers(module.getStaticImports().values());
        addImportNodeWrappers(module.getStarImports());
        addImportNodeWrappers(module.getStaticStarImports().values());
        // sort import base on start line
        Collections.sort(imports, new Comparator<ImportNodeWrapper>() {
            @Override
            public int compare(ImportNodeWrapper import_1, ImportNodeWrapper import_2) {
                return Integer.compare(import_1.getLineNumber(), import_2.getLineNumber());
            }

        });
    }

    private void addImportNodeWrappers(Collection<ImportNode> importsList) {
        for (ImportNode importNode : importsList) {
            imports.add(new ImportNodeWrapper(importNode, this));
        }
    }

    public ClassNodeWrapper(ClassNodeWrapper classNodeWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(classNodeWrapper, parentNodeWrapper);
        copyClassProperties(classNodeWrapper);
    }

    private void copyClassProperties(ClassNodeWrapper classNodeWrapper) {
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

    public boolean isSuper() {
        return isSuper;
    }

    public boolean isThis() {
        return isThis;
    }

    public GenericsTypeWrapper[] getGenericsTypes() {
        return genericsTypes;
    }

    public void setType(Class<?> newType) {
        copyProperties(new ClassNode(newType));
    }

    public List<MethodNodeWrapper> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    public List<String> getMethodNames() {
        List<String> methodNames = new ArrayList<String>();
        List<MethodNodeWrapper> methods = getMethods();
        for (MethodNodeWrapper method : methods) {
            methodNames.add(method.getName());
        }
        return methodNames;
    }

    public void addMethod(MethodNodeWrapper method) {
        if (method == null) {
            return;
        }
        method.setParent(this);
        methods.add(method);
    }

    public boolean addMethod(MethodNodeWrapper method, int index) {
        if (method == null || index < 0 || index > methods.size()) {
            return false;
        }
        method.setParent(this);
        methods.add(index, method);
        return true;
    }

    public boolean removeMethod(MethodNodeWrapper method) {
        return methods.remove(method);
    }

    public boolean removeMethod(int index) {
        if (index < 0 || index >= methods.size()) {
            return false;
        }
        methods.remove(index);
        return true;
    }

    public int indexOfMethod(MethodNodeWrapper methodNode) {
        if (methodNode == null) {
            return -1;
        }
        return methods.indexOf(methodNode);
    }

    public boolean setMethod(MethodNodeWrapper method, int index) {
        if (method == null || index < 0 || index > methods.size()) {
            return false;
        }
        method.setParent(this);
        methods.set(index, method);
        return true;
    }

    public List<FieldNodeWrapper> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public void clearFields() {
        fields.clear();
    }

    public void addField(FieldNodeWrapper field) {
        if (field == null) {
            return;
        }
        field.setParent(this);
        fields.add(field);
    }

    public boolean addField(FieldNodeWrapper field, int index) {
        if (field == null || index < 0 || index > fields.size()) {
            return false;
        }
        field.setParent(this);
        fields.add(index, field);
        return true;
    }

    public boolean removeField(FieldNodeWrapper field) {
        return fields.remove(field);
    }

    public boolean removeField(int index) {
        if (index < 0 || index >= fields.size()) {
            return false;
        }
        fields.remove(index);
        return true;
    }

    public int indexOfField(FieldNodeWrapper field) {
        if (field == null) {
            return -1;
        }
        return fields.indexOf(field);
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public int getModifiers() {
        return modifiers;
    }

    public ClassNodeWrapper getComponentType() {
        return componentType;
    }

    public List<ImportNodeWrapper> getImports() {
        return Collections.unmodifiableList(imports);
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

    @Override
    public boolean isChildAssignble(ASTNodeWrapper astNode) {
        return (astNode instanceof MethodNodeWrapper || astNode instanceof FieldNodeWrapper);
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject) {
        if (childObject instanceof MethodNodeWrapper) {
            addMethod((MethodNodeWrapper) childObject);
            return true;
        } else if (childObject instanceof FieldNodeWrapper) {
            addField((FieldNodeWrapper) childObject);
            return true;
        }
        return false;
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject, int index) {
        if (childObject instanceof MethodNodeWrapper) {
            return addMethod((MethodNodeWrapper) childObject, index);
        } else if (childObject instanceof FieldNodeWrapper) {
            return addField((FieldNodeWrapper) childObject, index);
        }
        return false;
    }

    @Override
    public boolean removeChild(ASTNodeWrapper childObject) {
        if (childObject instanceof MethodNodeWrapper) {
            return removeMethod((MethodNodeWrapper) childObject);
        } else if (childObject instanceof FieldNodeWrapper) {
            return removeField((FieldNodeWrapper) childObject);
        }
        return false;
    }

    @Override
    public int indexOf(ASTNodeWrapper childObject) {
        if (childObject instanceof MethodNodeWrapper) {
            return indexOfMethod((MethodNodeWrapper) childObject);
        } else if (childObject instanceof FieldNodeWrapper) {
            return indexOfField((FieldNodeWrapper) childObject);
        }
        return -1;
    }

    @Override
    public ClassNodeWrapper clone() {
        return new ClassNodeWrapper(this, getParent());
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (!(input instanceof ClassNodeWrapper) || this.isEqualsTo(input)) {
            return false;
        }
        copyClassProperties((ClassNodeWrapper) input);
        return true;
    }
}
