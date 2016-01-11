package com.kms.katalon.core.keyword

import groovy.transform.CompileStatic

import java.text.MessageFormat

import org.apache.commons.lang.math.NumberUtils;

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

@CompileStatic
public class BuiltinKeywords {
    private static final KeywordLogger logger = KeywordLogger.getInstance();

    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void comment(String message) {
        // Just a comment line, do nothing
        logger.logInfo(message);
    }


    /**
     * Verify if two strings match each other, the second string can be a
     * regular expression.
     *
     * @param actualText
     * @param expectedText
     * @param isRegex
     *            whether string2 is regular expression or not, has two values
     *            true, false (default if left empty)
     * @param flowControl
     * @return whether two strings match, has two values true, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static boolean verifyMatch(String actualText, String expectedText, boolean isRegex, FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "")
        return KeywordMain.runKeyword({
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_MATCHING_ACTUAL_TXT_W_EXPECTED_TXT, actualText, expectedText, regularExpressionLog));
            if (KeywordHelper.match(actualText, expectedText, isRegex)) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ACTUAL_TXT_MATCHED_EXPECTED_TXT, actualText, expectedText, regularExpressionLog));
                return true;
            } else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ACTUAL_TXT_NOT_MATCHED_EXPECTED_TXT, actualText, expectedText, regularExpressionLog), flowControl, null);
                return false;
            }
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_MATCHING_BETWEEN_TXTS, actualText, expectedText, regularExpressionLog))
    }


    /**
     * Verify if two strings do not match each other, the second string can be a
     * regular expression.
     *
     * @param actualText
     * @param expectedText
     * @param isRegex
     *            whether string2 is regular expression or not, has two values
     *            true, false (default if left empty)
     * @param flowControl
     * @return whether two strings do not match, has two values true, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static boolean verifyNotMatch(String actualText, String expectedText, boolean isRegex, FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "")
        return KeywordMain.runKeyword({
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_MATCHING_ACTUAL_TXT_W_EXPECTED_VAL, actualText, expectedText, regularExpressionLog));
            if (KeywordHelper.match(actualText, expectedText, isRegex)) {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_TXTS_MATCHED_BUT_EXPECTED_UNMATCHED, actualText, expectedText, regularExpressionLog), flowControl, null);
                return false;
            } else {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TXTS_UNMATCHED, actualText, expectedText, regularExpressionLog));
                return true;
            }
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_TXTS_ARE_UNMATCHED, actualText, expectedText, regularExpressionLog))
    }


    /**
     * Verify if two objects are equal.
     *
     * @param actualObject
     * @param expectedObject
     * @param flowControl
     * @return true if actual object is equals with expected object; otherwise, false
     * @throws StepFailedException
     * @throws StepErrorException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyEqual(Object actualObject, Object expectedObject, FailureHandling flowControl) throws StepFailedException, StepErrorException {
        return KeywordMain.runKeyword({
            logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_COMPARE_ACTUAL_W_EXPECTED, String.valueOf(actualObject), String.valueOf(expectedObject)));
            boolean isEqual;
            if (NumberUtils.isNumber(String.valueOf(actualObject)) && NumberUtils.isNumber(String.valueOf(expectedObject))) {
                isEqual = KeywordHelper.comparingNumberObject(actualObject, expectedObject) == 0;
            } else {
                isEqual = actualObject == expectedObject;
            }
            if (!isEqual) {
                KeywordMain.stepFailed(
                        MessageFormat.format(StringConstants.KW_MSG_OBJECTS_ARE_NOT_EQUAL, String.valueOf(actualObject), String.valueOf(expectedObject)), flowControl, null);
                return false;
            } else {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJECTS_ARE_EQUAL, String.valueOf(actualObject), String.valueOf(expectedObject)));
            }
            return isEqual;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJECTS_ARE_EQUAL, actualObject, expectedObject))
    }



    /**
     * Verify if two objects are not equal.
     *
     * @param actualNumber
     * @param expectedNumber
     * @param flowControl
     * @return true if actual object is not equals with expected object; otherwise, false
     * @throws StepFailedException
     * @throws StepErrorException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyNotEqual(Object actualObject, Object expectedObject, FailureHandling flowControl) throws StepFailedException, StepErrorException {
        return KeywordMain.runKeyword({
            logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_COMPARE_ACTUAL_W_EXPECTED, String.valueOf(actualObject), String.valueOf(expectedObject)));
            boolean isEqual;
            if (NumberUtils.isNumber(String.valueOf(actualObject)) && NumberUtils.isNumber(String.valueOf(expectedObject))) {
                isEqual = KeywordHelper.comparingNumberObject(actualObject, expectedObject) == 0;
            } else {
                isEqual = actualObject == expectedObject;
            }
            if (isEqual) {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJECTS_ARE_EQUAL, String.valueOf(actualObject), String.valueOf(expectedObject)), flowControl, null);
                return false;
            } else {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJECTS_ARE_NOT_EQUAL, String.valueOf(actualObject), String.valueOf(expectedObject)));
            }
            return !isEqual;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJECTS_ARE_NOT_EQUAL, actualObject, expectedObject))
    }

    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyGreaterThan(Object actualNumber, Object expectedNumber, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            boolean isGreaterThan = KeywordHelper.comparingNumberObject(actualNumber, expectedNumber) > 0;
            if (isGreaterThan) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ACTUAL_NUM_IS_GREATER_THAN_EXPECTED_NUM, actualNumber, expectedNumber));
            } else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ACTUAL_NUM_IS_NOT_GREATER_THAN_EXPECTED_NUM, actualNumber, expectedNumber), flowControl, null);
                return false;
            }
            return isGreaterThan;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_WHICH_NUM_IS_GREATER, actualNumber, expectedNumber))
    }

    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyGreaterThanOrEqual(Object actualNumber, Object expectedNumber, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            boolean isGreaterThanOrEqual = KeywordHelper.comparingNumberObject(actualNumber, expectedNumber) >= 0;
            if (isGreaterThanOrEqual) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ACTUAL_NUM_IS_GT_OR_EQ_TO_EXPECTED_NUM, actualNumber, expectedNumber));
            } else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ACTUAL_NUM_IS_NOT_GT_OR_EQ_TO_EXPECTED_NUM, actualNumber, expectedNumber), flowControl, null);
            }
            return isGreaterThanOrEqual;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_NUMS_ARE_GT_OR_EQ, actualNumber, expectedNumber))
    }

    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyLessThan(Object actualNumber, Object expectedNumber, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            boolean isLessThan = KeywordHelper.comparingNumberObject(actualNumber, expectedNumber) < 0;
            if (isLessThan) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ACTUAL_NUM_IS_LT_EXPECTED_NUM, actualNumber, expectedNumber));
            } else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ACTUAL_NUM_IS_NOT_LT_EXPECTED_NUM, actualNumber, expectedNumber), flowControl, null);
                return false;
            }
            return isLessThan;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_WHICH_NUM_IS_LT, actualNumber, expectedNumber))
    }

    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyLessThanOrEqual(Object actualNumber, Object expectedNumber, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            boolean isLessThanOrEqual = KeywordHelper.comparingNumberObject(actualNumber, expectedNumber) <= 0;
            if (isLessThanOrEqual) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ACTUAL_NUM_IS_LT_OR_EQ_TO_EXPECTED_NUM, actualNumber, expectedNumber));
            } else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ACTUAL_NUM_IS_NOT_LT_OR_EQ_EXPECTED_NUM, actualNumber, expectedNumber), flowControl, null);
                return false;
            }
            return isLessThanOrEqual;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_WHICH_NUM_IS_LT_OR_EQ_TO, actualNumber, expectedNumber))
    }

    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static String concatenate(String[] strings, FailureHandling flowControl) throws StepFailedException {
        String stringArrayValue = null;
        return KeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_CHECING_STRINGS_PARAM);
            if (strings == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_STRS_PARAM_IS_NULL);
            }
            StringBuilder stringArrayValueBuilder = new StringBuilder("[");
            for (String s : strings) {
                stringArrayValueBuilder.append("'");
                stringArrayValueBuilder.append(s);
                stringArrayValueBuilder.append("',");
            }
            stringArrayValue = stringArrayValueBuilder.substring(0, stringArrayValueBuilder.length() - 1) + "]";
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CONCAT_STR_ARRAY, stringArrayValue));
            StringBuilder sb = new StringBuilder();
            for (String str : strings) {
                sb.append(str);
            }
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_CONCAT_STR_ARRAY, stringArrayValue, sb.toString()));
            return sb.toString();
        }
        , flowControl, (stringArrayValue != null) ? MessageFormat.format(StringConstants.KW_CANNOT_CONCAT_STR_ARRAY, stringArrayValue) : StringConstants.KW_CANNOT_CONCAT)
    }

    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static callTestCase(TestCase calledTestCase, Map<String, Object> binding, FailureHandling flowControl) throws Exception {
        KeywordMain.runKeyword({
            List<Throwable> parentErrors = ErrorCollector.getCollector().getCoppiedErrors();
            try {
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_CALLED_TC);
                if (calledTestCase == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_CALLED_TC_IS_NULL);
                }

                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_STARTING_TO_CALL_TC, calledTestCase.getTestCaseId()));
                TestResult result = TestCaseMain.runTestCase(calledTestCase.getTestCaseId(), new TestCaseBinding(
                        calledTestCase.getTestCaseId(), binding), flowControl);
                switch (result.getTestStatus().getStatusValue()) {
                    case TestStatus.TestStatusValue.FAILED:
                        KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_CALL_TC_FAILED, calledTestCase.getTestCaseId()), flowControl, result.getMessage());
                        break;
                    case TestStatus.TestStatusValue.ERROR:
                        KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_CALL_TC_X_FAILED_BECAUSE_OF_ERROR, calledTestCase.getTestCaseId()),
                        flowControl, result.getMessage());
                        break;
                    case TestStatus.TestStatusValue.PASSED:
                        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_CALL_TC_X_SUCCESSFULLY, calledTestCase.getTestCaseId()));
                        break;
                    default:
                        break;
                }
            } finally {
                if (flowControl == FailureHandling.OPTIONAL) {
                    ErrorCollector.getCollector().clearErrors();
                    ErrorCollector.getCollector().getErrors().addAll(parentErrors);
                }
            }
        }
        , flowControl, (calledTestCase != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_CALL_TC_W_ID_X, calledTestCase.getTestCaseId())
        : StringConstants.KW_MSG_CANNOT_CALL_TC)
    }

    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void delay(Object second, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_DELAYING_BROWSER_IN_SEC, second));
            int secondIntValue = Integer.valueOf(String.valueOf(second));
            try {
                Thread.sleep(secondIntValue * 1000);
            } catch (InterruptedException e) {
                // Thread is interrupted, do nothing
            }
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DELAYED_SEC, second));
        }
        , flowControl, StringConstants.KW_MSG_CANNOT_DELAY_BROWSER)
    }

    public static void verifyValue(Object actualValue, Object expectedValue, String operator) {
    }
}
