package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ForStatementWrapper;

public class AstForStatementTreeTableNode extends AstCompositeEditableInputStatementTreeTableNode {
    public AstForStatementTreeTableNode(ForStatementWrapper forStatement, AstTreeTableNode parentNode) {
        super(forStatement, parentNode, ImageConstants.IMG_16_LOOP, StringConstants.TREE_FOR_STATEMENT);
    }
}
