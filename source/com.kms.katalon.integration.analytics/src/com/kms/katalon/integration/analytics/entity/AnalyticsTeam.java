package com.kms.katalon.integration.analytics.entity;

public class AnalyticsTeam {

    private Long id;
    
    private String role;

    private String name;
    
    private AnalyticsOrganization organization;
    
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public AnalyticsOrganization getOrganization() {
		return organization;
	}
    
    public void setOrganization(AnalyticsOrganization organization) {
		this.organization = organization;
	}
}
