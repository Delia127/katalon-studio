package com.kms.katalon.composer.report.provider;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.control.CTableViewer;

public class ReportTestCaseTableViewer extends CTableViewer {

    private String searchedString;

    public ReportTestCaseTableViewer(Composite parent, int style) {
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
