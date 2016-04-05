package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.CastExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;

public class CastExpressionWrapper extends ExpressionWrapper {
    private ExpressionWrapper expression;

    public CastExpressionWrapper(CastExpression castExpression, ASTNodeWrapper parentNodeWrapper) {
        super(castExpression, parentNodeWrapper);
        type = new ClassNodeWrapper(castExpression.getType(), this);
        expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(castExpression.getExpression(), this);
    }

    public CastExpressionWrapper(CastExpressionWrapper castExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(castExpressionWrapper, parentNodeWrapper);
        expression = castExpressionWrapper.getExpression().copy(this);
        type = new ClassNodeWrapper(castExpressionWrapper.getType(), this);
    }
    
    public CastExpressionWrapper(ClassNode typeClass, ExpressionWrapper expression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        type = new ClassNodeWrapper(typeClass, this);
        if (expression != null) {
            expression.setParent(this);
            this.expression = expression;
        }
    }

    @Override
    public String getText() {
        return expression.getText();
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    public void setExpression(ExpressionWrapper expression) {
        this.expression = expression;
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(type);
        astNodeWrappers.add(expression);
        return astNodeWrappers;
    }

    @Override
    public CastExpressionWrapper clone() {
        return new CastExpressionWrapper(this, getParent());
    }
}
