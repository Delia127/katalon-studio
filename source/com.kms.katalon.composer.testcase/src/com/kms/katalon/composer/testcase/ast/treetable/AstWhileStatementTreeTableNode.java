package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;

public class AstWhileStatementTreeTableNode extends AstStatementTreeTableNode {
	private WhileStatement whileStatement;

	public AstWhileStatementTreeTableNode(WhileStatement statement, AstTreeTableNode parentNode, ASTNode parentObject,
			ClassNode scriptClass) {
		super(statement, parentNode, parentObject, scriptClass);
		whileStatement = statement;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_WHILE_STATEMENT;
	}

	@Override
	public boolean hasChildren() {
		return whileStatement.getLoopBlock() != null;
	}

	@Override
	public List<AstTreeTableNode> getChildren() throws Exception {
		return AstTreeTableUtil.getChildren(whileStatement.getLoopBlock(), this, whileStatement.getLoopBlock(),
				scriptClass);
	}

	@Override
	public void addChildObject(ASTNode astObject, int index) {
		AstTreeTableUtil.addChild(whileStatement.getLoopBlock(), astObject, index);
	}

	@Override
	public void removeChildObject(ASTNode astObject) {
		AstTreeTableUtil.removeChild(whileStatement.getLoopBlock(), astObject);
	}

	@Override
	public int getChildObjectIndex(ASTNode astObject) {
		return AstTreeTableUtil.getIndex(whileStatement.getLoopBlock(), astObject);
	}

	@Override
	public String getInputText() {
		if (whileStatement.getBooleanExpression() != null) {
			return AstTreeTableTextValueUtil.getTextValue(whileStatement.getBooleanExpression());
		}
		return "";
	}

	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_LOOP;
	}
}
