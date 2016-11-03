package com.kms.katalon.composer.report.provider;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.control.CTreeViewer;

public class ReportTestStepTreeViewer extends CTreeViewer {

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
