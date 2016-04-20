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
    
    public VariableExpressionWrapper() {
        this(DEFAULT_VARIABLE_NAME, null);
    }

    public VariableExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        this(DEFAULT_VARIABLE_NAME, parentNodeWrapper);
    }
    
    public VariableExpressionWrapper(String variable) {
        this(variable, null);
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
        copyVariableProperties(variableExpressionWrapper);
    }

    private void copyVariableProperties(VariableExpressionWrapper variableExpressionWrapper) {
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
        if (originType == null) {
            return;
        }
        originType.setParent(this);
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
    
    @Override
    public boolean isInputEditatble() {
        return true;
    }

    @Override
    public ASTNodeWrapper getInput() {
        return this;
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (!(input instanceof VariableExpressionWrapper) || this.isEqualsTo(input)) {
            return false;
        }
        copyVariableProperties((VariableExpressionWrapper) input);
        return true;
    }
}
