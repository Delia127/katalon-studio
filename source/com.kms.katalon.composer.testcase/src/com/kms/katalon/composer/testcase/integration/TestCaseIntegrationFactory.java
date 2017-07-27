package com.kms.katalon.composer.testcase.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationViewBuilder;

public class TestCaseIntegrationFactory {
    private static TestCaseIntegrationFactory _instance;

    private Map<String, TestCaseIntegrationViewBuilder> integrationViewMap;

    public Map<String, TestCaseIntegrationViewBuilder> getIntegrationViewMap() {
        return integrationViewMap;
    }

    private TestCaseIntegrationFactory() {
        integrationViewMap = new HashMap<String, TestCaseIntegrationViewBuilder>();
    }

    public static TestCaseIntegrationFactory getInstance() {
        if (_instance == null) {
            _instance = new TestCaseIntegrationFactory();
        }
        return _instance;
    }

    public void addNewIntegrationView(String productName, TestCaseIntegrationViewBuilder contributionView) {
        integrationViewMap.put(productName, contributionView);
    }

    public List<Entry<String, TestCaseIntegrationViewBuilder>> getSortedViewBuilders() {
        List<Entry<String, TestCaseIntegrationViewBuilder>> sortedBuilders = new ArrayList<>(
                getIntegrationViewMap().entrySet());
        sortedBuilders.sort((left, right) -> left.getValue().preferredOrder() - right.getValue().preferredOrder());
        return sortedBuilders;
    }
}
