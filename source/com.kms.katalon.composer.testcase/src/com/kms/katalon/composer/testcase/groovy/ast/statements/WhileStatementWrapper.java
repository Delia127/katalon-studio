package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class WhileStatementWrapper extends CompositeStatementWrapper {
    private BooleanExpressionWrapper booleanExpression;
    private BlockStatementWrapper loopBlock;

    public WhileStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(this);
        this.loopBlock = new BlockStatementWrapper(this);
    }

    public WhileStatementWrapper(WhileStatement whileStatement, ASTNodeWrapper parentNodeWrapper) {
        super(whileStatement, parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(whileStatement.getBooleanExpression(), this);
        this.loopBlock = new BlockStatementWrapper((BlockStatement) whileStatement.getLoopBlock(), this);
    }

    public WhileStatementWrapper(WhileStatementWrapper whileStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(whileStatementWrapper, parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(whileStatementWrapper.getBooleanExpression(), this);
        this.loopBlock = new BlockStatementWrapper(whileStatementWrapper.getBlock(), this);
    }

    public BooleanExpressionWrapper getBooleanExpression() {
        return booleanExpression;
    }

    public void setBooleanExpression(BooleanExpressionWrapper booleanExpression) {
        this.booleanExpression = booleanExpression;
    }

    @Override
    public String getText() {
        return "while " + "(" + getBooleanExpression().getText() + ")";
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(booleanExpression);
        astNodeWrappers.add(loopBlock);
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return loopBlock;
    }

    @Override
    public WhileStatementWrapper clone() {
        return new WhileStatementWrapper(this, getParent());
    }

    @Override
    public ASTNodeWrapper getInput() {
        return this.getBooleanExpression();
    }

    @Override
    public String getInputText() {
        return this.getInput().getText();
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input instanceof BooleanExpressionWrapper
                && !AstTreeTableValueUtil.compareAstNode(input, this.getBooleanExpression())) {
            this.setBooleanExpression((BooleanExpressionWrapper) input);
            return true;
        }
        return false;
    }

}
