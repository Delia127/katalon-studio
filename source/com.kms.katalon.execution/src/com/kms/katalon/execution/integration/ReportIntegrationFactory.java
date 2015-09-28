package com.kms.katalon.execution.integration;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportIntegrationFactory {
	private static ReportIntegrationFactory _instance;
	
	private Map<String, ReportIntegrationContribution> reportIntegrationMap;
	
	public Map<String, ReportIntegrationContribution> getIntegrationContributorMap() {
		return reportIntegrationMap;
	}

	private ReportIntegrationFactory() {
		reportIntegrationMap = new LinkedHashMap<String, ReportIntegrationContribution>();
	}
	
	public static ReportIntegrationFactory getInstance() {
		if (_instance == null) {
			_instance = new ReportIntegrationFactory();
		}
		return _instance;
	}
	
	public void addNewReportIntegration(String productName, ReportIntegrationContribution contributor) {
		reportIntegrationMap.put(productName, contributor);
	}
}
