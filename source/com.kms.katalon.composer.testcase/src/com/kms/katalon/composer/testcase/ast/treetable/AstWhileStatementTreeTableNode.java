package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.WhileStatementWrapper;

public class AstWhileStatementTreeTableNode extends AstCompositeEditableInputStatementTreeTableNode {
    public AstWhileStatementTreeTableNode(WhileStatementWrapper whileStatement, AstTreeTableNode parentNode) {
        super(whileStatement, parentNode, ImageConstants.IMG_16_LOOP, StringConstants.TREE_WHILE_STATEMENT);
    }
}
