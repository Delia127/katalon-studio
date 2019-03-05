package com.kms.katalon.composer.testsuite.integration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kms.katalon.composer.testsuite.parts.integration.TestSuiteIntegrationPlatformBuilder;
import com.kms.katalon.composer.testsuite.parts.integration.TestSuiteIntegrationViewBuilder;
import com.kms.katalon.controller.ProjectController;

public class TestSuiteIntegrationFactory {
    private static TestSuiteIntegrationFactory _instance;

    private Map<String, TestSuiteIntegrationViewBuilder> integrationViewMap;

    private Map<String, TestSuiteIntegrationViewBuilder> getIntegrationViewMap() {
        return integrationViewMap;
    }

    private TestSuiteIntegrationPlatformBuilder platformBuilder;

    private TestSuiteIntegrationFactory() {
        integrationViewMap = new LinkedHashMap<String, TestSuiteIntegrationViewBuilder>();
    }

    public static TestSuiteIntegrationFactory getInstance() {
        if (_instance == null) {
            _instance = new TestSuiteIntegrationFactory();
        }
        return _instance;
    }

    public void addNewIntegrationView(String productName, TestSuiteIntegrationViewBuilder contributionView) {
        integrationViewMap.put(productName, contributionView);
    }

    public List<TestSuiteIntegrationViewBuilder> getSortedViewBuilders() {
        List<TestSuiteIntegrationViewBuilder> sortedBuilders = new ArrayList<>(getIntegrationViewMap().entrySet())
                .stream()
                .map(e -> e.getValue())
                .filter(e -> e.isEnabled(ProjectController.getInstance().getCurrentProject()))
                .collect(Collectors.toList());
        if (platformBuilder != null) {
            sortedBuilders.addAll(platformBuilder.getBuilders());
        }
        sortedBuilders.sort((left, right) -> left.getName().toLowerCase().compareTo(right.getName().toLowerCase()));
        return sortedBuilders;
    }
}
