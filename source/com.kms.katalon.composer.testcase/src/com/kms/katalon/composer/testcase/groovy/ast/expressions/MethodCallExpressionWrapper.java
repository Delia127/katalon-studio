package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.MethodCallExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class MethodCallExpressionWrapper extends ExpressionWrapper {
    private static final String TO_STRING_METHOD_NAME = "toString";

    private ExpressionWrapper objectExpression;
    private ExpressionWrapper method;
    private ExpressionWrapper arguments;
    private boolean spreadSafe = false;
    private boolean safe = false;

    public MethodCallExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        this("this", TO_STRING_METHOD_NAME, parentNodeWrapper);
    }

    public MethodCallExpressionWrapper(String classSimpleName, String method, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.objectExpression = new VariableExpressionWrapper(classSimpleName, this);
        this.method = new ConstantExpressionWrapper(method, this);
        this.arguments = new ArgumentListExpressionWrapper(this);
    }

    public MethodCallExpressionWrapper(Class<?> clazz, String method, ASTNodeWrapper parentNodeWrapper) {
        this(clazz.getSimpleName(), method, parentNodeWrapper);
    }

    public MethodCallExpressionWrapper(MethodCallExpression expression, ASTNodeWrapper parentNodeWrapper) {
        super(expression, parentNodeWrapper);
        objectExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression.getObjectExpression(),
                this);
        method = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression.getMethod(), this);
        arguments = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression.getArguments(), this);
    }

    public MethodCallExpressionWrapper(MethodCallExpressionWrapper methodCallExpressionWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(methodCallExpressionWrapper, parentNodeWrapper);
        objectExpression = methodCallExpressionWrapper.getObjectExpression().copy(this);
        method = methodCallExpressionWrapper.getMethod().copy(this);
        arguments = methodCallExpressionWrapper.getArguments().copy(this);
    }

    public ExpressionWrapper getObjectExpression() {
        return objectExpression;
    }

    public void setObjectExpression(ExpressionWrapper objectExpression) {
        this.objectExpression = objectExpression;
    }

    public String getObjectExpressionAsString() {
        if (!(objectExpression instanceof ConstantExpressionWrapper)) {
            return objectExpression.getText();
        }
        return ((ConstantExpressionWrapper) objectExpression).getValue().toString();
    }
    
    public boolean isObjectExpressionOfClass(Class<?> clazz) {
        String objectExpressionString = getObjectExpressionAsString();
        return objectExpressionString.equals(clazz.getName()) || objectExpressionString.equals(clazz.getSimpleName());
    }

    /**
     * This method returns the method name as String if it is no dynamic calculated method name, but a constant.
     */
    public String getMethodAsString() {
        if (!(method instanceof ConstantExpressionWrapper)) {
            return null;
        }
        return ((ConstantExpressionWrapper) method).getValue().toString();
    }

    public ExpressionWrapper getMethod() {
        return method;
    }

    public void setMethod(ExpressionWrapper method) {
        this.method = method;
    }

    public void setMethod(String method) {
        ConstantExpressionWrapper newConstant = new ConstantExpressionWrapper(method, this);
        newConstant.copyProperties(this.method);
        this.method = newConstant;
    }

    public ExpressionWrapper getArguments() {
        return arguments;
    }

    public void setArguments(ExpressionWrapper arguments) {
        this.arguments = arguments;
    }

    public boolean isSpreadSafe() {
        return spreadSafe;
    }

    public void setSpreadSafe(boolean spreadSafe) {
        this.spreadSafe = spreadSafe;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    @Override
    public String getText() {
        String object = objectExpression.getText();
        return (object.equals("this") ? "" : object + ".")
                + ((method instanceof ConstantExpressionWrapper) ? ((ConstantExpressionWrapper) method).getValue()
                        : method.getText()) + arguments.getText();

    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(objectExpression);
        astNodeWrappers.add(method);
        astNodeWrappers.add(arguments);
        return astNodeWrappers;
    }

    @Override
    public MethodCallExpressionWrapper clone() {
        return new MethodCallExpressionWrapper(this, getParent());
    }
}
