package com.kms.katalon.composer.testcase.treetable;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;

public class AstCommentStatementTreeTableNode extends AstStatementTreeTableNode {
	private ExpressionStatement statement;

	public AstCommentStatementTreeTableNode(ExpressionStatement statement, AstTreeTableNode parentNode,
			ASTNode parentObject, ClassNode scriptClass) {
		super(statement, parentNode, parentObject, scriptClass);
		this.statement = statement;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_COMMENT;
	}
	
	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_COMMENT;
	}
	
	@Override
	public String getInputText() {
		if (statement.getExpression() != null) {
			return statement.getExpression().getText();
		}
		return "";
	}
}
