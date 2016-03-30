package com.kms.katalon.composer.report.provider;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

public class ReportTestStepTreeViewer extends TreeViewer {

    private String searchedString;

    public ReportTestStepTreeViewer(Composite parent, int style) {
        super(parent, style);
    }

    public String getSearchedString() {
        if (searchedString == null) {
            searchedString = "";
        }
        return searchedString;
    }

    public void setSearchedString(String searchedString) {
        this.searchedString = searchedString;
    }
}
