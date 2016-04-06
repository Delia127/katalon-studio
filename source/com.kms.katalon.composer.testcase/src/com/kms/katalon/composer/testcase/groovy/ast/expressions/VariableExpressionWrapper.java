package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.VariableExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;

public class VariableExpressionWrapper extends ExpressionWrapper {
    private static final String DEFAULT_VARIABLE_NAME = "a";
    private String variable;
    private ClassNodeWrapper originType;

    public VariableExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        this(DEFAULT_VARIABLE_NAME, parentNodeWrapper);
    }

    public VariableExpressionWrapper(String variable, ASTNodeWrapper parentNodeWrapper) {
        this(variable, Object.class, parentNodeWrapper);
    }

    public VariableExpressionWrapper(String variable, Class<?> originType, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.variable = variable;
        this.originType = new ClassNodeWrapper(originType, this);
    }

    public VariableExpressionWrapper(VariableExpression expression, ASTNodeWrapper parentNodeWrapper) {
        super(expression, parentNodeWrapper);
        this.variable = expression.getName();
        this.originType = new ClassNodeWrapper(expression.getOriginType(), this);
    }

    public VariableExpressionWrapper(VariableExpressionWrapper variableExpressionWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(variableExpressionWrapper, parentNodeWrapper);
        this.variable = variableExpressionWrapper.getName();
        this.originType = new ClassNodeWrapper(variableExpressionWrapper.getOriginType(), this);
    }

    @Override
    public String getText() {
        return getVariable();
    }

    public String getName() {
        return getVariable();
    }

    public ClassNodeWrapper getOriginType() {
        return originType;
    }

    public void setOriginType(ClassNodeWrapper originType) {
        this.originType = originType;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    @Override
    public VariableExpressionWrapper clone() {
        return new VariableExpressionWrapper(this, getParent());
    }
    
    @Override
    public boolean hasAstChildren() {
        return true;
    };
    
    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(originType);
        return astNodeWrappers;
    }
}
