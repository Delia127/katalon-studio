package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ThrowStatementWrapper;

public class AstThrowStatementTreeTableNode extends AstEditableStatementTreeTableNode {
    public AstThrowStatementTreeTableNode(ThrowStatementWrapper throwStatement, AstTreeTableNode parentNode) {
        super(throwStatement, parentNode, StringConstants.TREE_THROW_STATEMENT);
    }

}
