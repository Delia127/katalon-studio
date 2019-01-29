package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.report.ReportTestCaseEntity;
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
	    List<TestDataExecutedEntity> ls = getTestDataExecutions();
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

    public List<ReportTestCaseEntity> reportTestCases() {
        List<ReportTestCaseEntity> reportTestCases = new ArrayList<>();
        
        for (int loop = 0; loop < loopTimes; loop++) {
            ReportTestCaseEntity reportItems = new ReportTestCaseEntity();
            String testCaseName = (loop == 0) ? getSourceName() : String.format("%s - Iteration %d", getSourceName(), loop);
            reportItems.setTestCaseName(testCaseName);
            reportItems.setTestCaseId(getSourceId());
            reportTestCases.add(reportItems);
        }
        return reportTestCases;
    }
}
