package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

import java.util.List;

public interface AstTreeTableNode {
	public ASTNodeWrapper getASTObject();
	public String getItemText();
	public String getItemTooltipText();
	public Image getIcon();

	public boolean canHaveChildren();
	public AstTreeTableNode getParent();
	public boolean hasChildren();
	public List<AstTreeTableNode> getChildren();
	public void reloadChildren();

}
