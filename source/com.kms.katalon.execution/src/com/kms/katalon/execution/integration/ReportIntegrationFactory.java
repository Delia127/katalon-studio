package com.kms.katalon.execution.integration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
	
	public List<IntegrationCommand> getIntegrationCommands() {
	    List<IntegrationCommand> integrationCommands = new ArrayList<IntegrationCommand>();
	    for (ReportIntegrationContribution contribution : reportIntegrationMap.values()) {
	        if (contribution.getIntegrationCommand() != null) {
	            integrationCommands.add(contribution.getIntegrationCommand());
	        }
	    }
	    return integrationCommands;
    }
}
