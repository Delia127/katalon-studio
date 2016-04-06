package com.kms.katalon.composer.testcase.groovy.ast.statements;

import org.codehaus.groovy.ast.stmt.ContinueStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class ContinueStatementWrapper extends StatementWrapper {
    private String label;
    
    public ContinueStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }
    
    public ContinueStatementWrapper(String label, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.label = label;
    }
    
    public ContinueStatementWrapper(ContinueStatement continueStatement, ASTNodeWrapper parentNodeWrapper) {
        super(continueStatement, parentNodeWrapper);
        this.label = continueStatement.getLabel();
    }
    
    public ContinueStatementWrapper(ContinueStatementWrapper continueStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(continueStatementWrapper, parentNodeWrapper);
        this.label = continueStatementWrapper.getLabel();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getText() {
        return "continue";
    }

    @Override
    public ContinueStatementWrapper clone() {
        return new ContinueStatementWrapper(this, getParent());
    }

}
