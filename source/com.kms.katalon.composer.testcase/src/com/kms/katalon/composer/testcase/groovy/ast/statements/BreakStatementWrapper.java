package com.kms.katalon.composer.testcase.groovy.ast.statements;

import org.codehaus.groovy.ast.stmt.BreakStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class BreakStatementWrapper extends StatementWrapper {
    private String label;
    
    public BreakStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }
    
    public BreakStatementWrapper(String label, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.label = label;
    }
    
    public BreakStatementWrapper(BreakStatement breakStatement, ASTNodeWrapper parentNodeWrapper) {
        super(breakStatement, parentNodeWrapper);
        this.label = breakStatement.getLabel();
    }
    
    public BreakStatementWrapper(BreakStatementWrapper breakStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(breakStatementWrapper, parentNodeWrapper);
        this.label = breakStatementWrapper.getLabel();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getText() {
        return "break";
    }

    @Override
    public BreakStatementWrapper clone() {
        return new BreakStatementWrapper(this, getParent());
    }

}
