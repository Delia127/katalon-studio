package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class TryCatchStatementWrapper extends CompositeStatementWrapper {
    private List<CatchStatementWrapper> catchStatements = new ArrayList<CatchStatementWrapper>();
    private BlockStatementWrapper finallyStatement = null;
    private BlockStatementWrapper tryBlock;

    public TryCatchStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        tryBlock = new BlockStatementWrapper(this);
        finallyStatement = new BlockStatementWrapper(this);
    }

    public TryCatchStatementWrapper(TryCatchStatement tryCatchStatement, ASTNodeWrapper parentNodeWrapper) {
        super(tryCatchStatement, parentNodeWrapper);
        tryBlock = new BlockStatementWrapper((BlockStatement) tryCatchStatement.getTryStatement(), this);
        for (CatchStatement catchStatement : tryCatchStatement.getCatchStatements()) {
            catchStatements.add(new CatchStatementWrapper(catchStatement, this));
        }
        if (!(tryCatchStatement.getFinallyStatement() instanceof BlockStatement)) {
            return;
        }
        BlockStatement blockStatement = (BlockStatement) tryCatchStatement.getFinallyStatement();
        if (blockStatement.getStatements().size() == 1 && blockStatement.getStatements().get(0) instanceof BlockStatement) {
            finallyStatement = new BlockStatementWrapper((BlockStatement) blockStatement.getStatements().get(0), this);
        }
    }

    public TryCatchStatementWrapper(TryCatchStatementWrapper tryCatchStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(tryCatchStatementWrapper, parentNodeWrapper);
        tryBlock = new BlockStatementWrapper(tryCatchStatementWrapper.getBlock(), this);
        for (CatchStatementWrapper catchStatement : tryCatchStatementWrapper.getCatchStatements()) {
            catchStatements.add(new CatchStatementWrapper(catchStatement, this));
        }
        if (tryCatchStatementWrapper.getFinallyStatement() != null) {
            this.finallyStatement = new BlockStatementWrapper(tryCatchStatementWrapper.getFinallyStatement(), this);
        }
    }

    public List<CatchStatementWrapper> getCatchStatements() {
        return catchStatements;
    }

    public void setCatchStatements(List<CatchStatementWrapper> catchStatements) {
        this.catchStatements = catchStatements;
    }

    public void addCatchStatement(CatchStatementWrapper catchStatement) {
        catchStatements.add(catchStatement);
    }

    public boolean addCatchStatement(CatchStatementWrapper catchStatement, int index) {
        if (index < 0 || index > catchStatements.size()) {
            return false;
        }
        catchStatements.add(index, catchStatement);
        return true;
    }

    public boolean removeCatchStatement(int index) {
        if (index < 0 || index >= catchStatements.size()) {
            return false;
        }
        catchStatements.remove(index);
        return true;
    }

    public boolean removeCatchStatement(CatchStatementWrapper catchStatement) {
        return catchStatements.remove(catchStatement);
    }

    public BlockStatementWrapper getFinallyStatement() {
        return finallyStatement;
    }

    public void setFinallyStatement(BlockStatementWrapper finallyStatement) {
        this.finallyStatement = finallyStatement;
    }

    @Override
    public String getText() {
        return "try";
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(tryBlock);
        astNodeWrappers.addAll(catchStatements);
        if (finallyStatement != null) {
            astNodeWrappers.add(finallyStatement);
        }
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return tryBlock;
    }

    @Override
    public TryCatchStatementWrapper clone() {
        return new TryCatchStatementWrapper(this, getParent());
    }

}
