package com.kms.katalon.core.context;

import com.kms.katalon.core.annotation.AfterTestSuite;
import com.kms.katalon.core.annotation.BeforeTestSuite;

/**
 * Provides some related information of the current executed test suite.
 * </br>
 * System will automatically inject an instance of {@link TestSuiteContext} as a parameter in {@link BeforeTestSuite}
 * methods,
 * {@link AfterTestSuite} methods.
 * 
 * @see BeforeTestSuite
 * @see AfterTestSuite
 * @since 5.1
 */
public interface TestSuiteContext {
    /**
     * @return Id of the current executed test suite
     */
    String getTestSuiteId();
}
