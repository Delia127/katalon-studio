package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ReturnStatementWrapper;

public class AstReturnStatementWrapper extends AstStatementTreeTableNode {
    public AstReturnStatementWrapper(ReturnStatementWrapper returnStatement, AstTreeTableNode parentNode) {
        super(returnStatement, parentNode, StringConstants.TREE_RETURN_STATEMENT);
    }

}
