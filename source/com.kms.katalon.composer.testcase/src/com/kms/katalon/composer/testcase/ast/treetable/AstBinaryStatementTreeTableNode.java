package com.kms.katalon.composer.testcase.ast.treetable;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;

public class AstBinaryStatementTreeTableNode extends AstStatementTreeTableNode {
	private ExpressionStatement expressionStatement;

	public AstBinaryStatementTreeTableNode(ExpressionStatement expressionStatement, AstTreeTableNode parentNode,
			ASTNode parentObject, ClassNode scriptClass) {
		super(expressionStatement, parentNode, parentObject, scriptClass);
		this.expressionStatement = expressionStatement;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_BINARY_STATEMENT;
	}
	
	@Override
	public String getInputText() {
		return AstTreeTableTextValueUtil.getTextValue(expressionStatement.getExpression());
	}

	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_BINARY;
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}
}
