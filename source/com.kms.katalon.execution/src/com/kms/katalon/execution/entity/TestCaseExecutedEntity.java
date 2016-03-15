package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseExecutedEntity implements IExecutedEntity {
	private String testCaseId;
	private List<TestDataExecutedEntity> testDataExecutions;
	private int loopTimes;
	
	public TestCaseExecutedEntity(TestCaseEntity testCase) {
	    testCaseId = testCase.getId();
	}
	
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

    @Override
    public int getTotalTestCases() {
        return 1;
    }

    @Override
    public List<TestCaseExecutedEntity> getTestCaseExecutedEntities() {
        List<TestCaseExecutedEntity> executedEntities = new ArrayList<TestCaseExecutedEntity>(1);
        executedEntities.add(this);
        
        return executedEntities;
    }

    @Override
    public String getSourceName() {
        return null;
    }

    @Override
    public String getSourceId() {
        return testCaseId;
    }

    @Override
    public String getSourceDescription() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public int mainTestCaseDepth() {
        return 0;
    }

}
