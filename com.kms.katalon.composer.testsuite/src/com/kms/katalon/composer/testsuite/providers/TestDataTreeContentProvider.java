package com.kms.katalon.composer.testsuite.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.testsuite.tree.TestDataLinkTreeNode;
import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataTreeContentProvider implements ITreeContentProvider {
	
	private List<TestCaseTestDataLink> dataLinks;
	private List<TestDataLinkTreeNode> dataLinkTreeNodes;
	
	public TestDataTreeContentProvider() {
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		if (newInput != null && newInput instanceof List) {
			dataLinks = (List<TestCaseTestDataLink>) newInput;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List<?>) {
			List<TestCaseTestDataLink> testDataLinks = (List<TestCaseTestDataLink>) inputElement;
			dataLinkTreeNodes = new ArrayList<TestDataLinkTreeNode>();
			for (TestCaseTestDataLink testDataLink : testDataLinks) {
				int index = testDataLinks.indexOf(testDataLink);
				dataLinkTreeNodes.add(new TestDataLinkTreeNode(Integer.toString(index + 1), testDataLink));
			}
		} else {
			dataLinkTreeNodes = Collections.emptyList();
		}
		
		return dataLinkTreeNodes.toArray(new TestDataLinkTreeNode[dataLinkTreeNodes.size()]);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement == null || !(parentElement instanceof TestDataLinkTreeNode))
			return null;
		TestDataLinkTreeNode linkNode = (TestDataLinkTreeNode) parentElement;
		return linkNode.getChildrenNode().toArray(new TestDataLinkTreeNode[linkNode.getChildrenNode().size()]);
	}

	@Override
	public Object getParent(Object element) {
		if (element == null || !(element instanceof TestDataLinkTreeNode))
			return false;
		TestDataLinkTreeNode link = (TestDataLinkTreeNode) element;
		return link.getParentNode();
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element == null || !(element instanceof TestDataLinkTreeNode))
			return false;
		TestDataLinkTreeNode link = (TestDataLinkTreeNode) element;
		return link.getChildrenNode().size() > 0;
	}

	public List<TestCaseTestDataLink> getDataLinks() {
		if (dataLinks == null) {
			dataLinks = new ArrayList<TestCaseTestDataLink>();
		}
		return dataLinks;
	}
	
	public TreePath getTreePath(TestDataLinkTreeNode treeNode) {
		if (treeNode.getParentNode() == null) {
			return new TreePath(new Object[0]).createChildPath(treeNode);
		} else {
			return getTreePath(treeNode.getParentNode()).createChildPath(treeNode);
		}
	}
	
	public TestDataLinkTreeNode getTreeNode(TestCaseTestDataLink testDataLink) {
		if (testDataLink.getTestDataId() == null) return null;
		
		if (dataLinkTreeNodes == null) return null;
			 
		for (TestDataLinkTreeNode rootNode : dataLinkTreeNodes) {
			TestDataLinkTreeNode foundTreeNode = getTreeNode(testDataLink.getId(), rootNode);
			if (foundTreeNode != null) return foundTreeNode;
		}
		return null;
	}
	
	private TestDataLinkTreeNode getTreeNode(String testDataLinkId, TestDataLinkTreeNode treeNode) {
		if (treeNode.getTestDataLink() == null) return null;
		if (treeNode.getTestDataLink().getId().equals(testDataLinkId)) {
			return treeNode;
		}
		
		for (TestDataLinkTreeNode childTreeNode : treeNode.getChildrenNode()) {
			TestDataLinkTreeNode foundTreeNode = getTreeNode(testDataLinkId, childTreeNode);
			if (foundTreeNode != null) return foundTreeNode;
		}
		return null;
	}
}
