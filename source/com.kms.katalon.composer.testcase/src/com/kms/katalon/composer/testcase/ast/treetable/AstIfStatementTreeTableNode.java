package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;

public class AstIfStatementTreeTableNode extends AstStatementTreeTableNode {
	private IfStatement ifStatement;

	public AstIfStatementTreeTableNode(IfStatement statement, AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) {
		super(statement, parentNode, parentObject, scriptClass);
		ifStatement = statement;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_IF_STATEMENT;
	}

	@Override
	public boolean hasChildren() {
		return ifStatement.getIfBlock() != null;
	}

	@Override
	public List<AstTreeTableNode> getChildren() throws Exception {
		return AstTreeTableUtil.getChildren(ifStatement.getIfBlock(), this, ifStatement.getIfBlock(), scriptClass);
	}

	@Override
	public void addChildObject(ASTNode astObject, int index) {
		AstTreeTableUtil.addChild(ifStatement.getIfBlock(), astObject, index);
	}

	@Override
	public void removeChildObject(ASTNode astObject) {
		AstTreeTableUtil.removeChild(ifStatement.getIfBlock(), astObject);
	}

	@Override
	public int getChildObjectIndex(ASTNode astObject) {
		return AstTreeTableUtil.getIndex(ifStatement.getIfBlock(), astObject);
	}

	@Override
	public boolean isInputEditable() {
		return ifStatement.getBooleanExpression() != null;
	}

	@Override
	public String getInputText() {
		if (ifStatement.getBooleanExpression() != null) {
			return AstTreeTableTextValueUtil.getInstance().getTextValue(ifStatement.getBooleanExpression());
		}
		return "";
	}

	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_IF;
	}
}
