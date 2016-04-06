package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CatchStatementWrapper;

public class AstCatchStatementTreeTableNode extends AstCompositeEditableInputStatementTreeTableNode {
    public AstCatchStatementTreeTableNode(CatchStatementWrapper statement, AstTreeTableNode parentNode) {
        super(statement, parentNode, StringConstants.TREE_CATCH_STATEMENT);
    }
    
    @Override
    public CatchStatementWrapper getASTObject() {
        return (CatchStatementWrapper) statement;
    }
}
