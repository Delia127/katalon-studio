package com.kms.katalon.core.context;

import java.util.Map;

import com.kms.katalon.core.annotation.AfterTestCase;
import com.kms.katalon.core.annotation.BeforeTestCase;

/**
 * Provides some related information of the current executed test case.
 * </br>
 * System will automatically inject an instance of {@link TestCaseContext} as a parameter in {@link BeforeTestCase} methods,
 * {@link AfterTestCase} methods.
 * 
 * @see BeforeTestCase
 * @see AfterTestCase
 * @since 5.1
 */
public interface TestCaseContext {
    /**
     * @return Id of the current executed test case
     */
    String getTestCaseId();

    /**
     * @return A map stores variables (key is variable's name, value is variable's value) that were used in the current
     * test case.
     */
    Map<String, Object> getTestCaseVariables();

    /**
     * Returns test status after the test case executed complete.
     * 
     * @return It should be <code>PASSED</code>, <code>FAILED</code>, or <code>ERROR</code>
     */
    String getTestCaseStatus();
}
