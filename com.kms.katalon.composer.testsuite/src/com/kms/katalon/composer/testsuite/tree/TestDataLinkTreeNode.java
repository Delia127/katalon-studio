package com.kms.katalon.composer.testsuite.tree;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataLinkTreeNode {
	private TestCaseTestDataLink testDataLink;	
	private TestDataLinkTreeNode parentNode;
	private List<TestDataLinkTreeNode> childrenNode;
	private String id;
	
	public TestDataLinkTreeNode(String id, TestCaseTestDataLink testDataLink) {
		setId(id);
		setTestDataLink(testDataLink);
		
		childrenNode = new ArrayList<TestDataLinkTreeNode>();
		
		for (TestCaseTestDataLink childLink : testDataLink.getChildrenLink()) {
			int index = testDataLink.getChildrenLink().indexOf(childLink);
			TestDataLinkTreeNode childNode = new TestDataLinkTreeNode(id + "." + Integer.toString(index + 1), childLink);

			childrenNode.add(childNode);
			childNode.setParentNode(this);
		}
	}
	
	public TestCaseTestDataLink getTestDataLink() {
		return testDataLink;
	}
	
	public void setTestDataLink(TestCaseTestDataLink testDataLink) {
		this.testDataLink = testDataLink;
	}
	
	public TestDataLinkTreeNode getParentNode() {
		return parentNode;
	}
	
	public void setParentNode(TestDataLinkTreeNode parentNode) {
		this.parentNode = parentNode;
	}

	public List<TestDataLinkTreeNode> getChildrenNode() {
		return childrenNode;
	}

	public void setChildrenNode(List<TestDataLinkTreeNode> childrenNode) {
		this.childrenNode = childrenNode;
	}
	
	public void addChildNode(TestDataLinkTreeNode childNode, int index) {
		testDataLink.getChildrenLink().add(index, childNode.getTestDataLink());		
		childNode.setId(getId() + "." + Integer.toString(index + 1));
		childrenNode.add(index, childNode);
		childNode.setParentNode(this);
		
		resetChildrenId();
	}
	
	public void removeChildNode(TestDataLinkTreeNode childNode) {
		testDataLink.getChildrenLink().remove(childNode.getTestDataLink());		
		childrenNode.remove(childNode);		
		childNode.setParentNode(null);
		resetChildrenId();
	}
	
	public void resetChildrenId() {
		for (TestDataLinkTreeNode childNode : getChildrenNode()) {
			int index = getChildrenNode().indexOf(childNode);
			childNode.setId(getId() + "." + Integer.toString(index + 1));
			
			childNode.resetChildrenId();
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
