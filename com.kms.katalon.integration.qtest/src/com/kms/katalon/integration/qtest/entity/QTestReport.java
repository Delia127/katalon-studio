package com.kms.katalon.integration.qtest.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class QTestReport {
	private Map<Integer, QTestLog> testLogMap;

	public Map<Integer, QTestLog> getTestLogMap() {
		if (testLogMap == null) {
			testLogMap = new LinkedHashMap<Integer, QTestLog>();
		}
		return testLogMap;
	}

	public void setTestLogMap(Map<Integer, QTestLog> testLogMap) {
		this.testLogMap = testLogMap;
	}
}
