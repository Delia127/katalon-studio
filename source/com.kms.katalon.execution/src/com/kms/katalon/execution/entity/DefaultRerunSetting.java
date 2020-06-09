package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.execution.console.entity.BooleanConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.console.entity.IntegerConsoleOption;
import com.kms.katalon.execution.console.entity.StringConsoleOption;

public class DefaultRerunSetting implements Rerunable, ConsoleOptionContributor {
    public static final int DEFAULT_RERUN_TIME = 0;
    private static final int DEFAULT_PREVIOUS_RUN_TIME = 0;
    public static final boolean DEFAULT_RERUN_FAILED_TEST_CASE_ONLY = false;
    public static final boolean DEFAULT_RERUN_FAILED_TEST_CASE_TEST_DATA_ONLY = false;
    public static final boolean DEFAULT_RERUN_IMMEDIATELY = false;
    public final static String RETRY_OPTION = "retry";
    public final static String RETRY_FAIL_TEST_CASE_ONLY_OPTION = "retryFailedTestCases";
    public final static String RETRY_FAIL_TEST_CASE_TEST_DATA_ONLY_OPTION = "retryFailedTestCasesTestData";
    public final static String RETRY_STRATEGY = "retryStrategy";
    
    private int previousRerunTimes;
    private int remainingRerunTimes;
    private boolean rerunFailedTestCaseOnly;
    private boolean rerunFailedTestCaseWithTestDataOnly;
    private boolean rerunImmediately;
    private boolean overrideRerunFailedTestCaseOnly;
    private boolean overrideRerunFailedTestCaseWithTestDataOnly;
    private boolean overrideRerunImmediately;
    private boolean overrideRemainingRerunTimes;
    
    public enum RetryStrategyValue {
        
        IMMEDIATELY, ALL_EXECUTIONS, FAILED_EXECUTIONS;
        
        public static RetryStrategyValue getEnum(String value) {
            if("immediately".equals(value)) {
                return RetryStrategyValue.IMMEDIATELY;
            } else if("allExecutions".equals(ALL_EXECUTIONS)) {
                return RetryStrategyValue.ALL_EXECUTIONS;
            } else if("failedExecutions".equals(FAILED_EXECUTIONS)) {
                return RetryStrategyValue.FAILED_EXECUTIONS;
            }
            return RetryStrategyValue.ALL_EXECUTIONS;
        }

        public String getUserFacingValue() {
            if (this.equals(IMMEDIATELY)) {
                return "immediately";
            }
            if (this.equals(ALL_EXECUTIONS)) {
                return "allExecutions";
            }
            if (this.equals(FAILED_EXECUTIONS)) {
                return "failedExecutions";
            }
            return "allExecutions";
        }
    };

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

    public static final BooleanConsoleOption LEGACY_RERUN_FAIL_TEST_CASE_ONLY_CONSOLE_OPTION = new BooleanConsoleOption() {
        @Override
        public String getOption() {
            return RETRY_FAIL_TEST_CASE_ONLY_OPTION;
        }
    };
    
    public static final BooleanConsoleOption LEGACY_RERUN_FAIL_TEST_CASE__TEST_DATA_ONLY_CONSOLE_OPTION = new BooleanConsoleOption() {
        @Override
        public String getOption() {
            return RETRY_FAIL_TEST_CASE_TEST_DATA_ONLY_OPTION;
        }
    };
    
    public static final StringConsoleOption RERUN_STRATEGY_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return RETRY_STRATEGY;
        }
    };

    public DefaultRerunSetting() {
        this(DEFAULT_RERUN_TIME, DEFAULT_RERUN_FAILED_TEST_CASE_ONLY);
    }

    public DefaultRerunSetting(int remainingRerunTime, boolean rerunFailedTestCaseOnly) {
        this(DEFAULT_PREVIOUS_RUN_TIME, remainingRerunTime, rerunFailedTestCaseOnly);
    }

    public DefaultRerunSetting(int previousRerunTimes, int remainingRerunTime, boolean rerunFailedTestCaseOnly) {
        this(previousRerunTimes, remainingRerunTime, rerunFailedTestCaseOnly, DEFAULT_RERUN_IMMEDIATELY);
    }

    public DefaultRerunSetting(int previousRerunTimes, int remainingRerunTime, boolean rerunFailedTestCasesOnly,
            boolean rerunImmediately) {
        setPreviousRerunTimes(previousRerunTimes);
        setRemainingRerunTimes(remainingRerunTime);
        setRerunFailedTestCaseOnly(rerunFailedTestCasesOnly);
        setRerunImmediately(rerunImmediately);
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
        consoleOptionList.add(LEGACY_RERUN_FAIL_TEST_CASE_ONLY_CONSOLE_OPTION);
        consoleOptionList.add(LEGACY_RERUN_FAIL_TEST_CASE__TEST_DATA_ONLY_CONSOLE_OPTION);
        consoleOptionList.add(RERUN_STRATEGY_CONSOLE_OPTION);
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
        } else if (consoleOption == LEGACY_RERUN_FAIL_TEST_CASE_ONLY_CONSOLE_OPTION) {
            setRerunFailedTestCaseOnly(Boolean.valueOf(argumentValue));
            overrideRerunFailedTestCaseOnly = true;
        } else if (consoleOption == LEGACY_RERUN_FAIL_TEST_CASE__TEST_DATA_ONLY_CONSOLE_OPTION) {
            setRerunFailedTestCaseAndTestDataOnly(Boolean.valueOf(argumentValue));
            overrideRerunFailedTestCaseWithTestDataOnly = true;
        } else if (consoleOption == RERUN_STRATEGY_CONSOLE_OPTION) {
            RetryStrategyValue strategyValue = RetryStrategyValue.getEnum(argumentValue);
            resetAllLegacyRetrySettings();
            switch (strategyValue) {
                case IMMEDIATELY:
                    setRerunImmediately(true);
                    overrideRerunImmediately = true;
                    break;
                case FAILED_EXECUTIONS:
                    setRerunFailedTestCaseOnly(true);
                    overrideRerunFailedTestCaseOnly = true;
                    setRerunFailedTestCaseAndTestDataOnly(true);
                    overrideRerunFailedTestCaseWithTestDataOnly = true;
                    break;
                case ALL_EXECUTIONS:
                default:
                    break;
            }
        }
    }
    
    private void resetAllLegacyRetrySettings() {
        setRerunImmediately(false);
        overrideRerunImmediately = false;
        setRerunFailedTestCaseOnly(false);
        overrideRerunFailedTestCaseOnly = false;
        setRerunFailedTestCaseAndTestDataOnly(false);
        overrideRerunFailedTestCaseWithTestDataOnly = false;
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
        
        if (!overrideRerunFailedTestCaseWithTestDataOnly) {
            setRerunFailedTestCaseAndTestDataOnly(rerunable.isRerunFailedTestCasesAndTestDataOnly());
        }
        
        if(!overrideRerunImmediately) {
            setRerunImmediately(rerunable.isRerunImmediately());
        }
        return this;
    }
    
    public void setRerunFailedTestCaseAndTestDataOnly(boolean val) {
        this.rerunFailedTestCaseWithTestDataOnly = val;
    }

    @Override
    public boolean isRerunFailedTestCasesAndTestDataOnly() {
        return rerunFailedTestCaseWithTestDataOnly;
    }

    @Override
    public boolean isRerunImmediately() {
        return rerunImmediately;
    }

    public void setRerunImmediately(boolean val) {
        rerunImmediately = val;
    }


}
