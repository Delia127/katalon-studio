package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.WhileStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;

public class WhileStatementWrapper extends CompositeStatementWrapper {
    private BooleanExpressionWrapper booleanExpression;

    public WhileStatementWrapper() {
        this(null);
    }
    
    public WhileStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(this);
    }

    public WhileStatementWrapper(WhileStatement whileStatement, ASTNodeWrapper parentNodeWrapper) {
        super(whileStatement, initLoopBlock(whileStatement), parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(whileStatement.getBooleanExpression(), this);
    }

    public WhileStatementWrapper(WhileStatementWrapper whileStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(whileStatementWrapper, parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(whileStatementWrapper.getBooleanExpression(), this);
    }
    
    private static BlockStatement initLoopBlock(WhileStatement whileStatement) {
        Statement loopBlock = whileStatement.getLoopBlock();
        if (loopBlock instanceof BlockStatement) {
            return (BlockStatement) loopBlock;
        }
        BlockStatement block = new BlockStatement();
        block.addStatement(loopBlock);
        return block;
    }

    public BooleanExpressionWrapper getBooleanExpression() {
        return booleanExpression;
    }

    public void setBooleanExpression(BooleanExpressionWrapper booleanExpression) {
        if (booleanExpression == null) {
            return;
        }
        booleanExpression.setParent(this);
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
        astNodeWrappers.addAll(super.getAstChildren());
        return astNodeWrappers;
    }

    @Override
    public WhileStatementWrapper clone() {
        return new WhileStatementWrapper(this, getParent());
    }

    @Override
    public boolean isInputEditatble() {
        return true;
    }

    @Override
    public ASTNodeWrapper getInput() {
        return getBooleanExpression();
    }

    @Override
    public String getInputText() {
        return getInput().getText();
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input instanceof BooleanExpressionWrapper && !getBooleanExpression().isEqualsTo(input)) {
            setBooleanExpression((BooleanExpressionWrapper) input);
            return true;
        }
        return false;
    }

}
