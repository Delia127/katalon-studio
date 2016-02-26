package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;

public class ElseIfStatementWrapper extends CompositeStatementWrapper {
    private BooleanExpressionWrapper booleanExpression;
    private BlockStatementWrapper code;

    public ElseIfStatementWrapper(IfStatementWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(this);
        this.code = new BlockStatementWrapper(parentNodeWrapper);
    }

    public ElseIfStatementWrapper(IfStatement ifStatement, IfStatementWrapper parentNodeWrapper) {
        super(ifStatement, parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(ifStatement.getBooleanExpression(), this);
        this.code = new BlockStatementWrapper((BlockStatement) ifStatement.getIfBlock(), this);
        this.lineNumber = booleanExpression.getLineNumber();
        this.columnNumber = booleanExpression.getColumnNumber() - 1;
        this.lastLineNumber = ifStatement.getIfBlock().getLastLineNumber();
        this.lastColumnNumber = ifStatement.getIfBlock().getLastColumnNumber() - 1;
        this.start = booleanExpression.getStart();
        this.end = ifStatement.getIfBlock().getEnd();
    }

    public ElseIfStatementWrapper(ElseIfStatementWrapper elseIfStatementWrapper, IfStatementWrapper parentNodeWrapper) {
        super(elseIfStatementWrapper, parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(elseIfStatementWrapper.getBooleanExpression(), this);
        this.code = new BlockStatementWrapper(elseIfStatementWrapper.getBlock(), this);
    }

    public BooleanExpressionWrapper getBooleanExpression() {
        return booleanExpression;
    }

    public void setBooleanExpression(BooleanExpressionWrapper booleanExpression) {
        this.booleanExpression = booleanExpression;
    }

    @Override
    public String getText() {
        return "else if (" + getBooleanExpression().getText() + ")";
    }

    @Override
    public IfStatementWrapper getParent() {
        return (IfStatementWrapper) super.getParent();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(booleanExpression);
        astNodeWrappers.add(code);
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return code;
    }

    @Override
    public ElseIfStatementWrapper clone() {
        return new ElseIfStatementWrapper(this, getParent());
    }

    @Override
    public ASTNodeWrapper getInput() {
        return this.getBooleanExpression();
    }

    @Override
    public String getInputText() {
        return getInput().getText();
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input instanceof BooleanExpressionWrapper && !AstTreeTableValueUtil.compareAstNode(input, this.getBooleanExpression())) {
            this.setBooleanExpression((BooleanExpressionWrapper) input);
            return true;
        }
        return false;
    }
}
