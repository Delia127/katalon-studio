package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.ImportNode;

public class ImportNodeWrapper extends AnnonatedNodeWrapper {
    private ClassNodeWrapper type;
    private String alias;
    private String fieldName;
    private String packageName;
    private boolean isStar;
    private boolean isStatic;

    public ImportNodeWrapper(Class<?> type, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.type = new ClassNodeWrapper(type, this);
        this.alias = null;
        this.isStar = false;
        this.isStatic = false;
        this.packageName = null;
        this.fieldName = null;
    }

    public ImportNodeWrapper(ImportNodeWrapper importNodeWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(importNodeWrapper, parentNodeWrapper);
        this.type = new ClassNodeWrapper(importNodeWrapper.getType(), this);
        this.alias = importNodeWrapper.getAlias();
        this.fieldName = importNodeWrapper.getFieldName();
        this.packageName = importNodeWrapper.getPackageName();
        this.isStar = importNodeWrapper.isStar();
        this.isStatic = importNodeWrapper.isStatic();
    }
    
    public ImportNodeWrapper(ImportNode importNode, ASTNodeWrapper parentNodeWrapper) {
        super(importNode, parentNodeWrapper);
        this.type = new ClassNodeWrapper(importNode.getType(), this);
        this.alias = importNode.getAlias();
        this.fieldName = importNode.getFieldName();
        this.packageName = importNode.getPackageName();
        this.isStar = importNode.isStar();
        this.isStatic = importNode.isStatic();
    }

    public ClassNodeWrapper getType() {
        return type;
    }

    public String getAlias() {
        return alias;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isStar() {
        return isStar;
    }

    public boolean isStatic() {
        return isStatic;
    }
    
    public String getClassName() {
        return type == null ? null : type.getName();
    }

    @Override
    public String getText() {
        String typeName = (type == null ? null : type.getName());
        if (isStar && !isStatic) {
            return "import " + packageName + "*";
        }
        if (isStar) {
            return "import static " + typeName + ".*";
        }
        if (isStatic) {
            if (alias != null && alias.length() != 0 && !alias.equals(fieldName)) {
                return "import static " + typeName + "." + fieldName + " as " + alias;
            }
            return "import static " + typeName + "." + fieldName;
        }
        if (alias == null || alias.length() == 0) {
            return "import " + typeName;
        }
        return "import " + typeName + " as " + alias;
    }

    @Override
    public boolean hasAstChildren() {
        return false;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        return Collections.emptyList();
    }

    @Override
    public ImportNodeWrapper clone() {
        return new ImportNodeWrapper(this, getParent());
    }
}
