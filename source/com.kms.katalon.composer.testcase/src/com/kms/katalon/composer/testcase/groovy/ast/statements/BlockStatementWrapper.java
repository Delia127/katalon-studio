package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.CommentWrapper;

/**
 * Base class for any statement contains list of statements
 *
 */
public class BlockStatementWrapper extends CompositeStatementWrapper {
    protected List<StatementWrapper> statements = new ArrayList<StatementWrapper>();
    protected List<CommentWrapper> insideComments = new ArrayList<CommentWrapper>();

    public BlockStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public BlockStatementWrapper(BlockStatementWrapper blockStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(blockStatementWrapper, parentNodeWrapper);
        for (StatementWrapper statement : blockStatementWrapper.getStatements()) {
            statements.add(statement.copy(this));
        }
        for (CommentWrapper insideComment : blockStatementWrapper.getInsideComments()) {
            insideComments.add(new CommentWrapper(insideComment, this));
        }
    }

    public BlockStatementWrapper(List<StatementWrapper> statements, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        addStatements(statements);
    }

    public BlockStatementWrapper(BlockStatement blockStatement, ASTNodeWrapper parentNodeWrapper) {
        super(blockStatement, parentNodeWrapper);
        statements.addAll(ASTNodeWrapHelper.getStatementNodeWrappersFromBlockStatement(blockStatement, this));
        if (getStatements().size() == 1 && getStatements().get(0) instanceof ReturnStatementWrapper
                && (((ReturnStatementWrapper) getStatements().get(0)).getExpression().getText().equals("null"))) {
            getStatements().clear();
        }
    }

    public List<StatementWrapper> getStatements() {
        return statements;
    }

    public void addStatement(StatementWrapper statement) {
        statements.add(statement);
    }

    public boolean addStatement(StatementWrapper statement, int index) {
        if (index < 0 || index > statements.size()) {
            return false;
        }
        statements.add(index, statement);
        return true;
    }

    public void addStatements(List<StatementWrapper> listOfStatements) {
        statements.addAll(listOfStatements);
    }
    
    public boolean removeStatement(StatementWrapper statement) {
        return statements.remove(statement);
    }
    
    public boolean removeStatement(int index) {
        if (index < 0 || index >= statements.size()) {
            return false;
        }
        statements.remove(index);
        return true;
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(statements);
        return astNodeWrappers;
    }

    public List<CommentWrapper> getInsideComments() {
        return insideComments;
    }

    @Override
    public void setInsideComments(List<CommentWrapper> commentWrapperList) {
        if (!statements.isEmpty()) {
            super.setInsideComments(commentWrapperList);
            return;
        }
        if (commentWrapperList == null || commentWrapperList.isEmpty()) {
            return;
        }
        insideComments.addAll(commentWrapperList);
    }

    @Override
    public BlockStatementWrapper clone() {
        return new BlockStatementWrapper(this, getParent());
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return this;
    }
}
