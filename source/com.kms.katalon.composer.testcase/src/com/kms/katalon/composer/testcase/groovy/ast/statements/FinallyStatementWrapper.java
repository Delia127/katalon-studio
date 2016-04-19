package com.kms.katalon.composer.testcase.groovy.ast.statements;

import org.codehaus.groovy.ast.stmt.BlockStatement;

public class FinallyStatementWrapper extends ComplexLastStatementWrapper {
    public FinallyStatementWrapper(FinallyStatementWrapper finallyStatementWrapper, TryCatchStatementWrapper parentNodeWrapper) {
        super(finallyStatementWrapper, parentNodeWrapper);
    }

    public FinallyStatementWrapper(BlockStatement block, TryCatchStatementWrapper parentNodeWrapper) {
        super(block, parentNodeWrapper);
    }
    
    public FinallyStatementWrapper(TryCatchStatementWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }
    
    public FinallyStatementWrapper() {
        this(null);
    }

    @Override
    public TryCatchStatementWrapper getParent() {
        return (TryCatchStatementWrapper) super.getParent();
    }

    @Override
    public FinallyStatementWrapper clone() {
        return new FinallyStatementWrapper(this, getParent());
    }

}
