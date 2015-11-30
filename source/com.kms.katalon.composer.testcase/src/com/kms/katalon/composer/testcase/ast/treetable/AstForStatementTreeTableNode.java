package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstForStatementTreeTableNode extends AstStatementTreeTableNode {
	private ForStatement forStatement;

	public AstForStatementTreeTableNode(ForStatement statement, AstTreeTableNode parentNode, ASTNode parentObject,
			ClassNode scriptClass) {
		super(statement, parentNode, parentObject, scriptClass);
		forStatement = statement;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_FOR_STATEMENT;
	}

	@Override
	public boolean hasChildren() {
		return forStatement.getLoopBlock() != null;
	}

	@Override
	public List<AstTreeTableNode> getChildren() throws Exception {
		return AstTreeTableUtil.getChildren(forStatement.getLoopBlock(), this, forStatement.getLoopBlock(), scriptClass);
	}

	@Override
	public void addChildObject(ASTNode astObject, int index) {
		AstTreeTableUtil.addChild(forStatement.getLoopBlock(), astObject, index);
	}

	@Override
	public void removeChildObject(ASTNode astObject) {
		AstTreeTableUtil.removeChild(forStatement.getLoopBlock(), astObject);
	}

	@Override
	public int getChildObjectIndex(ASTNode astObject) {
		return AstTreeTableUtil.getIndex(forStatement.getLoopBlock(), astObject);
	}

	@Override
	public boolean setInput(Object input) {
		if (input instanceof ForStatement) {
			ForStatement newForStatement = (ForStatement) input;
			if (!AstTreeTableValueUtil.compareAstNode(forStatement, newForStatement) && getParent() != null) {
				AstTreeTableNode parentNode = getParent();
				int index = parentNode.getChildObjectIndex(forStatement);
				parentNode.removeChildObject(forStatement);
				parentNode.addChildObject(newForStatement, index);
				return true;
			}
		}
		return false;
	}

	@Override
	public String getInputText() {
		return AstTreeTableTextValueUtil.getInputTextValue(forStatement);
	}

	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_LOOP;
	}

}
