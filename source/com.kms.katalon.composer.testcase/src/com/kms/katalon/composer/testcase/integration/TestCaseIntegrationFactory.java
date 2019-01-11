package com.kms.katalon.composer.testcase.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationPlatformBuilder;
import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationViewBuilder;
import com.kms.katalon.controller.ProjectController;

public class TestCaseIntegrationFactory {
    private static TestCaseIntegrationFactory _instance;

    private Map<String, TestCaseIntegrationViewBuilder> integrationViewMap;

    private TestCaseIntegrationPlatformBuilder platformBuilder;

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

    public void setPlatformBuilder(TestCaseIntegrationPlatformBuilder platformBuilder) {
        this.platformBuilder = platformBuilder;
    }

    public List<TestCaseIntegrationViewBuilder> getSortedViewBuilders() {
        List<TestCaseIntegrationViewBuilder> sortedBuilders = new ArrayList<>(getIntegrationViewMap().entrySet())
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
