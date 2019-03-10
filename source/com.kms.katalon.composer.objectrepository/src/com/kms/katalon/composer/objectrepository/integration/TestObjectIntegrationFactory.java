package com.kms.katalon.composer.objectrepository.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kms.katalon.controller.ProjectController;

public class TestObjectIntegrationFactory {
	private static TestObjectIntegrationFactory _instance;

    private Map<String, TestObjectIntegrationViewBuilder> integrationViewMap;

    private TestObjectIntegrationPlatformBuilder platformBuilder;

    public Map<String, TestObjectIntegrationViewBuilder> getIntegrationViewMap() {
        return integrationViewMap;
    }

    private TestObjectIntegrationFactory() {
        integrationViewMap = new HashMap<String, TestObjectIntegrationViewBuilder>();
    }

    public static TestObjectIntegrationFactory getInstance() {
        if (_instance == null) {
            _instance = new TestObjectIntegrationFactory();
        }
        return _instance;
    }

    public void addNewIntegrationView(String productName, TestObjectIntegrationViewBuilder contributionView) {
        integrationViewMap.put(productName, contributionView);
    }

    public void setPlatformBuilder(TestObjectIntegrationPlatformBuilder platformBuilder) {
        this.platformBuilder = platformBuilder;
    }

    public List<TestObjectIntegrationViewBuilder> getSortedViewBuilders() {
        List<TestObjectIntegrationViewBuilder> sortedBuilders = new ArrayList<>(getIntegrationViewMap().entrySet())
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
