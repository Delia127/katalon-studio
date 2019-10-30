package com.kms.katalon.execution.launcher.result;

import com.kms.katalon.execution.launcher.ReportableLauncher;
import com.kms.katalon.execution.launcher.TestSuiteCollectionLauncher;

public class TestSuiteCollectionLauncherResult extends LauncherResult {

    private TestSuiteCollectionLauncher launcher;

    public TestSuiteCollectionLauncherResult(TestSuiteCollectionLauncher launcher, int totalTestCases) {
        super(totalTestCases);
        this.launcher = launcher;
    }

    @Override
    public int getReturnCode() {
        boolean hasErrors = false;
        boolean hasFailures = false;
        boolean hasFailuresAndErrors = false;
        for (ReportableLauncher subLauncher : launcher.getSubLaunchers()) {
            int childResult = subLauncher.getResult().getReturnCode();
            if (childResult == RETURN_CODE_PASSED) {
                continue;
            }
            if (subLauncher.needToRerun()) {
                continue;
            }
            switch (childResult) {
                case RETURN_CODE_FAILED_AND_ERROR: {
                    hasFailuresAndErrors = true;
                    break;
                }
                case RETURN_CODE_ERROR: {
                    hasErrors = true;
                    break;
                }
                case RETURN_CODE_FAILED: {
                    hasFailures = true;
                }
            }
        }
        if (hasFailuresAndErrors) {
            return RETURN_CODE_FAILED_AND_ERROR;
        }
        if (hasErrors && hasFailures) {
            return RETURN_CODE_FAILED_AND_ERROR;
        }
        if (hasErrors) {
            return RETURN_CODE_ERROR;
        }
        if (hasFailures) {
            return RETURN_CODE_FAILED;
        }
        return RETURN_CODE_PASSED;
    }
}
