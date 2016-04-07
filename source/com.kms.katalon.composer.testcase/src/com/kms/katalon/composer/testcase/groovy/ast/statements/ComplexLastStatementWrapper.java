package com.kms.katalon.composer.testcase.groovy.ast.statements;

import org.codehaus.groovy.ast.stmt.BlockStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public abstract class ComplexLastStatementWrapper extends CompositeStatementWrapper {
    public ComplexLastStatementWrapper(BlockStatement block, ASTNodeWrapper parentNodeWrapper) {
        super(block, block, parentNodeWrapper);
    }

    public ComplexLastStatementWrapper(ComplexLastStatementWrapper complexLastStatementWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(complexLastStatementWrapper, parentNodeWrapper);
    }

    public ComplexLastStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }
    
    public ComplexLastStatementWrapper() {
        this(null);
    }

    @Override
    public ComplexLastStatementWrapper copy(ASTNodeWrapper newParent) {
        return (ComplexLastStatementWrapper) super.copy(newParent);
    }
    
    @Override
    public boolean canHaveDescription() {
        return false;
    }
}
