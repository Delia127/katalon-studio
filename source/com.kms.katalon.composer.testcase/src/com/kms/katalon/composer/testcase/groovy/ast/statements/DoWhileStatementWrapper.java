package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;

public class DoWhileStatementWrapper extends CompositeStatementWrapper {
    private BooleanExpressionWrapper booleanExpression;
    private BlockStatementWrapper loopBlock;

    public DoWhileStatementWrapper(BooleanExpressionWrapper booleanExpression, 
            ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.booleanExpression = booleanExpression;
        this.loopBlock = new BlockStatementWrapper(this);
    }

    public DoWhileStatementWrapper(DoWhileStatement doWhileStatement, ASTNodeWrapper parentNodeWrapper) {
        super(doWhileStatement, parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(doWhileStatement.getBooleanExpression(), this);
        this.loopBlock = new BlockStatementWrapper((BlockStatement) doWhileStatement.getLoopBlock(), this);
    }
    
    public DoWhileStatementWrapper(DoWhileStatementWrapper doWhileStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(doWhileStatementWrapper, parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(doWhileStatementWrapper.getBooleanExpression(), this);
        this.loopBlock = new BlockStatementWrapper(doWhileStatementWrapper.getBlock(), this);
    }

    public BooleanExpressionWrapper getBooleanExpression() {
        return booleanExpression;
    }

    @Override
    public String getText() {
       return "do {...} while" + "(" + getBooleanExpression().getText() + ")";
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(loopBlock);
        astNodeWrappers.add(booleanExpression);
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return loopBlock;
    }

    @Override
    public DoWhileStatementWrapper clone() {
        return new DoWhileStatementWrapper(this, getParent());
    }
}
