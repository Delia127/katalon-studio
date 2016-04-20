package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.ExpressionStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

public class ExpressionStatementWrapper extends StatementWrapper {
    private ExpressionWrapper expression;
    
    public ExpressionStatementWrapper(ExpressionWrapper expression) {
        this(expression, null);
    }

    public ExpressionStatementWrapper(ExpressionWrapper expression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        setExpression(expression);
    }

    public ExpressionStatementWrapper(ExpressionStatement statement, ASTNodeWrapper parentNodeWrapper) {
        super(statement, parentNodeWrapper);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(statement.getExpression(),
                this);
    }

    public ExpressionStatementWrapper(ExpressionStatementWrapper expressionStatementWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(expressionStatementWrapper, parentNodeWrapper);
        this.expression = expressionStatementWrapper.getExpression().copy(this);
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    public void setExpression(ExpressionWrapper expression) {
        if (expression == null) {
            return;
        }
        expression.setParent(this);
        this.expression = expression;
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
        astNodeWrappers.add(expression);
        return astNodeWrappers;
    }

    @Override
    public ExpressionStatementWrapper clone() {
        return new ExpressionStatementWrapper(this, getParent());
    }

    @Override
    public boolean isInputEditatble() {
        return true;
    }

    @Override
    public String getInputText() {
        if (expression == null) {
            return "";
        }
        return expression.getText();
    }

    @Override
    public ASTNodeWrapper getInput() {
        return expression;
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input == null || !(input instanceof ExpressionWrapper)) {
            return false;
        }
        if (expression.isEqualsTo(input)) {
            return false;
        }
        setExpression((ExpressionWrapper) input);
        return true;
    }
}
