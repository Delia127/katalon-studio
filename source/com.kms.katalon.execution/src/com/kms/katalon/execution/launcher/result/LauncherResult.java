package com.kms.katalon.execution.launcher.result;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kms.katalon.core.logging.model.TestStatus;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.execution.constants.StringConstants;

public class LauncherResult implements ILauncherResult {
    public static final int RETURN_CODE_PASSED = 0;

    public static final int RETURN_CODE_FAILED = 1;

    public static final int RETURN_CODE_ERROR = 2;

    public static final int RETURN_CODE_FAILED_AND_ERROR = 3;

    public static final int RETURN_CODE_INVALID_ARGUMENT = 4;

    private int totalTestCases;

    private int numPasses;

    private int numFailures;

    private int numErrors;

    private int numIncomplete;

    private List<TestStatus> statuses;

    public LauncherResult(int totalTestCases) {
        this.setTotalTestCases(totalTestCases);
        setNumPasses(0);
        setNumFailures(0);
        setNumErrors(0);
        setNumIncomplete(0);

        statuses = new ArrayList<>(totalTestCases);
        for (int i = 0; i < totalTestCases; i++) {
            TestStatus testStatus = new TestStatus();
            testStatus.setStatusValue(TestStatusValue.INCOMPLETE);
            statuses.add(testStatus);
        }
    }

    public int getTotalTestCases() {
        return totalTestCases;
    }

    private void setTotalTestCases(int totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public int getNumPasses() {
        return numPasses;
    }

    public void setNumPasses(int numPasses) {
        this.numPasses = numPasses;
    }

    public int getNumFailures() {
        return numFailures;
    }

    public void setNumFailures(int numFailures) {
        this.numFailures = numFailures;
    }

    public int getNumErrors() {
        return numErrors;
    }

    public void setNumErrors(int numErrors) {
        this.numErrors = numErrors;
    }

    public void setNumIncomplete(int numIncomplete) {
        this.numIncomplete = numIncomplete;
    }

    public int getExecutedTestCases() {
        return getNumPasses() + getNumFailures() + getNumErrors();
    }

    public void increasePasses() {
        statuses.get(getExecutedTestCases()).setStatusValue(TestStatusValue.PASSED);
        numPasses++;
    }

    public void increaseFailures() {
        statuses.get(getExecutedTestCases()).setStatusValue(TestStatusValue.FAILED);
        numFailures++;
    }

    public void increaseErrors() {
        statuses.get(getExecutedTestCases()).setStatusValue(TestStatusValue.ERROR);
        numErrors++;
    }

    public boolean isNotPassed() {
        return numErrors + numFailures > 0;
    }

    public boolean hasErrors() {
        return numErrors > 0;
    }

    public boolean hasFailures() {
        return numFailures > 0;
    }

    public int getReturnCode() {
        if (!isNotPassed()) {
            return RETURN_CODE_PASSED;
        } else {
            if (hasErrors() && hasFailures()) {
                return RETURN_CODE_FAILED_AND_ERROR;
            } else if (hasErrors()) {
                return RETURN_CODE_ERROR;
            } else {
                return RETURN_CODE_FAILED;
            }
        }
    }

    @Override
    public String toString() {
        return MessageFormat.format(StringConstants.MODEL_TOTAL_PASSED_FAILED_ERRORS,
                Integer.toString(getTotalTestCases()), Integer.toString(getNumPasses()),
                Integer.toString(getNumFailures()), Integer.toString(getNumErrors()));
    }

    @Override
    public int getNumIncomplete() {
        return numIncomplete;
    }

    @Override
    public TestStatusValue[] getResultValues() {
        return statuses.stream()
                .map(s -> s.getStatusValue())
                .collect(Collectors.toList())
                .toArray(new TestStatusValue[0]);
    }

    @Override
    public TestStatus[] getStatuses() {
        return statuses.toArray(new TestStatus[0]);
    }
}
