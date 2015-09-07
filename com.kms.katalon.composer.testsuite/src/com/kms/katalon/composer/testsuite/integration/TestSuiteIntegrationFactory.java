package com.kms.katalon.composer.testsuite.integration;

import java.util.LinkedHashMap;
import java.util.Map;

import com.kms.katalon.composer.testsuite.parts.integration.TestSuiteIntegrationViewBuilder;

public class TestSuiteIntegrationFactory {
	private static TestSuiteIntegrationFactory _instance;
	
	private Map<String, TestSuiteIntegrationViewBuilder> integrationViewMap;
	
	public Map<String, TestSuiteIntegrationViewBuilder> getIntegrationViewMap() {
		return integrationViewMap;
	}

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
	
}
