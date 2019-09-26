package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.execution.console.entity.BooleanConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.console.entity.IntegerConsoleOption;

public class DefaultRerunSetting implements Rerunable, ConsoleOptionContributor {
    public static final int DEFAULT_RERUN_TIME = 0;
    private static final int DEFAULT_PREVIOUS_RUN_TIME = 0;
    public static final boolean DEFAULT_RERUN_FAILED_TEST_CASE_ONLY = false;

    public final static String RETRY_OPTION = "retry";
    public final static String RETRY_FAIL_TEST_CASE_ONLY_OPTION = "retryFailedTestCases";

    private int previousRerunTimes;
    private int remainingRerunTimes;
    private boolean rerunFailedTestCaseOnly;
    private boolean overrideRerunFailedTestCaseOnly;
    private boolean overrideRemainingRerunTimes;

    public static final IntegerConsoleOption RETRY_CONSOLE_OPTION = new IntegerConsoleOption() {
        @Override
        public String getOption() {
            return RETRY_OPTION;
        }

        @Override
        public String getDefaultArgumentValue() {
            return String.valueOf(DEFAULT_RERUN_TIME);
        }
    };

    public static final BooleanConsoleOption RERUN_FAIL_TEST_CASE_ONLY_CONSOLE_OPTION = new BooleanConsoleOption() {
        @Override
        public String getOption() {
            return RETRY_FAIL_TEST_CASE_ONLY_OPTION;
        }
    };

    public DefaultRerunSetting() {
        this(DEFAULT_RERUN_TIME, DEFAULT_RERUN_FAILED_TEST_CASE_ONLY);
    }

    public DefaultRerunSetting(int remainingRerunTime, boolean rerunFailedTestCaseOnly) {
        this(DEFAULT_PREVIOUS_RUN_TIME, remainingRerunTime, rerunFailedTestCaseOnly);
    }

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

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
        consoleOptionList.add(RETRY_CONSOLE_OPTION);
        consoleOptionList.add(RERUN_FAIL_TEST_CASE_ONLY_CONSOLE_OPTION);
        return consoleOptionList;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if (StringUtils.isBlank(argumentValue)) {
            return;
        }
        if (consoleOption == RETRY_CONSOLE_OPTION) {
            setRemainingRerunTimes(Integer.valueOf(argumentValue));
            overrideRemainingRerunTimes = true;
        } else if (consoleOption == RERUN_FAIL_TEST_CASE_ONLY_CONSOLE_OPTION) {
            setRerunFailedTestCaseOnly(Boolean.valueOf(argumentValue));
            overrideRerunFailedTestCaseOnly = true;
        }
    }

    @Override
    public Rerunable mergeWith(Rerunable rerunable) {
        if (rerunable == null) {
            return this;
        }
        if (!overrideRemainingRerunTimes) {
            setRemainingRerunTimes(rerunable.getRemainingRerunTimes());
        }
        if (!overrideRerunFailedTestCaseOnly) {
            setRerunFailedTestCaseOnly(rerunable.isRerunFailedTestCasesOnly());
        }
        return this;
    }

}
