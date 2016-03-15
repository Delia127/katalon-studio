package com.kms.katalon.execution.logging;

import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.logging.XmlLogRecordException;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class LogExceptionFilter {
    public static boolean isTraceableException(XmlLogRecordException logException) {
        String className = logException.getClassName();
        return (isTestCaseScript(className) || isCustomKeywordScript(className));
    }

    public static boolean isCustomKeywordScript(String className) {
        return (KeywordController.getInstance().isCustomKeywordClass(className));
    }

    public static boolean isTestCaseScript(String className) {
        return (className.matches("Script[0-9]{13}"));
    }

    public static TestCaseEntity getTestCaseByLogException(XmlLogRecordException logException) throws Exception {
        String className = logException.getClassName();

        TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByScriptName(className);
        return testCase;
    }
}
