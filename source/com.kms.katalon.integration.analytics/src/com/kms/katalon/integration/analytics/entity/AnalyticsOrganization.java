package com.kms.katalon.integration.analytics.entity;

public class AnalyticsOrganization {

    private Long id;

    private String name;

    private String role;

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

    public AnalyticsOrganizationRole getRole() {
        return AnalyticsOrganizationRole.valueOf(role);
    }

    public void setRole(String role) {
        this.role = role;
    }
}
