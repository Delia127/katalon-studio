package com.kms.katalon.entity.dal.exception;

import java.text.MessageFormat;

import com.kms.katalon.entity.constants.StringConstants;

public class TestCaseIsReferencedByTestSuiteExepception extends Exception {
	private static final long serialVersionUID = -5781380888506538125L;

	public TestCaseIsReferencedByTestSuiteExepception(String testCasePk, String message) {
		super(MessageFormat.format(StringConstants.EXC_CANNOT_DEL_TEST_CASE_X_FOR_REASON, testCasePk, message));
	}
}
