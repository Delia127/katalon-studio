package com.kms.katalon.composer.testcase.groovy.ast.statements;

import org.codehaus.groovy.ast.stmt.BlockStatement;

public class ElseStatementWrapper extends ComplexLastStatementWrapper {
    public ElseStatementWrapper(BlockStatement block, IfStatementWrapper parentNodeWrapper) {
        super(block, parentNodeWrapper);
    }

    public ElseStatementWrapper(ElseStatementWrapper elseStatementWrapper, IfStatementWrapper parentNodeWrapper) {
        super(elseStatementWrapper, parentNodeWrapper);
    }
    
    public ElseStatementWrapper(IfStatementWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }
    
    public ElseStatementWrapper() {
        this(null);
    }

    @Override
    public IfStatementWrapper getParent() {
        return (IfStatementWrapper) super.getParent();
    }
    
    @Override
    public ElseStatementWrapper clone() {
        return new ElseStatementWrapper(this, getParent());
    }

}
