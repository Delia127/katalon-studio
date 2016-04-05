package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;

public class AstClassTreeTableNode extends AstAbstractTreeTableNode {
	private ClassNodeWrapper classNode;

	public AstClassTreeTableNode(ClassNodeWrapper classNode, AstTreeTableNode parentNode) {
	    super(parentNode);
		this.classNode = classNode;
	}

	@Override
	public ClassNodeWrapper getASTObject() {
		return classNode;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_CLASS;
	}

	@Override
	public AstTreeTableNode getParent() {
		return parentNode;
	}
	@Override
	public Image getIcon() {
		return ImageConstants.IMG_16_FUNCTION;
	}
}
