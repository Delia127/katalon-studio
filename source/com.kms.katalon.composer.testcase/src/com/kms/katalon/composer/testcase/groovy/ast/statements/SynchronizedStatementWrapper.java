package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

public class SynchronizedStatementWrapper extends CompositeStatementWrapper {
    private ExpressionWrapper expression;

    public SynchronizedStatementWrapper(ExpressionWrapper expression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = expression;
    }

    public SynchronizedStatementWrapper(SynchronizedStatement synchronizedStatement, ASTNodeWrapper parentNodeWrapper) {
        super(synchronizedStatement, initCodeBlock(synchronizedStatement), parentNodeWrapper);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                synchronizedStatement.getExpression(), this);
    }

    public SynchronizedStatementWrapper(SynchronizedStatementWrapper synchronizedStatementWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(synchronizedStatementWrapper, parentNodeWrapper);
        this.expression = synchronizedStatementWrapper.getExpression().copy(this);
    }
    
    private static BlockStatement initCodeBlock(SynchronizedStatement synchronizedStatement) {
        Statement code = synchronizedStatement.getCode();
        if (code instanceof BlockStatement) {
            return (BlockStatement) code;
        }
        BlockStatement block = new BlockStatement();
        block.addStatement(code);
        return block;
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
        astNodeWrappers.addAll(super.getAstChildren());
        return astNodeWrappers;
    }

    @Override
    public SynchronizedStatementWrapper clone() {
        return new SynchronizedStatementWrapper(this, getParent());
    }
}
