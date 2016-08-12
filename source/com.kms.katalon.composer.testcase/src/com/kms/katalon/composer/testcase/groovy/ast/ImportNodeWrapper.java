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

    public ImportNodeWrapper(Class<?> type, String fieldName, ASTNodeWrapper parentNodeWrapper, boolean isStatic) {
        this(type, parentNodeWrapper);
        this.fieldName = fieldName;
        this.isStatic = isStatic;
    }

    public ImportNodeWrapper(Class<?> type, ASTNodeWrapper parentNodeWrapper) {
        this(type, parentNodeWrapper, null);
    }

    public ImportNodeWrapper(Class<?> type, ASTNodeWrapper parentNodeWrapper, String alias) {
        super(parentNodeWrapper);
        this.type = new ClassNodeWrapper(type, this);
        this.alias = alias;
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
        if (importNode.getType() != null) {
            this.type = new ClassNodeWrapper(importNode.getType(), this);
        }
        this.alias = importNode.getAlias();
        this.fieldName = importNode.getFieldName();
        this.packageName = importNode.getPackageName();
        this.isStar = importNode.isStar();
        this.isStatic = importNode.isStatic();
    }
    
    public ImportNodeWrapper(String className, String classNameWithoutPackage, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.type = new ClassNodeWrapper(className, classNameWithoutPackage, this);
        this.isStar = false;
        this.isStatic = false;
        this.packageName = null;
        this.fieldName = null;
        this.alias = classNameWithoutPackage;
    }

    public ClassNodeWrapper getType() {
        return type;
    }

    public String getAlias() {
        return alias;
    }
    
    public String getKnownAlias() {
        if (alias != null) {
            return alias;
        }
        if (fieldName != null) {
            return fieldName;
        }
        
        return getType().getNameWithoutPackage();
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = prime * result + (isStar ? 1231 : 1237);
        result = prime * result + (isStatic ? 1231 : 1237);
        result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ImportNodeWrapper other = (ImportNodeWrapper) obj;
        return getText().equals(other.getText());
    }
}
