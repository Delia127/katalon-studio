package com.kms.katalon.composer.testcase.ast.treetable;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.BlockStatement;

public class AstScriptMainBlockStatmentTreeTableNode extends AstStatementTreeTableNode {
	
	public AstScriptMainBlockStatmentTreeTableNode(BlockStatement blockStatement, AstTreeTableNode parentNode,
			ASTNode parentObject, ClassNode scriptClass) {
		super(blockStatement, parentNode, parentObject, scriptClass);
	}
}
