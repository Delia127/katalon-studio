package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTHasBlock;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

/**
 * Created by taittle on 3/24/16.
 */
public abstract class CompositeStatementWrapper extends StatementWrapper implements ASTHasBlock {
    protected BlockStatementWrapper block;

    public CompositeStatementWrapper(StatementWrapper statementWrapper, BlockStatementWrapper block,
            ASTNodeWrapper parentNodeWrapper) {
        super(statementWrapper, parentNodeWrapper);
        this.block = new BlockStatementWrapper(block, this);
    }

    public CompositeStatementWrapper(Statement statement, BlockStatement block, ASTNodeWrapper parentNodeWrapper) {
        super(statement, parentNodeWrapper);
        this.block = new BlockStatementWrapper(block, this);
    }

    public CompositeStatementWrapper(CompositeStatementWrapper compositeStatement,
            ASTNodeWrapper parentNodeWrapper) {
        super(compositeStatement, parentNodeWrapper);
        this.block = new BlockStatementWrapper(compositeStatement.getBlock(), this);
    }

    public CompositeStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        block = new BlockStatementWrapper(this);
    }
    
    public CompositeStatementWrapper() {
        this(null);
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(block);
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return block;
    }

    @Override
    public CompositeStatementWrapper copy(ASTNodeWrapper newParent) {
        return (CompositeStatementWrapper) super.copy(newParent);
    }
    
    @Override
    public boolean isChildAssignble(ASTNodeWrapper astNode) {
        return getBlock().isChildAssignble(astNode);
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject) {
        return getBlock().addChild(childObject);
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject, int index) {
        return getBlock().addChild(childObject, index);
    }

    @Override
    public boolean removeChild(ASTNodeWrapper childObject) {
        return getBlock().removeChild(childObject);
    }

    @Override
    public int indexOf(ASTNodeWrapper childObject) {
        return getBlock().indexOf(childObject);
    }
}
