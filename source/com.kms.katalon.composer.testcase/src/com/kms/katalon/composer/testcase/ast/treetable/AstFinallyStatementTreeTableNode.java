package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;

public class AstFinallyStatementTreeTableNode extends AstCompositeStatementTreeTableNode {
	public AstFinallyStatementTreeTableNode(BlockStatementWrapper statement, AstTreeTableNode parentNode) {
		super(statement, parentNode, StringConstants.TREE_FINALLY_STATEMENT);
	}
}
