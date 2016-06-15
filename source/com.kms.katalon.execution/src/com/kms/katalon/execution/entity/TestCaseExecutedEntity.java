package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseExecutedEntity extends ExecutedEntity {
	private List<TestDataExecutedEntity> testDataExecutions;
	private int loopTimes;
	
	public TestCaseExecutedEntity(TestCaseEntity testCase) {
	    super(testCase);
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
    public List<IExecutedEntity> getExecutedItems() {
        List<IExecutedEntity> executedEntities = new ArrayList<IExecutedEntity>(1);
        executedEntities.add(this);
        
        return executedEntities;
    }

    @Override
    public int mainTestCaseDepth() {
        return 0;
    }

    @Override
    public int getTotalTestCases() {
        return 1;
    }

}
