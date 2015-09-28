package com.kms.katalon.composer.testcase.treetable;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;

public class AstClassTreeTableNode extends AstAbstractTreeTableNode {
	private ClassNode classNode;
	private AstTreeTableNode parentNode;

	public AstClassTreeTableNode(ClassNode classNode, AstTreeTableNode parentNode) {
		this.classNode = classNode;
		this.parentNode = parentNode;
	}

	@Override
	public ASTNode getASTObject() {
		return classNode;
	}

	@Override
	public ASTNode getParentASTObject() {
		return null;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_CLASS;
	}

	@Override
	public boolean hasChildren() {
		if (classNode.getMethods().size() > 0 || classNode.getFields().size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public List<AstTreeTableNode> getChildren() throws Exception {
		return AstTreeTableUtil.getChildren(classNode, this);
	}

	@Override
	public AstTreeTableNode getParent() {
		return parentNode;
	}

	@Override
	public void addChildObject(ASTNode astObject, int index) {
		AstTreeTableUtil.addChild(classNode, astObject, index);
	}

	@Override
	public void removeChildObject(ASTNode astObject) {
		AstTreeTableUtil.removeChild(classNode, astObject);
	}

	@Override
	public int getChildObjectIndex(ASTNode astObject) {
		return AstTreeTableUtil.getIndex(classNode, astObject);
	}

	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_FUNCTION;
	}

	@Override
	public AstTreeTableNode clone() {
		return null;
	}

}
