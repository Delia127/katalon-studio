package com.kms.katalon.composer.testsuite.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataTreeViewerFilter extends ViewerFilter {

    private String searchString;

    public void setSearchText(String s) {
        this.searchString = ".*" + s.toLowerCase() + ".*";
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element == null || !(element instanceof TestCaseTestDataLink)) { return false; }
        if (searchString == null || searchString.length() == 0) {
            return true;
        }

        TestCaseTestDataLink treeNode = (TestCaseTestDataLink) element;
        if (treeNode.getTestDataId().toLowerCase().matches(searchString)) {
            return true;
        }
        return false;
    }

}
