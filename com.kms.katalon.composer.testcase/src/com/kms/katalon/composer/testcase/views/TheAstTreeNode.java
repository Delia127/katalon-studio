package com.kms.katalon.composer.testcase.views;

import java.util.ArrayList;

import org.codehaus.groovy.ast.ASTNode;

public class TheAstTreeNode {

	private String name;
	
	public TheAstTreeNode parent;

	public ASTNode ast;
	
	public ArrayList<TheAstTreeNode> children = new ArrayList<TheAstTreeNode>();

	public TheAstTreeNode(TheAstTreeNode parent) {
		this.parent = parent;
	}

	public TheAstTreeNode(TheAstTreeNode parent, String name) {
		this.parent = parent;
		this.name = name;
	}
	
	public String toString() {
		if(this.name != null){
			return name;
		}
		else if(ast != null){
			return ast.getClass().getCanonicalName();
		}
		return "Unknown";
	}
	
	public void add(TheAstTreeNode childNode){
		children.add(childNode);
		if(childNode.parent == null){
			childNode.parent = this;
		}
	}
	
}
