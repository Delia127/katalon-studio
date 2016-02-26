package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.MethodPointerExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class MethodPointerExpressionWrapper extends ExpressionWrapper {
    private ExpressionWrapper expression;
    private ExpressionWrapper methodName;

    public MethodPointerExpressionWrapper(ExpressionWrapper expression, ExpressionWrapper methodName,
            ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = expression;
        this.methodName = methodName;
    }

    public MethodPointerExpressionWrapper(MethodPointerExpression methodPointerExpression,
            ASTNodeWrapper parentNodeWrapper) {
        super(methodPointerExpression, parentNodeWrapper);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                methodPointerExpression.getExpression(), this);
        this.methodName = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                methodPointerExpression.getMethodName(), this);
    }
    
    public MethodPointerExpressionWrapper(MethodPointerExpressionWrapper methodPointerExpressionWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(methodPointerExpressionWrapper, parentNodeWrapper);
        this.expression = methodPointerExpressionWrapper.getExpression().copy(this);
        this.methodName = methodPointerExpressionWrapper.getMethodName().copy(this);
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }
    
    public ExpressionWrapper getMethodName() {
        return methodName;
    }
    
    @Override
    public String getText() {
        if (expression == null) {
            return "&" + methodName.getText();
        } else {
            return expression.getText() + ".&" + methodName.getText();
        }
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expression);
        astNodeWrappers.add(methodName);
        return astNodeWrappers;
    }

    @Override
    public MethodPointerExpressionWrapper clone() {
        return new MethodPointerExpressionWrapper(this, getParent());
    }
}
