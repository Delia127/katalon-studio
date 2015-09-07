package com.kms.katalon.integration.qtest.entity;

import com.kms.katalon.core.logging.model.TestCaseLogRecord;

public class QTestLogUploadedPreview {
	private int testLogIndex;
	private QTestProject qTestProject;
	private QTestSuite qTestSuite;
	private QTestTestCase qTestCase;
	private QTestRun qTestRun;
	private QTestLog qTestLog;
	private TestCaseLogRecord testCaseLogRecord;

	public int getTestLogIndex() {
		return testLogIndex;
	}

	public void setTestLogIndex(int testLogIndex) {
		this.testLogIndex = testLogIndex;
	}
	
	public QTestProject getQTestProject() {
		return qTestProject;
	}

	public void setQTestProject(QTestProject qTestProject) {
		this.qTestProject = qTestProject;
	}

	public QTestSuite getQTestSuite() {
		return qTestSuite;
	}

	public void setQTestSuite(QTestSuite qTestSuite) {
		this.qTestSuite = qTestSuite;
	}

	public QTestTestCase getQTestCase() {
		return qTestCase;
	}

	public void setQTestCase(QTestTestCase qTestCase) {
		this.qTestCase = qTestCase;
	}

	public QTestRun getQTestRun() {
		return qTestRun;
	}

	public void setQTestRun(QTestRun qTestRun) {
		this.qTestRun = qTestRun;
	}

	public QTestLog getQTestLog() {
		return qTestLog;
	}

	public void setQTestLog(QTestLog qTestLog) {
		this.qTestLog = qTestLog;
	}

	public TestCaseLogRecord getTestCaseLogRecord() {
		return testCaseLogRecord;
	}

	public void setTestCaseLogRecord(TestCaseLogRecord testCaseLogRecord) {
		this.testCaseLogRecord = testCaseLogRecord;
	}
}
