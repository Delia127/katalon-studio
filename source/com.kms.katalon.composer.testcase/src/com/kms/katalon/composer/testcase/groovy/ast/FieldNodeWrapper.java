package com.kms.katalon.composer.testcase.groovy.ast;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.FieldNode;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

public class FieldNodeWrapper extends AnnonatedNodeWrapper {
    private String name;

    private int modifiers;

    private ClassNodeWrapper type;

    private ExpressionWrapper initialValueExpression;

    public FieldNodeWrapper(String name, Class<?> type, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.name = name;
        this.type = new ClassNodeWrapper(type, this);
        this.modifiers = Modifier.PUBLIC;
    }

    public FieldNodeWrapper(FieldNode fieldNode, ASTNodeWrapper parentNodeWrapper) {
        super(fieldNode, parentNodeWrapper);
        this.name = fieldNode.getName();
        this.modifiers = fieldNode.getModifiers();
        this.type = new ClassNodeWrapper(fieldNode.getType(), this);
        this.initialValueExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                fieldNode.getInitialExpression(), this);
    }

    public FieldNodeWrapper(FieldNodeWrapper fieldNodeWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(fieldNodeWrapper, parentNodeWrapper);
        this.name = fieldNodeWrapper.getName();
        this.modifiers = fieldNodeWrapper.getModifiers();
        this.type = new ClassNodeWrapper(fieldNodeWrapper.getType(), this);
        if (initialValueExpression != null) {
            initialValueExpression = fieldNodeWrapper.getInitialValueExpression().copy(this);
        }
    }

    public FieldNodeWrapper(FieldNodeWrapper fieldNodeWrapper) {
        this(fieldNodeWrapper, fieldNodeWrapper.getParent());
    }

    public String getName() {
        return name;
    }

    public int getModifiers() {
        return modifiers;
    }

    public ExpressionWrapper getInitialValueExpression() {
        return initialValueExpression;
    }
    
    public void setInitialValueExpression(ExpressionWrapper initialValueExpression) {
        if (initialValueExpression == null) {
            return;
        }
        initialValueExpression.setParent(this);
        this.initialValueExpression = initialValueExpression;
    }

    public ClassNodeWrapper getType() {
        return type;
    }

    @Override
    public String getText() {
        return name;
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(super.getAstChildren());
        astNodeWrappers.add(initialValueExpression);
        return astNodeWrappers;
    }
    
    @Override
    public FieldNodeWrapper clone() {
        return new FieldNodeWrapper(this, getParent());
    }
}
