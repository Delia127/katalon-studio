package com.kms.katalon.composer.testcase.ast.treetable;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;

public class AstAssertStatementTreeTableNode extends AstStatementTreeTableNode {
	private AssertStatement assertStatement;

	public AstAssertStatementTreeTableNode(AssertStatement assertStatement, AstTreeTableNode parentNode,
			ASTNode parentObject, ClassNode scriptClass) {
		super(assertStatement, parentNode, parentObject, scriptClass);
		this.assertStatement = assertStatement;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_ASSERT_STATEMENT;
	}

	@Override
	public String getInputText() {
		if (assertStatement.getBooleanExpression() != null) {
			return AstTreeTableTextValueUtil.getTextValue(assertStatement.getBooleanExpression());
		}
		return "";
	}

	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_ASSERT;
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}
}
