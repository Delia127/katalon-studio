package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.PropertyExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class PropertyExpressionWrapper extends ExpressionWrapper {
    private static final String THIS_VARIABLE_NAME = "this";
    private ExpressionWrapper objectExpression;
    private ExpressionWrapper property;
    private boolean spreadSafe = false;
    private boolean safe = false;
    private boolean isStatic = false;

    public PropertyExpressionWrapper(String variableName, String propertyName, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.objectExpression = new VariableExpressionWrapper(variableName, this);
        this.property = new ConstantExpressionWrapper(propertyName, this);
    }

    public PropertyExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        this(THIS_VARIABLE_NAME, null, parentNodeWrapper);
    }

    public PropertyExpressionWrapper(PropertyExpression propertyExpression, ASTNodeWrapper parentNodeWrapper) {
        super(propertyExpression, parentNodeWrapper);
        this.objectExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                propertyExpression.getObjectExpression(), this);
        this.property = ASTNodeWrapHelper
                .getExpressionNodeWrapperFromExpression(propertyExpression.getProperty(), this);
        this.spreadSafe = propertyExpression.isSpreadSafe();
        this.safe = propertyExpression.isSafe();
        this.isStatic = propertyExpression.isStatic();
    }

    public PropertyExpressionWrapper(PropertyExpressionWrapper propertyExpressionWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(propertyExpressionWrapper, parentNodeWrapper);
        this.objectExpression = propertyExpressionWrapper.getObjectExpression().copy(this);
        this.property = propertyExpressionWrapper.getProperty().copy(this);
        this.spreadSafe = propertyExpressionWrapper.isSpreadSafe();
        this.safe = propertyExpressionWrapper.isSafe();
        this.isStatic = propertyExpressionWrapper.isStatic();
    }

    public ExpressionWrapper getObjectExpression() {
        return objectExpression;
    }

    public String getObjectExpressionAsString() {
        if (!(objectExpression instanceof ConstantExpressionWrapper)) {
            return objectExpression.getText();
        }
        return String.valueOf(((ConstantExpressionWrapper) objectExpression).getValue());
    }

    public void setObjectExpression(ExpressionWrapper objectExpression) {
        this.objectExpression = objectExpression;
    }

    public boolean isObjectExpressionOfClass(Class<?> clazz) {
        String objectExpressionString = getObjectExpressionAsString();
        return objectExpressionString.equals(clazz.getName()) || objectExpressionString.equals(clazz.getSimpleName());
    }

    public ExpressionWrapper getProperty() {
        return property;
    }

    public void setProperty(ExpressionWrapper property) {
        this.property = property;
    }

    public void setProperty(String propertyString) {
        if (!(property instanceof ConstantExpressionWrapper)) {
            return;
        }
        ((ConstantExpressionWrapper) property).setValue(propertyString);
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

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    @Override
    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getObjectExpressionAsString());
        if (isSpreadSafe()) {
            stringBuilder.append("*");
        }
        if (isSpreadSafe()) {
            stringBuilder.append("*");
        } else if (isSafe()) {
            stringBuilder.append("?");
        }
        stringBuilder.append(".");
        stringBuilder.append(getPropertyAsString());
        return stringBuilder.toString();
    }

    public String getPropertyAsString() {
        if (!(property instanceof ConstantExpressionWrapper)) {
            return property.getText();
        }
        return String.valueOf(((ConstantExpressionWrapper) property).getValue());
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(objectExpression);
        astNodeWrappers.add(property);
        return astNodeWrappers;
    }

    @Override
    public PropertyExpressionWrapper clone() {
        return new PropertyExpressionWrapper(this, getParent());
    }
}
