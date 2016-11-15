package com.kms.katalon.core.keyword.builtin

import groovy.transform.CompileStatic

import java.text.MessageFormat

import org.apache.commons.lang.math.NumberUtils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.constants.StringConstants
import com.kms.katalon.core.exception.StepErrorException
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.helper.KeywordHelper
import com.kms.katalon.core.logging.ErrorCollector
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.logging.model.TestStatus
import com.kms.katalon.core.main.TestCaseMain
import com.kms.katalon.core.main.TestResult
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseBinding
import java.text.MessageFormat
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.keyword.KeywordMain
import com.kms.katalon.core.annotation.Action
import com.kms.katalon.core.keyword.AbstractKeyword
import com.kms.katalon.core.keyword.SupportLevel
import com.kms.katalon.core.keyword.KeywordExecutor

@Action(value = "concatenate")
public class ConcatenateKeyword extends AbstractKeyword {

    @CompileStatic
    @Override
    public SupportLevel getSupportLevel(Object ...params) {
        return SupportLevel.BUITIN
    }

    @CompileStatic
    @Override
    public Object execute(Object ...params) {
        String[] strings = (String[]) params[0]
        FailureHandling flowControl = (FailureHandling)(params.length > 1 && params[1] instanceof FailureHandling ? params[1] : RunConfiguration.getDefaultFailureHandling())
        return concatenate(strings,flowControl)
    }

    @CompileStatic
    public String concatenate(String[] strings, FailureHandling flowControl) throws StepFailedException {
        String stringArrayValue = null
        return KeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_CHECING_STRINGS_PARAM)
            if (strings == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_STRS_PARAM_IS_NULL)
            }
            StringBuilder stringArrayValueBuilder = new StringBuilder("[")
            for (String s : strings) {
                stringArrayValueBuilder.append("'")
                stringArrayValueBuilder.append(s)
                stringArrayValueBuilder.append("',")
            }
            stringArrayValue = stringArrayValueBuilder.substring(0, stringArrayValueBuilder.length() - 1) + "]"
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CONCAT_STR_ARRAY, stringArrayValue))
            StringBuilder sb = new StringBuilder()
            for (String str : strings) {
                sb.append(str)
            }
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_CONCAT_STR_ARRAY, [stringArrayValue, sb.toString()] as Object[]))
            return sb.toString()
        }, flowControl, (stringArrayValue != null) ? MessageFormat.format(StringConstants.KW_CANNOT_CONCAT_STR_ARRAY,
        stringArrayValue) : StringConstants.KW_CANNOT_CONCAT)
    }
}
