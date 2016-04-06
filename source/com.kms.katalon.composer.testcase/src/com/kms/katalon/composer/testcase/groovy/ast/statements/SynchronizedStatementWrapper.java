package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

public class SynchronizedStatementWrapper extends CompositeStatementWrapper {
    private ExpressionWrapper expression;
    private BlockStatementWrapper code;

    public SynchronizedStatementWrapper(ExpressionWrapper expression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = expression;
        this.code = new BlockStatementWrapper(this);
    }

    public SynchronizedStatementWrapper(SynchronizedStatement synchronizedStatement, ASTNodeWrapper parentNodeWrapper) {
        super(synchronizedStatement, parentNodeWrapper);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(synchronizedStatement.getExpression(), this);
        this.code = new BlockStatementWrapper((BlockStatement) synchronizedStatement.getCode(), this);
    }

    public SynchronizedStatementWrapper(SynchronizedStatementWrapper synchronizedStatementWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(synchronizedStatementWrapper, parentNodeWrapper);
        this.expression = synchronizedStatementWrapper.getExpression().copy(this);
        this.code = new BlockStatementWrapper(synchronizedStatementWrapper.getBlock(), this);
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    public void setExpression(ExpressionWrapper expression) {
        this.expression = expression;
    }

    @Override
    public String getText() {
        return "synchronized (" + getExpression().getText() + ")";
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expression);
        astNodeWrappers.add(code);
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return code;
    }

    @Override
    public SynchronizedStatementWrapper clone() {
        return new SynchronizedStatementWrapper(this, getParent());
    }
}
