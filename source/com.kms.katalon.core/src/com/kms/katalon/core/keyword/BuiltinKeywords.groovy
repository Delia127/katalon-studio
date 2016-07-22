package com.kms.katalon.core.keyword

import groovy.transform.CompileStatic

import java.text.MessageFormat

import org.apache.commons.lang.ObjectUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointCell
import com.kms.katalon.core.configuration.RunConfiguration
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
import com.kms.katalon.core.testdata.TestData

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
                throw new StepFailedException(MessageFormat.format(StringConstants.KW_MSG_ACTUAL_TXT_NOT_MATCHED_EXPECTED_TXT, actualText, expectedText, regularExpressionLog));
                return false;
            }
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_MATCHING_BETWEEN_TXTS, actualText,
        expectedText, regularExpressionLog))
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
     * @return whether two strings match, has two values true, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static boolean verifyMatch(String actualText, String expectedText, boolean isRegex) throws StepFailedException {
        return verifyMatch(actualText, expectedText, isRegex, RunConfiguration.getDefaultFailureHandling());
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
                throw new StepFailedException(MessageFormat.format(StringConstants.KW_MSG_TXTS_MATCHED_BUT_EXPECTED_UNMATCHED, actualText, expectedText, regularExpressionLog));
                return false;
            } else {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TXTS_UNMATCHED, actualText, expectedText, regularExpressionLog));
                return true;
            }
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_TXTS_ARE_UNMATCHED, actualText, expectedText,
        regularExpressionLog))
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
     * @return whether two strings do not match, has two values true, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static boolean verifyNotMatch(String actualText, String expectedText, boolean isRegex) throws StepFailedException {
        return verifyNotMatch(actualText, expectedText, isRegex, RunConfiguration.getDefaultFailureHandling());
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
                throw new StepFailedException(MessageFormat.format(StringConstants.KW_MSG_OBJECTS_ARE_NOT_EQUAL,
                String.valueOf(actualObject), String.valueOf(expectedObject)))
            } else {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJECTS_ARE_EQUAL, String.valueOf(actualObject), String.valueOf(expectedObject)));
            }
            return isEqual;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJECTS_ARE_EQUAL, actualObject,
        expectedObject))
    }

    /**
     * Verify if two objects are equal.
     *
     * @param actualObject
     * @param expectedObject
     * @return true if actual object is equals with expected object; otherwise, false
     * @throws StepFailedException
     * @throws StepErrorException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyEqual(Object actualObject, Object expectedObject) throws StepFailedException, StepErrorException {
        return verifyEqual(actualObject, expectedObject, RunConfiguration.getDefaultFailureHandling());
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
                throw new StepFailedException(MessageFormat.format(StringConstants.KW_MSG_OBJECTS_ARE_EQUAL, String.valueOf(actualObject), String.valueOf(expectedObject)));
                return false;
            } else {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJECTS_ARE_NOT_EQUAL, String.valueOf(actualObject), String.valueOf(expectedObject)));
            }
            return !isEqual;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJECTS_ARE_NOT_EQUAL, actualObject,
        expectedObject))
    }

    /**
     * Verify if two objects are not equal.
     *
     * @param actualNumber
     * @param expectedNumber
     * @return true if actual object is not equals with expected object; otherwise, false
     * @throws StepFailedException
     * @throws StepErrorException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyNotEqual(Object actualObject, Object expectedObject) throws StepFailedException, StepErrorException {
        return verifyNotEqual(actualObject, expectedObject, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if the actual number is greater than the expected number
     * @param actualNumber
     * @param expectedNumber
     * @param flowControl
     * @return
     *      true the actual number is greater than the expected number; otherwise false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyGreaterThan(Object actualNumber, Object expectedNumber, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            boolean isGreaterThan = KeywordHelper.comparingNumberObject(actualNumber, expectedNumber) > 0;
            if (isGreaterThan) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ACTUAL_NUM_IS_GREATER_THAN_EXPECTED_NUM, actualNumber, expectedNumber));
            } else {
                throw new StepFailedException(MessageFormat.format(StringConstants.KW_MSG_ACTUAL_NUM_IS_NOT_GREATER_THAN_EXPECTED_NUM, actualNumber, expectedNumber));
                return false;
            }
            return isGreaterThan;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_WHICH_NUM_IS_GREATER, actualNumber, expectedNumber))
    }

    /**
     * Verify if the actual number is greater than the expected number
     * @param actualNumber
     * @param expectedNumber
     * @return
     *      true the actual number is greater than the expected number; otherwise false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyGreaterThan(Object actualNumber, Object expectedNumber) throws StepFailedException {
        return verifyGreaterThan(actualNumber, expectedNumber, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if the actual number is greater than or equal with the expected number
     * @param actualNumber
     * @param expectedNumber
     * @param flowControl
     * @return
     *      true the actual number is greater than or equal with the expected number ; otherwise false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyGreaterThanOrEqual(Object actualNumber, Object expectedNumber, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            boolean isGreaterThanOrEqual = KeywordHelper.comparingNumberObject(actualNumber, expectedNumber) >= 0;
            if (isGreaterThanOrEqual) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ACTUAL_NUM_IS_GT_OR_EQ_TO_EXPECTED_NUM, actualNumber, expectedNumber));
            } else {
                throw new StepFailedException(MessageFormat.format(StringConstants.KW_MSG_ACTUAL_NUM_IS_NOT_GT_OR_EQ_TO_EXPECTED_NUM, actualNumber, expectedNumber));
            }
            return isGreaterThanOrEqual;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_NUMS_ARE_GT_OR_EQ, actualNumber,
        expectedNumber))
    }

    /**
     * Verify if the actual number is greater than or equal with the expected number
     * @param actualNumber
     * @param expectedNumber
     * @return
     *      true the actual number is greater than or equal with the expected number ; otherwise false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyGreaterThanOrEqual(Object actualNumber, Object expectedNumber) throws StepFailedException {
        return verifyGreaterThanOrEqual(actualNumber, expectedNumber, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if the actual number is less than the expected number
     * @param actualNumber
     * @param expectedNumber
     * @param flowControl
     * @return
     *      true the actual number is less than the expected number; otherwise false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyLessThan(Object actualNumber, Object expectedNumber, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            boolean isLessThan = KeywordHelper.comparingNumberObject(actualNumber, expectedNumber) < 0;
            if (isLessThan) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ACTUAL_NUM_IS_LT_EXPECTED_NUM, actualNumber, expectedNumber));
            } else {
                throw new StepFailedException(MessageFormat.format(StringConstants.KW_MSG_ACTUAL_NUM_IS_NOT_LT_EXPECTED_NUM, actualNumber, expectedNumber));
                return false;
            }
            return isLessThan;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_WHICH_NUM_IS_LT, actualNumber
        , expectedNumber))
    }

    /**
     * Verify if the actual number is less than the expected number
     * @param actualNumber
     * @param expectedNumber
     * @return
     *      true the actual number is less than the expected number; otherwise false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyLessThan(Object actualNumber, Object expectedNumber) throws StepFailedException {
        return verifyLessThan(actualNumber, expectedNumber, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if the actual number is less than or equal with the expected number
     * @param actualNumber
     * @param expectedNumber
     * @param flowControl
     * @return
     *      true the actual number is less than or equal with the expected number; otherwise false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyLessThanOrEqual(Object actualNumber, Object expectedNumber, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            boolean isLessThanOrEqual = KeywordHelper.comparingNumberObject(actualNumber, expectedNumber) <= 0;
            if (isLessThanOrEqual) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ACTUAL_NUM_IS_LT_OR_EQ_TO_EXPECTED_NUM, actualNumber, expectedNumber));
            } else {
                throw new StepFailedException(MessageFormat.format(StringConstants.KW_MSG_ACTUAL_NUM_IS_NOT_LT_OR_EQ_EXPECTED_NUM, actualNumber, expectedNumber));
                return false;
            }
            return isLessThanOrEqual;
        }
        , flowControl, MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_WHICH_NUM_IS_LT_OR_EQ_TO, actualNumber,
        expectedNumber))
    }

    /**
     * Verify if the actual number is less than or equal with the expected number
     * @param actualNumber
     * @param expectedNumber
     * @return
     *      true the actual number is less than or equal with the expected number; otherwise false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NUMBER)
    public static boolean verifyLessThanOrEqual(Object actualNumber, Object expectedNumber) throws StepFailedException {
        return verifyLessThanOrEqual(actualNumber, expectedNumber, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Concatenate string array into a single string
     * @param strings
     *      a string array
     * @param flowControl
     * @return
     *      the result string
     * @throws StepFailedException
     */
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
        , flowControl, (stringArrayValue != null) ? MessageFormat.format(StringConstants.KW_CANNOT_CONCAT_STR_ARRAY,
        stringArrayValue) : StringConstants.KW_CANNOT_CONCAT)
    }

    /**
     * Concatenate string array into a single string
     * @param strings
     *      a string array
     * @return
     *      the result string
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static String concatenate(String[] strings) throws StepFailedException {
        return concatenate(strings, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Call and execute another test case
     * @param calledTestCase
     *      represent a test case
     * @param binding
     *      map object contain variables bindings for the called test case
     * @param flowControl
     * @throws Exception
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static Object callTestCase(TestCase calledTestCase, Map<String, Object> binding, FailureHandling flowControl) throws Exception {
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
                        throw new StepFailedException(MessageFormat.format(StringConstants.KW_MSG_CALL_TC_FAILED, calledTestCase.getTestCaseId()));
                        break;
                    case TestStatus.TestStatusValue.ERROR:
                        throw new StepErrorException(MessageFormat.format(StringConstants.KW_MSG_CALL_TC_X_FAILED_BECAUSE_OF_ERROR, calledTestCase.getTestCaseId()));
                        break;
                    case TestStatus.TestStatusValue.PASSED:
                        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_CALL_TC_X_SUCCESSFULLY, calledTestCase.getTestCaseId()));
                        break;
                    default:
                        break;
                }
                return result.getScriptResult()
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

    /**
     * Call and execute another test case
     * @param calledTestCase
     *      represent a test case
     * @param binding
     *      map object contain variables bindings for the called test case
     * @throws Exception
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static Object callTestCase(TestCase calledTestCase, Map<String, Object> binding) throws Exception {
        return callTestCase(calledTestCase, binding, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Delay execution for a specific time (in seconds)
     * @param second
     *      number of seconds to delay
     * @param flowControl
     * @throws StepFailedException
     */
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

    /**
     * Delay execution for a specific time (in seconds)
     * @param second
     *      number of seconds to delay
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void delay(Object second) throws StepFailedException {
        delay(second, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify Checkpoint
     * 
     * @param checkpoint Checkpoint
     * @param logChangedValues <code>true</code> will log all the changed value between checkpoint data and the source. <code>false</code> will not log any changed value.
     * @param flowControl failure handling
     * @return <code>true</code> if checked data of checkpoint matches their source data. Otherwise, <code>false</code>.
     * @throws StepFailedException if data does not match
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static boolean verifyCheckpoint(Checkpoint checkpoint, boolean logChangedValues, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_MSG_VERIFY_CHECKPOINT)
            if (checkpoint == null) {
                throw new IllegalArgumentException(StringConstants.KW_MSG_CHECKPOINT_IS_NULL)
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_MSG_CHECKPOINT_ID_X, checkpoint.getId()))
            if (checkpoint.getTakenDate() == null) {
                throw new StepFailedException(StringConstants.KW_MSG_NO_SNAPSHOT)
            }
            if (checkpoint.getCheckpointData() == null) {
                logger.logWarning(StringConstants.KW_MSG_CHECKPOINT_DATA_IS_NULL)
            }
            if (checkpoint.getSourceData() == null) {
                logger.logWarning(StringConstants.KW_MSG_CHECKPOINT_SRC_DATA_IS_NULL)
            }
            if (checkpoint.getCheckpointData() == null && checkpoint.getSourceData() == null) {
                logger.logPassed(StringConstants.KW_MSG_CHECKPOINT_DATA_MATCHES_WITH_NULL);
                return true
            }
            if ((checkpoint.getCheckpointData() != null && checkpoint.getSourceData() == null) || (checkpoint.getCheckpointData() == null && checkpoint.getSourceData() != null)) {
                throw new StepFailedException(StringConstants.KW_MSG_CHECKPOINT_DATA_DOES_NOT_MATCH)
            }

            if (checkpoint.getCheckpointRowNumbers() == checkpoint.getSourceRowNumbers()) {
                logger.logInfo(StringConstants.KW_MSG_CHECKPOINT_ROW_NUMBER_MATCHES)
            } else {
                logger.logWarning(StringConstants.KW_MSG_CHECKPOINT_ROW_NUMBER_DOES_NOT_MATCH)
            }

            if (checkpoint.getCheckpointColumnNumbers() == checkpoint.getSourceColumnNumbers()) {
                logger.logInfo(StringConstants.KW_MSG_CHECKPOINT_COL_NUMBER_MATCHES)
            } else {
                logger.logWarning(StringConstants.KW_MSG_CHECKPOINT_COL_NUMBER_DOES_NOT_MATCH)
            }

            List<List<Object>> sourceData = checkpoint.getSourceData()
            List<List<CheckpointCell>> checkpointData = checkpoint.getCheckpointData()
            try {
                logger.logInfo(StringConstants.KW_MSG_VERIFY_CHECKED_VALUES)
                boolean isDataNotChanged = true
                for (int rowIndex = 0; rowIndex < checkpoint.getCheckpointRowNumbers(); rowIndex++) {
                    List<CheckpointCell> row = checkpointData.get(rowIndex)
                    for (int colIndex = 0; colIndex < checkpoint.getCheckpointColumnNumbers(); colIndex++) {
                        CheckpointCell cell = row.get(colIndex)
                        if (!cell.isChecked()) {
                            continue
                        }

                        Object checkedValue = cell.getValue()
                        Object currentValue = sourceData.get(rowIndex).get(colIndex)
                        if (!ObjectUtils.equals(checkedValue, currentValue)) {
                            if (logChangedValues) {
                                logger.logWarning(MessageFormat.format(StringConstants.KW_MSG_CHECKPOINT_NOT_MATCH_AT_ROW_X_COL_Y_CHECKED_VAL_NEW_VAL, rowIndex + TestData.BASE_INDEX, colIndex + TestData.BASE_INDEX, checkedValue, currentValue))
                            }
                            isDataNotChanged = false
                        }
                    }
                }

                if (isDataNotChanged) {
                    logger.logPassed(StringConstants.KW_MSG_CHECKPOINT_DATA_MATCHES)
                    return true
                }

                throw new StepFailedException(StringConstants.KW_MSG_CHECKPOINT_DATA_DOES_NOT_MATCH)
            } catch (Exception e) {
                // Index out of bound
                throw new StepFailedException(StringConstants.KW_MSG_CHECKPOINT_DATA_DOES_NOT_MATCH)
            }
        }, flowControl, MessageFormat.format(StringConstants.KW_MSG_UNABLE_TO_VERIFY_CHECKPOINT_X, checkpoint != null ? checkpoint.getId() : StringUtils.EMPTY));
    }

    /**
     * Verify Checkpoint
     * 
     * @param checkpoint Checkpoint
     * @param logChangedValues <code>true</code> will log all the changed value between checkpoint data and the source. <code>false</code> will not log any changed value.
     * @return <code>true</code> if checked data of checkpoint matches their source data. Otherwise, <code>false</code>.
     * @throws StepFailedException if data does not match
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static boolean verifyCheckpoint(Checkpoint checkpoint, boolean logChangedValues) throws StepFailedException {
        return verifyCheckpoint(checkpoint, logChangedValues, RunConfiguration.getDefaultFailureHandling())
    }
}
