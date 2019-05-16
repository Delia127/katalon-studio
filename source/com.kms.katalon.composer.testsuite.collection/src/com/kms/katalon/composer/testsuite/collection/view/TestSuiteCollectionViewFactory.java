package com.kms.katalon.composer.testsuite.collection.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kms.katalon.composer.testsuite.collection.platform.PlatformTestSuiteCollectionUIViewBuilder;
import com.kms.katalon.composer.testsuite.collection.view.builder.TestSuiteCollectionUIViewBuilder;
import com.kms.katalon.controller.ProjectController;

public class TestSuiteCollectionViewFactory {

    private static TestSuiteCollectionViewFactory _instance;

    private Map<String, TestSuiteCollectionUIViewBuilder> viewMap;

    private PlatformTestSuiteCollectionUIViewBuilder platformBuilder;

    public Map<String, TestSuiteCollectionUIViewBuilder> getViewMap() {
        return viewMap;
    }

    private TestSuiteCollectionViewFactory() {
        viewMap = new HashMap<String, TestSuiteCollectionUIViewBuilder>();
    }

    public static TestSuiteCollectionViewFactory getInstance() {
        if (_instance == null) {
            _instance = new TestSuiteCollectionViewFactory();
        }
        return _instance;
    }

    public void addNewView(String productName, TestSuiteCollectionUIViewBuilder view) {
        viewMap.put(productName, view);
    }

    public void setPlatformBuilder(PlatformTestSuiteCollectionUIViewBuilder platformBuilder) {
        this.platformBuilder = platformBuilder;
    }

    public List<TestSuiteCollectionUIViewBuilder> getSortedBuilders() {
        List<TestSuiteCollectionUIViewBuilder> sortedBuilders = new ArrayList<>(getViewMap().entrySet()).stream()
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
