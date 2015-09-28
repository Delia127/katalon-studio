package com.kms.katalon.core.testcase;

import java.util.Map;

public class TestCaseBinding {
	private String testCaseId;
	private Map<String, Object> bindedValues;
	
	public TestCaseBinding(String testCaseId, Map<String, Object> bindedValues) {
		setTestCaseId(testCaseId);
		setBindedValues(bindedValues);
	}

	public Map<String, Object> getBindedValues() {
		return bindedValues;
	}

	public void setBindedValues(Map<String, Object> bindedValues) {
		this.bindedValues = bindedValues;
	}

	public String getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}
}
