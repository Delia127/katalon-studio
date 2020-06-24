package com.kms.katalon.selenium.ide.util;

import java.util.List;
import java.util.Map;

import com.kms.katalon.selenium.ide.model.TestCase;
import com.kms.katalon.selenium.ide.model.TestSuite;

public final class ParsedResult {
    private final List<TestSuite> testSuites;
    private final List<TestCase> testCases;
    private final Map<String, String> monoSuiteTests;

    public ParsedResult(List<TestSuite> testSuites, List<TestCase> testCases, Map<String, String> monoSuiteTests) {
        this.testSuites = testSuites;
        this.testCases = testCases;
        this.monoSuiteTests = monoSuiteTests;
    }

    public List<TestSuite> getTestSuites() {
        return testSuites;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public Map<String, String> getMonoSuiteTests() {
        return monoSuiteTests;
    }
}
