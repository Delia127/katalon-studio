package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ContinueStatementWrapper;

public class AstContinueStatementTreeTableNode extends AstStatementTreeTableNode {
    public AstContinueStatementTreeTableNode(ContinueStatementWrapper continueStatement, AstTreeTableNode parentNode) {
        super(continueStatement, parentNode, StringConstants.TREE_CONTINUE_STATEMENT);
    }

}
