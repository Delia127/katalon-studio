package com.kms.katalon.execution.launcher;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.core.logging.model.TestSuiteLogRecord;

public class WaitTestSuiteDone {
	
	private List<TestSuiteLogRecord> testSuiteQueued = new ArrayList<>();
	
	private long wattingTestSuite;
		
	public void setTestSuiteToQueues(TestSuiteLogRecord testsuite) {
		testSuiteQueued.add(testsuite);
	}
	
	public List<TestSuiteLogRecord> getTestSuiteQueue() {
		return testSuiteQueued;
	}
	
	public void setWattingTestSuite() {
		wattingTestSuite--;
	}
	
	public long getWattingTestSuite() {
		return wattingTestSuite;
	}
}
