package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.IfStatementWrapper;

public class AstIfStatementTreeTableNode extends AstCompositeEditableInputStatementTreeTableNode {
	public AstIfStatementTreeTableNode(IfStatementWrapper statement, AstTreeTableNode parentNode) {
		super(statement, parentNode, ImageConstants.IMG_16_IF, StringConstants.TREE_IF_STATEMENT);
	}
	
	@Override
	public IfStatementWrapper getASTObject() {
	    return (IfStatementWrapper) statement;
	}
}
