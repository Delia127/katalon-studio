package com.kms.katalon.composer.testsuite.transfer;

import java.io.Serializable;

import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuiteTestCaseLinkTransferData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TestSuiteEntity testSuite;
	private TestSuiteTestCaseLink testSuiteTestCaseLink;
	public TestSuiteTestCaseLinkTransferData(TestSuiteEntity testSuite, TestSuiteTestCaseLink testSuiteTestCaseLink) {
		this.setTestSuite(testSuite);
		this.setTestSuiteTestCaseLink(testSuiteTestCaseLink);
	}
	public TestSuiteEntity getTestSuite() {
		return testSuite;
	}
	public void setTestSuite(TestSuiteEntity testSuite) {
		this.testSuite = testSuite;
	}
	public TestSuiteTestCaseLink getTestSuiteTestCaseLink() {
		return testSuiteTestCaseLink;
	}
	public void setTestSuiteTestCaseLink(TestSuiteTestCaseLink testSuiteTestCaseLink) {
		this.testSuiteTestCaseLink = testSuiteTestCaseLink;
	}
}
