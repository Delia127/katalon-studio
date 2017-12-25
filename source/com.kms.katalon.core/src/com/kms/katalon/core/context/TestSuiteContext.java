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
     * @since 5.1
     * @return Id of the current executed test suite
     */
    String getTestSuiteId();
    
    /**
     * @since 5.3
     * @return <ul>
     * <li>COMPLETE: All test cases completed normally.</li>
     * <li>ERROR: Some errors occurred. Eg: SetUp or TearDown methods failed.</li>
     * </ul>
     */
    String getStatus();
}
