package com.kms.katalon.composer.report.provider;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

public class ReportTestCaseTableViewer extends TableViewer {

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
