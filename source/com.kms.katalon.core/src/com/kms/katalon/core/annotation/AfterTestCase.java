package com.kms.katalon.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.kms.katalon.core.context.TestCaseContext;

/**
 * Marks method that will be invoked after every test case launches.
 * </br>
 * </br>
 * {@link AfterTestCase} methods also invoke after {@link TearDown} methods of the launched test case.
 * </br>
 * In {@link AfterTestCase} method, clients can get some related information for the current executed test case by
 * declaring a {@link TestCaseContext} parameter. 
 * </br>
 * </br>
 * Test hook execution flow:
 * <pre>
 * Invoke all {@link BeforeTestSuite} methods
 *      
 *      Each Test Case
 *          Invoke all {@link BeforeTestCase} methods
 *          Invoke all {@link SetUp} methods
 *          
 *          Execute Test Case's Script
 *                  
 *          Invoke all {@link TearDown} methods
 *          Invoke all {@link AfterTestCase} methods
 *          
 * Invoke all {@link AfterTestSuite} methods
 * </pre>
 * 
 * @since 5.1
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface AfterTestCase {

}
