package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;

public class AstSwitchDefaultStatementTreeTableNode extends AstCompositeStatementTreeTableNode {
    public AstSwitchDefaultStatementTreeTableNode(BlockStatementWrapper blockStatement, AstTreeTableNode parentNode) {
        super(blockStatement, parentNode, StringConstants.TREE_DEFAULT_STATEMENT);
        reloadChildren();
    }
}
