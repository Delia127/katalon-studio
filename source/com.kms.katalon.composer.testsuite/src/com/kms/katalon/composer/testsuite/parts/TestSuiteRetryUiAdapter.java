package com.kms.katalon.composer.testsuite.parts;

import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public interface TestSuiteRetryUiAdapter {
    public void setDirty(boolean value);
    public TestSuiteEntity getTestSuite();
}
