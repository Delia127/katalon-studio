package com.kms.katalon.composer.testcase.integration;

import java.util.LinkedHashMap;
import java.util.Map;

import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationViewBuilder;

public class TestCaseIntegrationFactory {
	private static TestCaseIntegrationFactory _instance;
	
	private Map<String, TestCaseIntegrationViewBuilder> integrationViewMap;
	
	public Map<String, TestCaseIntegrationViewBuilder> getIntegrationViewMap() {
		return integrationViewMap;
	}

	private TestCaseIntegrationFactory() {
		integrationViewMap = new LinkedHashMap<String, TestCaseIntegrationViewBuilder>();
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
	
}
