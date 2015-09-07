package com.kms.katalon.composer.testcase.treetable;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;

import com.kms.katalon.composer.testcase.constants.StringConstants;

public class AstSwitchDefaultStatementTreeTableNode extends AstStatementTreeTableNode {

	public AstSwitchDefaultStatementTreeTableNode(Statement statement, AstTreeTableNode parentNode,
			SwitchStatement parentSwitchStatement, ClassNode scriptClass) {
		super(statement, parentNode, parentSwitchStatement, scriptClass);
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_DEFAULT_STATEMENT;
	}
}
