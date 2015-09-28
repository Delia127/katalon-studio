package com.kms.katalon.entity.link;

public class TestStepCallTestCaseLink {
	private String testStepGuid;
	private String callTestCaseGuid;

	public TestStepCallTestCaseLink(String testStepGuid, String callTestCaseGuid) {
		super();
		this.testStepGuid = testStepGuid;
		this.callTestCaseGuid = callTestCaseGuid;
	}

	/**
	 * @return the testStepGuid
	 */
	public String getTestStepGuid() {
		return testStepGuid;
	}

	/**
	 * @param testStepGuid
	 *            the testStepGuid to set
	 */
	public void setTestStepGuid(String testStepGuid) {
		this.testStepGuid = testStepGuid;
	}

	/**
	 * @return the callTestCaseGuid
	 */
	public String getCallTestCaseGuid() {
		return callTestCaseGuid;
	}

	/**
	 * @param callTestCaseGuid
	 *            the callTestCaseGuid to set
	 */
	public void setCallTestCaseGuid(String callTestCaseGuid) {
		this.callTestCaseGuid = callTestCaseGuid;
	}

}
