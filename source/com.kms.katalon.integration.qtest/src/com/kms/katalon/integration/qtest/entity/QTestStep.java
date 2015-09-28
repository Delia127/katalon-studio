package com.kms.katalon.integration.qtest.entity;

public class QTestStep extends QTestEntity {
	private String description;
	private String expectedResult;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExpectedResult() {
		return expectedResult;
	}

	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}
}
