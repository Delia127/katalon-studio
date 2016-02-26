package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;

public class AstDefaultStatementTreeTableNode extends AstCompositeStatementTreeTableNode {
	public AstDefaultStatementTreeTableNode(BlockStatementWrapper statement, AstTreeTableNode parentNode) {
		super(statement, parentNode, StringConstants.TREE_DEFAULT_STATEMENT);
	}
}
