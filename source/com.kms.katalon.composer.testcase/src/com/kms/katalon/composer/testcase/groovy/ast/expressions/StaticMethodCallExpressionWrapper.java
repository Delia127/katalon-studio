package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;

public class StaticMethodCallExpressionWrapper extends ExpressionWrapper {
    private ClassNodeWrapper ownerType;
    private String method;
    private ExpressionWrapper arguments;

    public StaticMethodCallExpressionWrapper(Class<?> ownerType, String method, ExpressionWrapper arguments,
            ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.ownerType = new ClassNodeWrapper(ownerType, this);
        this.method = method;
        this.arguments = arguments;
    }

    public StaticMethodCallExpressionWrapper(StaticMethodCallExpression staticMethodCallExpression,
            ASTNodeWrapper parentNodeWrapper) {
        super(staticMethodCallExpression, parentNodeWrapper);
        this.ownerType = new ClassNodeWrapper(staticMethodCallExpression.getOwnerType(), this);
        this.method = staticMethodCallExpression.getMethod();
        this.arguments = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                staticMethodCallExpression.getArguments(), this);
    }
    
    public StaticMethodCallExpressionWrapper(StaticMethodCallExpressionWrapper staticMethodCallExpressionWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(staticMethodCallExpressionWrapper, parentNodeWrapper);
        this.ownerType = new ClassNodeWrapper(staticMethodCallExpressionWrapper.getOwnerType(), this);
        this.method = staticMethodCallExpressionWrapper.getMethod();
        this.arguments = staticMethodCallExpressionWrapper.getArguments().copy(this);
    }

    public ClassNodeWrapper getOwnerType() {
        return ownerType;
    }

    public String getMethod() {
        return method;
    }

    public ExpressionWrapper getArguments() {
        return arguments;
    }

    @Override
    public String getText() {
        return getOwnerType().getName() + "." + method + arguments.getText();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(ownerType);
        astNodeWrappers.add(arguments);
        return astNodeWrappers;
    }

    @Override
    public StaticMethodCallExpressionWrapper clone() {
        return new StaticMethodCallExpressionWrapper(this, getParent());
    }
}
