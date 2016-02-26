package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.AssertStatementWrapper;

public class AstAssertStatementTreeTableNode extends AstEditableStatementTreeTableNode {
    public AstAssertStatementTreeTableNode(AssertStatementWrapper assertStatement, AstTreeTableNode parentNode) {
        super(assertStatement, parentNode, ImageConstants.IMG_16_ASSERT, StringConstants.TREE_ASSERT_STATEMENT);
    }

}
