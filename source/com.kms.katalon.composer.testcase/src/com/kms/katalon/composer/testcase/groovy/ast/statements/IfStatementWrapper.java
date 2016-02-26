package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class IfStatementWrapper extends CompositeStatementWrapper {
    private BlockStatementWrapper code;
    private BooleanExpressionWrapper booleanExpression;
    private List<ElseIfStatementWrapper> elseIfStatements = new ArrayList<ElseIfStatementWrapper>();
    private BlockStatementWrapper elseStatement;

    public IfStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(this);
        this.code = new BlockStatementWrapper(parentNodeWrapper);
    }

    public IfStatementWrapper(BooleanExpressionWrapper booleanExpression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.booleanExpression = booleanExpression;
        this.code = new BlockStatementWrapper(parentNodeWrapper);
    }

    public IfStatementWrapper(IfStatement ifStatement, ASTNodeWrapper parentNodeWrapper) {
        super(ifStatement, parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(ifStatement.getBooleanExpression(), this);
        this.code = new BlockStatementWrapper((BlockStatement) ifStatement.getIfBlock(), this);
        getStatementNodeWrappersFromIfStatement(ifStatement.getElseBlock());
    }

    public IfStatementWrapper(IfStatementWrapper ifStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(ifStatementWrapper, parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(ifStatementWrapper.getBooleanExpression(), this);
        this.code = new BlockStatementWrapper(ifStatementWrapper.getBlock(), this);
        for (ElseIfStatementWrapper elseIfStatement : ifStatementWrapper.getElseIfStatements()) {
            elseIfStatements.add(new ElseIfStatementWrapper(elseIfStatement, this));
        }
        if (ifStatementWrapper.getElseStatement() != null) {
            elseStatement = new BlockStatementWrapper(ifStatementWrapper.getElseStatement(), this);
        }
    }

    private void getStatementNodeWrappersFromIfStatement(Statement statement) {
        if (statement instanceof EmptyStatement) {
            return;
        }
        if (statement instanceof IfStatement) {
            IfStatement elseIfStatement = (IfStatement) statement;
            elseIfStatements.add(new ElseIfStatementWrapper(elseIfStatement, this));
            getStatementNodeWrappersFromIfStatement(elseIfStatement.getElseBlock());
        }
        if (statement instanceof BlockStatement) {
            elseStatement = new BlockStatementWrapper((BlockStatement) statement, this);
        }
    }

    public BooleanExpressionWrapper getBooleanExpression() {
        return booleanExpression;
    }

    public void setBooleanExpression(BooleanExpressionWrapper booleanExpression) {
        this.booleanExpression = booleanExpression;
    }

    @Override
    public String getText() {
        return "if (" + getBooleanExpression().getText() + ")";
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    public List<ElseIfStatementWrapper> getElseIfStatements() {
        return elseIfStatements;
    }

    public void setElseIfStatements(List<ElseIfStatementWrapper> elseIfStatements) {
        this.elseIfStatements = elseIfStatements;
    }
    
    public void addElseIfStatement(ElseIfStatementWrapper elseIfStatement) {
        elseIfStatements.add(elseIfStatement);
    }

    public boolean addElseIfStatement(ElseIfStatementWrapper elseIfStatement, int index) {
        if (index < 0 || index > elseIfStatements.size()) {
            return false;
        }
        elseIfStatements.add(index, elseIfStatement);
        return true;
    }

    public boolean removeElseIfStatement(int index) {
        if (index < 0 || index >= elseIfStatements.size()) {
            return false;
        }
        elseIfStatements.remove(index);
        return true;
    }

    public boolean removeElseIfStatement(ElseIfStatementWrapper elseIfStatement) {
        return elseIfStatements.remove(elseIfStatement);
    }

    public BlockStatementWrapper getElseStatement() {
        return elseStatement;
    }

    public void setElseStatement(BlockStatementWrapper elseStatement) {
        this.elseStatement = elseStatement;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(booleanExpression);
        astNodeWrappers.add(code);
        astNodeWrappers.addAll(elseIfStatements);
        if (elseStatement != null) {
            astNodeWrappers.add(elseStatement);
        }
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return code;
    }

    @Override
    public IfStatementWrapper clone() {
        return new IfStatementWrapper(this, getParent());
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
