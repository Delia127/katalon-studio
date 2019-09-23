package com.kms.katalon.execution.integration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;

public class ReportIntegrationFactory {
    
    private static final String ANALYTICS_NAME = "Katalon TestOps";
    
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
    
    public List<ConsoleOptionContributor> getConsoleOptionContributorList() {
        return new ArrayList<ConsoleOptionContributor>(reportIntegrationMap.values());
    }
    
    public ReportIntegrationContribution getAnalyticsProvider () {
        return reportIntegrationMap.get(ANALYTICS_NAME);
    }
}
