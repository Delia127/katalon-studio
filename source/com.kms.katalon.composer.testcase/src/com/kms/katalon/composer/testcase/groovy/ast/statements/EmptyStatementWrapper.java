package com.kms.katalon.composer.testcase.groovy.ast.statements;

import org.codehaus.groovy.ast.stmt.EmptyStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class EmptyStatementWrapper extends StatementWrapper {
    public EmptyStatementWrapper(EmptyStatement statement, ASTNodeWrapper parentNodeWrapper) {
        super(statement, parentNodeWrapper);
    }
    
    public EmptyStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    @Override
    public String getText() {
        return "EMPTY";
    }

    @Override
    public EmptyStatementWrapper clone() {
        return new EmptyStatementWrapper(getParent());
    }
}
