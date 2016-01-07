package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kms.katalon.core.testdata.TestData;

public class TestSuiteExecutedEntity {
	private String testSuiteId;
	private List<TestCaseExecutedEntity> testCaseExecutedEntities;
	private Map<String, TestData> testDataMap;
	private ReportLocationSetting reportLocationSetting;

	public String getTestSuiteId() {
		return testSuiteId;
	}

	public void setTestSuiteId(String testSuiteId) {
		this.testSuiteId = testSuiteId;
	}

	public List<TestCaseExecutedEntity> getTestCaseExecutedEntities() {
		if (testCaseExecutedEntities == null) {
			testCaseExecutedEntities = new ArrayList<TestCaseExecutedEntity>();
		}
		return testCaseExecutedEntities;
	}

	public void setTestCaseExecutiedEntities(List<TestCaseExecutedEntity> testCaseExecutedEntities) {
		this.testCaseExecutedEntities = testCaseExecutedEntities;
	}
	
	public int getTotalTestCases() {
		int total = 0;
		
		for (TestCaseExecutedEntity testCaseExecutionEntity : getTestCaseExecutedEntities()) {
			total += testCaseExecutionEntity.getLoopTimes();
		}
		return total;
	}

	public Map<String, TestData> getTestDataMap() {
		return testDataMap;
	}

	public void setTestDataMap(Map<String, TestData> testDataMap) {
		this.testDataMap = testDataMap;
	}
	
    public ReportLocationSetting getReportLocationSetting() {
        return reportLocationSetting;
    }

    public void setReportLocation(ReportLocationSetting reportLocation) {
        this.reportLocationSetting = reportLocation;
    }

}
