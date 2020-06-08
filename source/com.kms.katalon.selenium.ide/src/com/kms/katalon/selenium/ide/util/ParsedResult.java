package com.kms.katalon.selenium.ide.util;

import java.util.List;

import com.kms.katalon.selenium.ide.model.TestCase;
import com.kms.katalon.selenium.ide.model.TestSuite;

public final class ParsedResult {
    private final List<TestSuite> testSuites;
    private final List<TestCase> testCases;

    public ParsedResult(List<TestSuite> testSuites, List<TestCase> testCases) {
        this.testSuites = testSuites;
        this.testCases = testCases;
    }

    public List<TestSuite> getTestSuites() {
        return testSuites;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }
}
