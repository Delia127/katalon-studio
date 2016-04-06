package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.FieldNodeWrapper;

public class AstFieldTreeTableNode extends AstAbstractTreeTableNode {
	private FieldNodeWrapper fieldNode;
	
	public AstFieldTreeTableNode(FieldNodeWrapper fieldNode, AstTreeTableNode parentNode) {
	    super(parentNode);
		this.fieldNode = fieldNode;
		this.parentNode = parentNode;
	}

	@Override
	public FieldNodeWrapper getASTObject() {
		return fieldNode;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_FIELD;
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
