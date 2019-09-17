package com.kms.katalon.integration.analytics.entity;

import java.util.List;

public class AnalyticsTestProject {
    private Long id;

    private String name;

    private String description;

    private List<AnalyticsTestSuiteCollection> testSuiteCollections;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AnalyticsTestSuiteCollection> getTestSuiteCollections() {
        return testSuiteCollections;
    }

    public void setTestSuiteCollections(List<AnalyticsTestSuiteCollection> testSuiteCollections) {
        this.testSuiteCollections = testSuiteCollections;
    }
    
    

}
