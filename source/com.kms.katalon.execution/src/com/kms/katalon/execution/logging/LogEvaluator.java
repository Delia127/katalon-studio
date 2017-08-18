package com.kms.katalon.execution.logging;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.execution.configuration.IRunConfiguration;

public interface LogEvaluator {
    default boolean isLogUnderTestCaseMainLevel(IRunConfiguration runConfig, int logDepth) {
        return logDepth == runConfig.getExecutionSetting().getExecutedEntity().mainTestCaseDepth() + 1;
    }

    default boolean isEndTestCaseLog(XmlLogRecord logRecord) {
        return StringUtils.defaultString(logRecord.getMessage()).startsWith(StringConstants.LOG_END_TEST);
    }

    default boolean isStartTestCaseLog(XmlLogRecord logRecord) {
        return StringUtils.defaultString(logRecord.getMessage()).startsWith(StringConstants.LOG_START_TEST);
    }
    
    default boolean isStartStep(XmlLogRecord logRecord) {
        return StringUtils.defaultString(logRecord.getMessage()).startsWith(StringConstants.LOG_START_KEYWORD);
    }
}
