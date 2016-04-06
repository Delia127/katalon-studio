package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.ExpressionStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

public class ExpressionStatementWrapper extends StatementWrapper {
    private ExpressionWrapper expressionWrapper;

    public ExpressionStatementWrapper(ExpressionWrapper expression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        setExpression(expression);
    }

    public ExpressionStatementWrapper(ExpressionStatement statement, ASTNodeWrapper parentNodeWrapper) {
        super(statement, parentNodeWrapper);
        this.expressionWrapper = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                statement.getExpression(), this);
    }
    
    public ExpressionStatementWrapper(ExpressionStatementWrapper expressionStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(expressionStatementWrapper, parentNodeWrapper);
        this.expressionWrapper = expressionStatementWrapper.getExpression().copy(this);
    }

    public ExpressionWrapper getExpression() {
        return expressionWrapper;
    }

    public void setExpression(ExpressionWrapper expression) {
        if (expression != null) {
            expression.setParent(this);
        }
        this.expressionWrapper = expression;
    }
    
    @Override
    public boolean hasAstChildren() {
        return true;
    }
    
    @Override
    public String getText() {
        return getExpression().getText();
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expressionWrapper);
        return astNodeWrappers;
    }

    @Override
    public ExpressionStatementWrapper clone() {
        return new ExpressionStatementWrapper(this, getParent());
    }
}
