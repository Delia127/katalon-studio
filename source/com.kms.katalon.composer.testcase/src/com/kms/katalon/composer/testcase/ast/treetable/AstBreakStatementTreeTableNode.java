package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BreakStatementWrapper;

public class AstBreakStatementTreeTableNode extends AstStatementTreeTableNode {
    public AstBreakStatementTreeTableNode(BreakStatementWrapper breakStatement, AstTreeTableNode parentNode) {
        super(breakStatement, parentNode, StringConstants.TREE_BREAK_STATEMENT);
    }

}
