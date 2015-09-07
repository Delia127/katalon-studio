package com.kms.katalon.composer.testsuite.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kms.katalon.composer.testsuite.tree.TestDataLinkTreeNode;

public class TestDataTreeViewerFilter extends ViewerFilter {

	private String searchString;

	public void setSearchText(String s) {
		// ensure that the value can be used for matching
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		// TODO Auto-generated method stub
		if (element == null || !(element instanceof TestDataLinkTreeNode))
			return false;
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		TestDataLinkTreeNode treeNode = (TestDataLinkTreeNode) element;
		if (treeNode.getTestDataLink().getTestDataId().toLowerCase().matches(searchString)) {
			return true;
		}
		
		boolean isChildrenMatched = false;
		for (TestDataLinkTreeNode childNode : treeNode.getChildrenNode()) {
			isChildrenMatched |= select(viewer, element, childNode);
		}
		return isChildrenMatched;
	}

}
