package com.kms.katalon.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks method that will be invoked before every test case launches.
 * </br>
 * </br>
 * {@link BeforeTestCase} methods also invoke before {@link SetUp} methods of the launched test case.
 * </br>
 * In {@link BeforeTestCase} method, clients can get some related information for the current executed test case by
 * declaring a {@link TestCaseContext} parameter.
 * </br>
 * </br>
 * Test hook execution flow:
 * 
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
public @interface BeforeTestCase {

}
