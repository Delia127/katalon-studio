package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;

public class AstElseStatementTreeTableNode extends AstCompositeStatementTreeTableNode {
    public AstElseStatementTreeTableNode(BlockStatementWrapper statement, AstTreeTableNode parentNode) {
        super(statement, parentNode, ImageConstants.IMG_16_ELSE, StringConstants.TREE_ELSE_STATEMENT);
    }
}
