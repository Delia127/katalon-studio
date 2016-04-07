package com.kms.katalon.composer.testcase.groovy.ast.statements;

import org.codehaus.groovy.ast.stmt.BlockStatement;

public class DefaultStatementWrapper extends ComplexLastStatementWrapper {
    public DefaultStatementWrapper(DefaultStatementWrapper defaultStatementWrapper, SwitchStatementWrapper parentNodeWrapper) {
        super(defaultStatementWrapper, parentNodeWrapper);
    }

    public DefaultStatementWrapper(BlockStatement block, SwitchStatementWrapper parentNodeWrapper) {
        super(block, parentNodeWrapper);
    }
    
    public DefaultStatementWrapper(SwitchStatementWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        block.addStatement(new BreakStatementWrapper(block));
    }
    
    public DefaultStatementWrapper() {
        this(null);
    }
    
    @Override
    public SwitchStatementWrapper getParent() {
        return (SwitchStatementWrapper) super.getParent();
    }
    
    @Override
    public DefaultStatementWrapper clone() {
        return new DefaultStatementWrapper(this, getParent());
    }

}
