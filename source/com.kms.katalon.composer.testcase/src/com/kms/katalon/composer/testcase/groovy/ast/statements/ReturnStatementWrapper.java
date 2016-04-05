package com.kms.katalon.composer.testcase.groovy.ast.statements;

import org.codehaus.groovy.ast.stmt.ReturnStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

public class ReturnStatementWrapper extends StatementWrapper {
    private ExpressionWrapper expression;

    public ReturnStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public ReturnStatementWrapper(ReturnStatement returnStatement, ASTNodeWrapper parentNodeWrapper) {
        super(returnStatement, parentNodeWrapper);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(returnStatement.getExpression(),
                this);
    }
    
    public ReturnStatementWrapper(ReturnStatementWrapper returnStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(returnStatementWrapper, parentNodeWrapper);
        this.expression = returnStatementWrapper.getExpression().copy(this);
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    public void setExpression(ExpressionWrapper expression) {
        this.expression = expression;
    }

    @Override
    public String getText() {
        return "return";
    }

    @Override
    public ReturnStatementWrapper clone() {
        return new ReturnStatementWrapper(this, getParent());
    }

}
