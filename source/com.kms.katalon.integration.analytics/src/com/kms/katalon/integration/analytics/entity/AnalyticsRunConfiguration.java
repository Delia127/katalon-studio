package com.kms.katalon.integration.analytics.entity;

public class AnalyticsRunConfiguration {

    private Long id;

    private String name;

    private Long testProjectId;

    private Long testSuiteCollectionId;

    private String configType;

    private String cloudType;

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

    public Long getTestProjectId() {
        return testProjectId;
    }

    public void setTestProjectId(Long testProjectId) {
        this.testProjectId = testProjectId;
    }

    public Long getTestSuiteCollectionId() {
        return testSuiteCollectionId;
    }

    public void setTestSuiteCollectionId(Long testSuiteCollectionId) {
        this.testSuiteCollectionId = testSuiteCollectionId;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getCloudType() {
        return cloudType;
    }

    public void setCloudType(String cloudType) {
        this.cloudType = cloudType;
    }

}
