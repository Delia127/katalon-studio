package com.kms.katalon.composer.testcase.treetable;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;

import com.kms.katalon.composer.testcase.constants.StringConstants;

public class AstFinallyStatementTreeTableNode extends AstStatementTreeTableNode {

	public AstFinallyStatementTreeTableNode(Statement statement, AstTreeTableNode parentNode,
			TryCatchStatement parentTryCatchStatement, ClassNode scriptClass) {
		super(statement, parentNode, parentTryCatchStatement, scriptClass);
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_FINALLY_STATEMENT;
	}
}
