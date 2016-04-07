package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;

public class ElseIfStatementWrapper extends ComplexChildStatementWrapper {
    protected BooleanExpressionWrapper expression;
    
    public ElseIfStatementWrapper() {
        this(null);
    }

    public ElseIfStatementWrapper(IfStatementWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = new BooleanExpressionWrapper(this);
    }

    public ElseIfStatementWrapper(IfStatement ifStatement, IfStatementWrapper parentNodeWrapper) {
        super(ifStatement, (BlockStatement) ifStatement.getIfBlock(), parentNodeWrapper);
        this.expression = new BooleanExpressionWrapper(ifStatement.getBooleanExpression(), this);
        this.lineNumber = expression.getLineNumber();
        this.columnNumber = expression.getColumnNumber() - 1;
        this.lastLineNumber = ifStatement.getIfBlock().getLastLineNumber();
        this.lastColumnNumber = ifStatement.getIfBlock().getLastColumnNumber() - 1;
        this.start = expression.getStart();
        this.end = ifStatement.getIfBlock().getEnd();
    }

    public ElseIfStatementWrapper(ElseIfStatementWrapper elseIfStatementWrapper, IfStatementWrapper parentNodeWrapper) {
        super(elseIfStatementWrapper, parentNodeWrapper);
        this.expression = new BooleanExpressionWrapper(elseIfStatementWrapper.getBooleanExpression(), this);
    }

    public BooleanExpressionWrapper getBooleanExpression() {
        return (BooleanExpressionWrapper) expression;
    }

    public void setBooleanExpression(BooleanExpressionWrapper booleanExpression) {
        if (booleanExpression == null) {
            return;
        }
        booleanExpression.setParent(this);
        this.expression = booleanExpression;
    }

    @Override
    public String getText() {
        return "else if (" + getInputText() + ")";
    }

    @Override
    public IfStatementWrapper getParent() {
        return (IfStatementWrapper) super.getParent();
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expression);
        astNodeWrappers.addAll(super.getAstChildren());
        return astNodeWrappers;
    }

    @Override
    public ElseIfStatementWrapper clone() {
        return new ElseIfStatementWrapper(this, getParent());
    }

    @Override
    public BooleanExpressionWrapper getInput() {
        return getBooleanExpression();
    }

    @Override
    public String getInputText() {
        return getInput().getText();
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input instanceof BooleanExpressionWrapper && !this.getBooleanExpression().isEqualsTo(input)) {
            setBooleanExpression((BooleanExpressionWrapper) input);
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean isAstNodeBelongToParentComplex(ASTNodeWrapper astNode) {
        return astNode instanceof ElseIfStatementWrapper || astNode instanceof ElseStatementWrapper;
    }
}
