package com.kms.katalon.execution.integration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kms.katalon.execution.entity.ConsoleOption;

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

    public List<ConsoleOption<?>> getIntegrationCommands() {
        List<ConsoleOption<?>> integrationCommands = new ArrayList<ConsoleOption<?>>();
        for (ReportIntegrationContribution contribution : reportIntegrationMap.values()) {
            if (contribution.getIntegrationCommands() == null) {
                continue;
            }
            integrationCommands.addAll(contribution.getIntegrationCommands());
        }
        return integrationCommands;
    }
}
