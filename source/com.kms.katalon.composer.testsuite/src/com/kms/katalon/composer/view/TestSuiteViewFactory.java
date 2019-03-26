package com.kms.katalon.composer.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationViewBuilder;
import com.kms.katalon.composer.testsuite.platform.PlatformTestSuiteUIViewBuilder;
import com.kms.katalon.composer.testsuite.view.builder.TestSuiteUIViewBuilder;
import com.kms.katalon.controller.ProjectController;

@SuppressWarnings("unused")
public class TestSuiteViewFactory {
	
	private static TestSuiteViewFactory _instance;
	
	private Map<String, TestSuiteUIViewBuilder> viewMap;
	
	private PlatformTestSuiteUIViewBuilder platformBuilder;
	
	public Map<String, TestSuiteUIViewBuilder> getViewMap() {
		return viewMap;
	}
	
	public TestSuiteViewFactory() { 
		viewMap = new HashMap<String, TestSuiteUIViewBuilder>();
	}
	
	public static TestSuiteViewFactory getInstance() {
		if(_instance == null) {
			_instance = new TestSuiteViewFactory();
		}
		return _instance;		
	}
	
	public void addNewView(String productName, TestSuiteUIViewBuilder view) {
		viewMap.put(productName, view);
	}
	
	public void setPlatformBuilder(PlatformTestSuiteUIViewBuilder platformBuilder) {
		this.platformBuilder = platformBuilder;
	}
	
	public List<TestSuiteUIViewBuilder> getSortedBuilders() {
		 List<TestSuiteUIViewBuilder> sortedBuilders = new ArrayList<>(getViewMap().entrySet())
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
