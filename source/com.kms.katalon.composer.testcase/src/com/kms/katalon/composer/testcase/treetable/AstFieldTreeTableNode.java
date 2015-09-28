package com.kms.katalon.composer.testcase.treetable;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;

public class AstFieldTreeTableNode extends AstAbstractTreeTableNode {
	private FieldNode fieldNode;
	private AstTreeTableNode parentNode;
	private ClassNode classNode;
	
	public AstFieldTreeTableNode(FieldNode fieldNode, AstTreeTableNode parentNode, ClassNode parentClassNode) {
		this.fieldNode = fieldNode;
		this.parentNode = parentNode;
		this.classNode = parentClassNode;
	}

	@Override
	public ASTNode getASTObject() {
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
	public ASTNode getParentASTObject() {
		return classNode;
	}

	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_FUNCTION;
	}

	@Override
	public AstTreeTableNode clone() {
		// TODO Auto-generated method stub
		return null;
	}

}
