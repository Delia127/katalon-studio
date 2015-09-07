package com.kms.katalon.composer.testsuite.filters;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class DataColumnViewerFilter extends ViewerFilter {

    private String searchString = ".*"; 
    
    public void setSearchString(String text) {
        if (text.trim().equals(StringUtils.EMPTY)) {
            searchString = ".*";
        } else {
            searchString = ".*" + text.toLowerCase() + ".*";
        }
    }
    
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element != null && element instanceof String) {
            String columnName = ((String) element).toLowerCase();
            if (columnName.matches(searchString)) {
                return true;
            }
        }
        return false;
    }
}
