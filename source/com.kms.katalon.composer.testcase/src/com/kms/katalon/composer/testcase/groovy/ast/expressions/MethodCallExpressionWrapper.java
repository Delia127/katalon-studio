package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class MethodCallExpressionWrapper extends ExpressionWrapper {
    public static final String TO_STRING_METHOD_NAME = "toString";

    public static final String THIS_VARIABLE = "this";

    private ExpressionWrapper objectExpression;

    private ExpressionWrapper method;

    private ArgumentListExpressionWrapper arguments;

    private boolean spreadSafe = false;

    private boolean safe = false;

    public MethodCallExpressionWrapper() {
        this(null);
    }

    public MethodCallExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        this("this", TO_STRING_METHOD_NAME, parentNodeWrapper);
    }

    public MethodCallExpressionWrapper(String classSimpleName, String method) {
        this(classSimpleName, method, null);
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
        if (expression.getArguments() instanceof ArgumentListExpression) {
            arguments = new ArgumentListExpressionWrapper((ArgumentListExpression) expression.getArguments(), this);
        } else {
            arguments = new ArgumentListExpressionWrapper(this);
        }
    }

    public MethodCallExpressionWrapper(MethodCallExpressionWrapper methodCallExpressionWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(methodCallExpressionWrapper, parentNodeWrapper);
        copyMethodCallProperties(methodCallExpressionWrapper);
    }

    private void copyMethodCallProperties(MethodCallExpressionWrapper methodCallExpressionWrapper) {
        objectExpression = methodCallExpressionWrapper.getObjectExpression().copy(this);
        method = methodCallExpressionWrapper.getMethod().copy(this);
        arguments = new ArgumentListExpressionWrapper(methodCallExpressionWrapper.getArguments(), this);
    }

    public ExpressionWrapper getObjectExpression() {
        return objectExpression;
    }

    public void setObjectExpression(ExpressionWrapper objectExpression) {
        if (objectExpression == null) {
            return;
        }
        objectExpression.setParent(this);
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
        return StringUtils.equals(objectExpressionString, clazz.getName())
                || StringUtils.equals(objectExpressionString, clazz.getSimpleName());
    }

    public boolean isObjectExpressionOfClass(String className) {
        return StringUtils.equals(getObjectExpressionAsString(), className);
    }

    /**
     * This method returns the method name as String if it is no dynamic calculated method name, but a constant.
     */
    public String getMethodAsString() {
        if (!(method instanceof ConstantExpressionWrapper)) {
            return method.getText();
        }
        return ((ConstantExpressionWrapper) method).getValueAsString();
    }

    public ExpressionWrapper getMethod() {
        return method;
    }

    public boolean setMethod(ExpressionWrapper method) {
        if (method == null || method.isEqualsTo(this.method)) {
            return false;
        }
        method.copyProperties(this.method);
        method.setParent(this);
        this.method = method;
        return true;
    }

    public boolean setMethod(String method) {
        if (StringUtils.equals(method, getMethodAsString())) {
            return false;
        }
        ConstantExpressionWrapper newConstant = new ConstantExpressionWrapper(method, this);
        newConstant.copyProperties(this.method);
        this.method = newConstant;
        return true;
    }

    public ArgumentListExpressionWrapper getArguments() {
        return arguments;
    }

    public boolean setArguments(ArgumentListExpressionWrapper arguments) {
        if (arguments == null || arguments.isEqualsTo(this.arguments)) {
            return false;
        }
        arguments.setParent(this);
        arguments.copyProperties(this.arguments);
        this.arguments = arguments;
        return true;
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
        if (!(input instanceof MethodCallExpressionWrapper) || this.isEqualsTo(input)) {
            return false;
        }
        copyMethodCallProperties((MethodCallExpressionWrapper) input);
        return true;
    }

    @Override
    public boolean replaceChild(ASTNodeWrapper oldChild, ASTNodeWrapper newChild) {
        if (oldChild == getObjectExpression() && newChild instanceof ExpressionWrapper) {
            setObjectExpression((ExpressionWrapper) newChild);
            return true;
        } else if (oldChild == getMethod() && newChild instanceof ExpressionWrapper) {
            setMethod((ExpressionWrapper) newChild);
            return true;
        } else if (oldChild == getArguments() && newChild instanceof ArgumentListExpressionWrapper) {
            setArguments((ArgumentListExpressionWrapper) newChild);
            return true;
        } 
        return super.replaceChild(oldChild, newChild);
    }
}
