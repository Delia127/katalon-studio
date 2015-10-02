package com.kms.katalon.entity.dal.exception;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.kms.katalon.entity.constants.StringConstants;

public class TestCaseIsReferencedByTestSuiteExepception extends Exception {
    private static final long serialVersionUID = -5781380888506538125L;

    // Key: test case' id, Value: test suite's id
    private Map<String, Set<String>> references;
    
    public TestCaseIsReferencedByTestSuiteExepception() {
        super();
    }

    public TestCaseIsReferencedByTestSuiteExepception(String testCasePk, String message) {
        super(MessageFormat.format(StringConstants.EXC_CANNOT_DEL_TEST_CASE_X_FOR_REASON, testCasePk, message));
    }

    public Map<String, Set<String>> getReferences() {
        if (references == null) {
            references = new HashMap<String, Set<String>>();
        }
        return references;
    }

    public void setReferences(Map<String, Set<String>> references) {
        this.references = references;
    }
    
    public void addReference(String testCaseId, String testSuiteId) {
        Set<String> testSuiteIds = getReferences().get(testCaseId);
        if (testSuiteIds == null) {
            testSuiteIds = new HashSet<String>();
        }
        testSuiteIds.add(testSuiteId);
        references.put(testCaseId, testSuiteIds);
    }
}
