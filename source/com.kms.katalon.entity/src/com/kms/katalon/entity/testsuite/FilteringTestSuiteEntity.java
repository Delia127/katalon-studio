package com.kms.katalon.entity.testsuite;

import com.kms.katalon.entity.util.Util;

public class FilteringTestSuiteEntity extends TestSuiteEntity {

    private static final long serialVersionUID = 5500699092447234799L;

    private String filteringText;

    private String filteringPlugin;

    private String filteringExtension;

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

    @Override
    public boolean equals(Object that) {
        if (!super.equals(that)) {
            return false;
        }
        return getFilteringText().equals(((FilteringTestSuiteEntity) that).getFilteringText());
    }

    public String getFilteringPlugin() {
        return filteringPlugin;
    }

    public void setFilteringPlugin(String filteringPlugin) {
        this.filteringPlugin = filteringPlugin;
    }

    public String getFilteringExtension() {
        return filteringExtension;
    }

    public void setFilteringExtension(String filteringExtension) {
        this.filteringExtension = filteringExtension;
    }
}
