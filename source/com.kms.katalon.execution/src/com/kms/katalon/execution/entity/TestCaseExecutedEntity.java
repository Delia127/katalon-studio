package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

public class TestCaseExecutedEntity {
	private String testCaseId;
	private List<TestDataExecutedEntity> testDataExecutions;
	private int loopTimes;
	
	public TestCaseExecutedEntity(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public List<TestDataExecutedEntity> getTestDataExecutions() {
		if (testDataExecutions == null) {
			testDataExecutions = new ArrayList<TestDataExecutedEntity>();
		}
		return testDataExecutions;
	}

	public void setTestDataExecutions(List<TestDataExecutedEntity> testDataExecutions) {
		this.testDataExecutions = testDataExecutions;
	}

	public int getLoopTimes() {
		return loopTimes;
	}

	public void setLoopTimes(int loopTimes) {
		this.loopTimes = loopTimes;
	}
	
	public TestDataExecutedEntity getTestDataExecuted(String testDataLinkId) {
		for (TestDataExecutedEntity executedEntity : getTestDataExecutions()) {
			if (executedEntity.getTestDataLinkId().equals(testDataLinkId) ) {
				return executedEntity;
			}
		}
		return null;
	}

}
