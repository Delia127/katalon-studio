package com.kms.katalon.integration.analytics.entity;

public class AnalyticsTestSuiteResource {
	
	private AnalyticsProjectTestSuite testSuite;
	private String[] profiles;
	public AnalyticsProjectTestSuite getTestSuite() {
		return testSuite;
	}
	public void setTestSuite(AnalyticsProjectTestSuite testSuite) {
		this.testSuite = testSuite;
	}
	public String[] getProfiles() {
		return profiles;
	}
	public void setProfiles(String[] profiles) {
		this.profiles = profiles;
	}
	public AnalyticsTestSuiteResource(AnalyticsProjectTestSuite testSuite, String[] profiles) {
		super();
		this.testSuite = testSuite;
		this.profiles = profiles;
	}
	

}
