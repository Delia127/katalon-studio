package com.kms.katalon.composer.report.parts.integration;

import com.kms.katalon.core.logging.model.TestCaseLogRecord;

public interface TestCaseChangedEventListener {
    void changeTestCase(TestCaseLogRecord logRecord);
}
