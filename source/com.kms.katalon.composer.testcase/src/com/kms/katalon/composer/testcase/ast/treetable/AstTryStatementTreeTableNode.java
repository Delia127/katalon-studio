package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.TryCatchStatementWrapper;

public class AstTryStatementTreeTableNode extends AstCompositeStatementTreeTableNode {
    public AstTryStatementTreeTableNode(TryCatchStatementWrapper statement, AstTreeTableNode parentNode) {
        super(statement, parentNode, StringConstants.TREE_TRY_STATEMENT);
    }
    
    @Override
    public TryCatchStatementWrapper getASTObject() {
        return (TryCatchStatementWrapper) statement;
    }
}
