package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.DoWhileStatementWrapper;

public class AstDoWhileStatementTreeTableNode extends AstCompositeEditableInputStatementTreeTableNode {
    public AstDoWhileStatementTreeTableNode(DoWhileStatementWrapper doWhileStatement, AstTreeTableNode parentNode) {
        super(doWhileStatement, parentNode, ImageConstants.IMG_16_LOOP, StringConstants.TREE_DO_WHILE_STATEMENT);
    }
}
