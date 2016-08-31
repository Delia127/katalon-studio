package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.expr.Expression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.AnnonatedNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;

// Base class for all expression
public abstract class ExpressionWrapper extends AnnonatedNodeWrapper {
    protected ClassNodeWrapper type = ClassNodeWrapper.getClassWrapper(ClassHelper.DYNAMIC_TYPE, this);

    public ExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public ExpressionWrapper(ExpressionWrapper expressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(expressionWrapper, parentNodeWrapper);
        this.type = new ClassNodeWrapper(expressionWrapper.getType(), this);
    }

    public ExpressionWrapper(Expression expression, ASTNodeWrapper parentNodeWrapper) {
        super(expression, parentNodeWrapper);
        this.type = ClassNodeWrapper.getClassWrapper(expression.getType(), this);
    }

    @Override
    public boolean hasAstChildren() {
        return false;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        return new ArrayList<ASTNodeWrapper>();
    }

    public ClassNodeWrapper getType() {
        return type;
    }

    public void setType(ClassNodeWrapper type) {
        if (type == null) {
            return;
        }
        type.setParent(this);
        this.type = type;
    }

    public void setType(Class<?> typeClass) {
        ScriptNodeWrapper scriptClass = getScriptClass();
        if (scriptClass != null) {
            getScriptClass().addImport(typeClass);
        }
        type.setType(typeClass);
    }

    @Override
    public abstract ExpressionWrapper clone();

    @Override
    public ExpressionWrapper copy(ASTNodeWrapper newParent) {
        return (ExpressionWrapper) super.copy(newParent);
    }

    @Override
    public String getInputText() {
        return getText();
    }

    public Class<?> resolveType(ClassLoader classLoader) {
        Class<?> foundClass = loadClassQuietly(getType().getName(), classLoader);
        if (foundClass != null) {
            return foundClass;
        }
        return null;
    }

    protected final Class<?> loadClassQuietly(String className, ClassLoader classLoader) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            return AstKeywordsInputUtil.loadClassFromImportedPackage(className, classLoader);
        }
    }
}
