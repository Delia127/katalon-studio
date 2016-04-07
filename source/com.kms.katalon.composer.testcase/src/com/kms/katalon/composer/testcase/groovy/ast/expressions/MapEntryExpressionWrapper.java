package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.MapEntryExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class MapEntryExpressionWrapper extends ExpressionWrapper {
    private ExpressionWrapper keyExpression;

    private ExpressionWrapper valueExpression;

    public MapEntryExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.keyExpression = new ConstantExpressionWrapper("", this);
        this.valueExpression = new ConstantExpressionWrapper("", this);
    }

    public MapEntryExpressionWrapper(ExpressionWrapper keyExpression, ExpressionWrapper valueExpression,
            ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        setKeyExpression(keyExpression);
        setValueExpression(valueExpression);
    }

    public MapEntryExpressionWrapper(MapEntryExpression expression, ASTNodeWrapper parentNodeWrapper) {
        super(expression, parentNodeWrapper);
        keyExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression.getKeyExpression(), this);
        valueExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression.getValueExpression(),
                this);
    }

    public MapEntryExpressionWrapper(MapEntryExpressionWrapper mapEntryExpressionWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(mapEntryExpressionWrapper, parentNodeWrapper);
        copyMapEntryProperties(mapEntryExpressionWrapper);
    }

    private void copyMapEntryProperties(MapEntryExpressionWrapper mapEntryExpressionWrapper) {
        keyExpression = mapEntryExpressionWrapper.getKeyExpression().copy(this);
        valueExpression = mapEntryExpressionWrapper.getValueExpression().copy(this);
    }

    public ExpressionWrapper getKeyExpression() {
        return keyExpression;
    }

    public void setKeyExpression(ExpressionWrapper keyExpression) {
        if (keyExpression == null) {
            return;
        }
        keyExpression.setParent(this);
        this.keyExpression = keyExpression;
    }

    public ExpressionWrapper getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(ExpressionWrapper valueExpression) {
        if (valueExpression == null) {
            return;
        }
        valueExpression.setParent(this);
        this.valueExpression = valueExpression;
    }

    @Override
    public String getText() {
        return keyExpression.getText() + ":" + valueExpression.getText();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(keyExpression);
        astNodeWrappers.add(valueExpression);
        return astNodeWrappers;
    }

    @Override
    public MapEntryExpressionWrapper clone() {
        return new MapEntryExpressionWrapper(this, getParent());
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
        if (!(input instanceof MapEntryExpressionWrapper) || this.isEqualsTo(input)) {
            return false;
        }
        copyMapEntryProperties((MapEntryExpressionWrapper) input);
        return true;
    }
}
