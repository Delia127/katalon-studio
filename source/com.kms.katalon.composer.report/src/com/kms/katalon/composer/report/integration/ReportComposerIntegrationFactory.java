package com.kms.katalon.composer.report.integration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kms.katalon.composer.report.parts.integration.ReportTestCaseIntegrationViewBuilder;

public class ReportComposerIntegrationFactory {
    private static ReportComposerIntegrationFactory _instance;

    private Map<String, ReportTestCaseIntegrationViewBuilder> testCaseIntegrationViewMap;

    public Map<String, ReportTestCaseIntegrationViewBuilder> getIntegrationViewMap() {
        return testCaseIntegrationViewMap;
    }

    private ReportComposerIntegrationFactory() {
        testCaseIntegrationViewMap = new LinkedHashMap<String, ReportTestCaseIntegrationViewBuilder>();
    }

    public static ReportComposerIntegrationFactory getInstance() {
        if (_instance == null) {
            _instance = new ReportComposerIntegrationFactory();
        }
        return _instance;
    }

    public void addNewTestCaseIntegrationView(String productName,
            ReportTestCaseIntegrationViewBuilder contributionView) {
        testCaseIntegrationViewMap.put(productName, contributionView);
    }

    public List<ReportTestCaseIntegrationViewBuilder> getSortedBuilder() {
        List<ReportTestCaseIntegrationViewBuilder> builders = new ArrayList<>();
        for (Entry<String, ReportTestCaseIntegrationViewBuilder> builderEntry : getInstance().getIntegrationViewMap()
                .entrySet()) {
            builders.add(builderEntry.getValue());
        }
        builders.sort((left, right) -> left.getPreferredOrder() - right.getPreferredOrder());
        return builders;
    }

    public int getPreferredOrder(String productName) {
        return getIntegrationViewMap().get(productName).getPreferredOrder();
    }
}
