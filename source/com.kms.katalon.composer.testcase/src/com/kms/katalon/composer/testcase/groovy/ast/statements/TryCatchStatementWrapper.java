package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTHasBlock;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class TryCatchStatementWrapper extends ComplexStatementWrapper<CatchStatementWrapper, FinallyStatementWrapper>
        implements ASTHasBlock {
    private BlockStatementWrapper block;
    
    public TryCatchStatementWrapper() {
        this(null);
    }

    public TryCatchStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        block = new BlockStatementWrapper(this);
        lastStatement = new FinallyStatementWrapper(this);
    }

    public TryCatchStatementWrapper(TryCatchStatement tryCatchStatement, ASTNodeWrapper parentNodeWrapper) {
        super(tryCatchStatement, parentNodeWrapper);
        block = new BlockStatementWrapper((BlockStatement) tryCatchStatement.getTryStatement(), this);
        for (CatchStatement catchStatement : tryCatchStatement.getCatchStatements()) {
            complexChildStatements.add(new CatchStatementWrapper(catchStatement, this));
        }
        if (!(tryCatchStatement.getFinallyStatement() instanceof BlockStatement)) {
            return;
        }
        BlockStatement blockStatement = (BlockStatement) tryCatchStatement.getFinallyStatement();
        if (blockStatement.getStatements().size() == 1
                && blockStatement.getStatements().get(0) instanceof BlockStatement) {
            lastStatement = new FinallyStatementWrapper((BlockStatement) blockStatement.getStatements().get(0), this);
        }
    }

    public TryCatchStatementWrapper(TryCatchStatementWrapper tryCatchStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(tryCatchStatementWrapper, parentNodeWrapper);
        block = new BlockStatementWrapper(tryCatchStatementWrapper.getBlock(), this);
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
        astNodeWrappers.add(block);
        astNodeWrappers.addAll(super.getAstChildren());
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return block;
    }

    @Override
    public TryCatchStatementWrapper clone() {
        return new TryCatchStatementWrapper(this, getParent());
    }

    @Override
    public boolean isChildAssignble(ASTNodeWrapper nodeWrapper) {
        return (nodeWrapper instanceof CatchStatementWrapper || nodeWrapper instanceof FinallyStatementWrapper || getBlock().isChildAssignble(
                nodeWrapper));
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject) {
        if (childObject instanceof CatchStatementWrapper) {
            addComplexChildStatement((CatchStatementWrapper) childObject);
            return true;
        }
        if (childObject instanceof FinallyStatementWrapper) {
            return setLastStatement((FinallyStatementWrapper) childObject);
        }
        return getBlock().addChild(childObject);
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject, int index) {
        if (childObject instanceof CatchStatementWrapper) {
            return addComplexChildStatement((CatchStatementWrapper) childObject, index);
        }
        if (childObject instanceof FinallyStatementWrapper) {
            return setLastStatement((FinallyStatementWrapper) childObject);
        }
        return getBlock().addChild(childObject, index);
    }

    @Override
    public boolean removeChild(ASTNodeWrapper childObject) {
        if (childObject instanceof CatchStatementWrapper) {
            return removeComplexChildStatement((CatchStatementWrapper) childObject);
        }
        if (childObject == lastStatement) {
            return removeLastStatement();
        }
        return getBlock().removeChild(childObject);
    }
    
    @Override
    public int indexOf(ASTNodeWrapper childObject) {
        if (childObject instanceof CatchStatementWrapper) {
            return indexOf((CatchStatementWrapper) childObject);
        } else if (childObject == lastStatement) {
            return 0;
        }
        return getBlock().indexOf(childObject);
    }
}
