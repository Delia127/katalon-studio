package com.kms.katalon.core.main;

import com.kms.katalon.core.logging.model.TestStatus;

public class TestResult {
	private TestStatus testStatus;
	private String message;
	public TestStatus getTestStatus() {
		return testStatus;
	}
	public void setTestStatus(TestStatus testStatus) {
		this.testStatus = testStatus;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
