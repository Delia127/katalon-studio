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
	public boolean isChildAssignble(ASTNodeWrapper astNode);
    public boolean addChild(ASTNodeWrapper childObject);
    public boolean addChild(ASTNodeWrapper childObject, int index);
    public boolean removeChild(ASTNodeWrapper childObject);
    public int indexOf(ASTNodeWrapper childObject);
	
	/**
	 * Check if the input node is the descendant of this node
	 */
	public boolean isDescendantNode(AstTreeTableNode otherNode);
}
