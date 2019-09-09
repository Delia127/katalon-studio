package com.kms.katalon.integration.analytics.entity;

import java.util.List;

public class AnalyticsLicenseKeyPage {
	
	private List<AnalyticsLicenseKey> content;
	
	public void setContent(List<AnalyticsLicenseKey> content) {
		this.content = content;
	}
	
	public List<AnalyticsLicenseKey> getContent() {
		return content;
	}
}
