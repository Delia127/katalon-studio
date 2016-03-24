package com.kms.katalon.execution.entity;

public class DefaultRerunSetting implements Rerunnable {
    
    private int previousRerunTimes;
    private int remainingRerunTimes;
    private boolean rerunFailedTestCaseOnly;
    
    public DefaultRerunSetting(int previousRerunTimes, int remainingRerunTime, boolean rerunFailedTestCaseOnly) {
        setPreviousRerunTimes(previousRerunTimes);
        setRemainingRerunTimes(remainingRerunTime);
        setRerunFailedTestCaseOnly(rerunFailedTestCaseOnly);
    }

    @Override
    public boolean isRerunFailedTestCasesOnly() {
        return rerunFailedTestCaseOnly;
    }

    public void setRerunFailedTestCaseOnly(boolean rerunFailedTestCaseOnly) {
        this.rerunFailedTestCaseOnly = rerunFailedTestCaseOnly;
    }

    @Override
    public int getPreviousRerunTimes() {
        return previousRerunTimes;
    }

    @Override
    public int getRemainingRerunTimes() {
        return remainingRerunTimes;
    }

    public void setPreviousRerunTimes(int previousRerunTimes) {
        this.previousRerunTimes = previousRerunTimes;
    }

    public void setRemainingRerunTimes(int remainingRerunTimes) {
        this.remainingRerunTimes = remainingRerunTimes;
    }

}
