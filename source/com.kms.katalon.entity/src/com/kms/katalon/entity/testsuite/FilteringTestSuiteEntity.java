package com.kms.katalon.entity.testsuite;

import com.kms.katalon.entity.util.Util;

public class FilteringTestSuiteEntity extends TestSuiteEntity {

    private static final long serialVersionUID = 5500699092447234799L;

    private String filteringText;

    public String getFilteringText() {
        return filteringText;
    }

    public void setFilteringText(String filteringText) {
        this.filteringText = filteringText;
    }
    
    public FilteringTestSuiteEntity clone() {
        FilteringTestSuiteEntity newTestSuite = (FilteringTestSuiteEntity) super.clone();
        newTestSuite.setTestSuiteGuid(Util.generateGuid());
        return newTestSuite;
    }
}
