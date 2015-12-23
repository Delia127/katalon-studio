package com.kms.katalon.execution.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestDataCombinationType;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.TestCaseExecutedEntity;
import com.kms.katalon.execution.entity.TestDataExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ExecutionUtil {
    private static final String BIT = "bit";
    private static final String OS_ARCHITECTURE_PROPERTY = "sun.arch.data.model";
    private static final String OS_NAME_PROPERTY = "os.name";
    private static final String UNKNOW_HOST = "Unknow host";

    public static String getLocalHostName() {
        try {
            return System.getProperty("user.name") + " - " + InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            return UNKNOW_HOST;
        }
    }

    public static String getLocalOS() {
        return System.getProperty(OS_NAME_PROPERTY) + " " + System.getProperty(OS_ARCHITECTURE_PROPERTY) + BIT;
    }

    public static IRunConfigurationContributor getDefaultExecutionConfiguration() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
        String selectedRunConfiguration = store
                .getString(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_CONFIGURATION);
        IRunConfigurationContributor[] allBuiltinRunConfigurationContributor = RunConfigurationCollector.getInstance()
                .getAllBuiltinRunConfigurationContributors();
        for (IRunConfigurationContributor runConfigurationContributor : allBuiltinRunConfigurationContributor) {
            if (runConfigurationContributor.getId().equals(selectedRunConfiguration)) {
                return runConfigurationContributor;
            }
        }
        return null;
    }

    public static int getDefaultPageLoadTimeout() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
        return store.getInt(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_TIMEOUT);
    }

    public static boolean openReportAfterExecuting() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
        return store.getBoolean(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_OPEN_REPORT_AFTER_EXECUTING);
    }

    public static Map<String, Object> escapeGroovy(Map<String, Object> propertiesMap) {
        for (Entry<String, Object> entry : propertiesMap.entrySet()) {
            if (entry.getValue() instanceof String) {
                entry.setValue(GroovyStringUtil.escapeGroovy((String) entry.getValue()));
            }
        }
        return propertiesMap;
    }

    /**
     * Create a new TestSuiteExecutedEntity that's based on the given params. Store a map of test data used in the given
     * test suite into the new created TestSuiteExecutedEntity
     * 
     * @param testSuite
     * @param project
     * @return
     */
    public static TestSuiteExecutedEntity loadTestDataForTestSuite(TestSuiteEntity testSuite, ProjectEntity project,
            List<String> passedTestCaseIds) throws Exception {
        TestSuiteExecutedEntity testSuiteExecutedEntity = new TestSuiteExecutedEntity();
        testSuiteExecutedEntity.setTestSuiteId(TestSuiteController.getInstance().getIdForDisplay(testSuite));
        String projectDir = project.getFolderLocation();

        // key: id of id of test data
        Map<String, TestData> testDataUsedMap = new HashMap<String, TestData>();

        for (TestSuiteTestCaseLink testCaseLink : TestSuiteController.getInstance().getTestSuiteTestCaseRun(testSuite)) {
            TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(
                    testCaseLink.getTestCaseId());

            if (testCase == null) {
                throw new IllegalArgumentException(MessageFormat.format(StringConstants.UTIL_EXC_TEST_CASE_X_NOT_FOUND,
                        testCaseLink.getTestCaseId()));
            }
            if (passedTestCaseIds != null && testSuite.isRerunFailedTestCasesOnly()
                    && passedTestCaseIds.contains(TestCaseController.getInstance().getIdForDisplay(testCase))) {
                continue;
            }

            TestCaseExecutedEntity testCaseExecutedEntity = new TestCaseExecutedEntity(testCaseLink.getTestCaseId());

            testCaseExecutedEntity.setLoopTimes(0);

            int numberTestCaseUsedOnce = -1;
            int numTestDataRowUsedManyTimes = 1;

            List<TestCaseTestDataLink> testDataLinkUsedList = TestSuiteController.getInstance()
                    .getTestDataLinkUsedInTestCase(testCaseLink);

            if (testDataLinkUsedList.size() > 0) {
                for (TestCaseTestDataLink testDataLink : testDataLinkUsedList) {

                    // check test data in the test data map first, if is doesn't
                    // exist, find it by using TestDataFactory to read its
                    // source.
                    TestData testData = testDataUsedMap.get(testDataLink.getTestDataId());

                    if (testData == null) {
                        testData = TestDataFactory.findTestDataForExternalBundleCaller(testDataLink.getTestDataId(),
                                projectDir);
                        testDataUsedMap.put(testDataLink.getTestDataId(), testData);
                    }

                    if (testData == null || testData.getRowNumbers() < 0) {
                        throw new IllegalArgumentException(MessageFormat.format(
                                StringConstants.UTIL_EXC_TD_DATA_SRC_X_UNAVAILABLE, testDataLink.getTestDataId()));
                    } else {
                        TestDataExecutedEntity testDataExecutedEntity = getTestDataExecutedEntity(testCaseLink,
                                testDataLink, testData);
                        if (testDataExecutedEntity == null)
                            continue;

                        int rowCount = testDataExecutedEntity.getRowIndexes().length;

                        // update numberTestCaseUsedOnce or
                        // numTestDataRowUsedManyTimes depend on
                        // combinationType of testDataLink
                        if (testDataLink.getCombinationType() == TestDataCombinationType.ONE) {
                            if (numberTestCaseUsedOnce < 1) {
                                numberTestCaseUsedOnce = rowCount;
                            } else {
                                numberTestCaseUsedOnce = Math.min(numberTestCaseUsedOnce, rowCount);
                            }
                        } else {
                            numTestDataRowUsedManyTimes *= rowCount;

                            for (TestDataExecutedEntity siblingDataExecuted : testCaseExecutedEntity
                                    .getTestDataExecutions()) {
                                if (siblingDataExecuted.getType() == TestDataCombinationType.MANY) {
                                    siblingDataExecuted.setMultiplier(siblingDataExecuted.getMultiplier() * rowCount);
                                }
                            }
                        }
                        testCaseExecutedEntity.getTestDataExecutions().add(testDataExecutedEntity);
                    }
                }

                if (numberTestCaseUsedOnce < 1) {
                    numberTestCaseUsedOnce = 1;
                }

                testCaseExecutedEntity.setLoopTimes(numTestDataRowUsedManyTimes * numberTestCaseUsedOnce);
            } else {
                testCaseExecutedEntity.setLoopTimes(1);
            }

            if (numberTestCaseUsedOnce < 1) {
                numberTestCaseUsedOnce = 1;
            }
            // make sure all TestDataExecutedEntity in testCaseExecutedEntity
            // has the same rows to prevent NullPointerException
            cutRedundantIndexes(testCaseExecutedEntity, numberTestCaseUsedOnce);

            testSuiteExecutedEntity.getTestCaseExecutedEntities().add(testCaseExecutedEntity);
        }

        testSuiteExecutedEntity.setTestDataMap(testDataUsedMap);

        return testSuiteExecutedEntity;
    }

    /**
     * Make sure all TestDataExecutedEntity in the given testCaseExecutedEntity has the same rows with the given
     * numberTestCaseUsedOnce
     * 
     * @param testCaseExecutedEntity
     * @see {@link TestCaseExecutedEntity}
     * @param numberTestCaseUsedOnce
     */
    private static void cutRedundantIndexes(TestCaseExecutedEntity testCaseExecutedEntity, int numberTestCaseUsedOnce) {
        if (numberTestCaseUsedOnce <= 1)
            return;

        for (TestDataExecutedEntity siblingDataExecuted : testCaseExecutedEntity.getTestDataExecutions()) {
            if ((siblingDataExecuted.getType() == TestDataCombinationType.ONE)
                    && (siblingDataExecuted.getRowIndexes().length > numberTestCaseUsedOnce)) {

                int[] newRowIndexs = ArrayUtils.remove(siblingDataExecuted.getRowIndexes(), numberTestCaseUsedOnce);

                siblingDataExecuted.setRowIndexes(newRowIndexs);
            }
        }
    }

    /**
     * Create new TestDataExecutedEntity that's based on the given params.
     * 
     * @param testCaseLink
     * @param testDataLink
     * @param testData
     */
    private static TestDataExecutedEntity getTestDataExecutedEntity(TestSuiteTestCaseLink testCaseLink,
            TestCaseTestDataLink testDataLink, TestData testData) {
        TestDataExecutedEntity testDataExecutedEntity = new TestDataExecutedEntity(testDataLink.getId(),
                testDataLink.getTestDataId());
        testDataExecutedEntity.setType(testDataLink.getCombinationType());

        int rowCount = 0;

        switch (testDataLink.getIterationEntity().getIterationType()) {
        case ALL: {
            rowCount = testData.getRowNumbers();

            if (rowCount <= 0) {
                throw new IllegalArgumentException(MessageFormat.format(
                        StringConstants.UTIL_EXC_TD_X_DOES_NOT_CONTAIN_ANY_RECORDS, testDataLink.getTestDataId()));
            }

            int[] rowIndexes = new int[rowCount];
            for (int index = 0; index < rowCount; index++) {
                rowIndexes[index] = index + 1;
            }
            testDataExecutedEntity.setRowIndexes(rowIndexes);

            break;
        }
        case RANGE: {
            int rowStart = testDataLink.getIterationEntity().getFrom();
            int rowEnd = testDataLink.getIterationEntity().getTo();

            if (rowStart > testData.getRowNumbers()) {
                throw new IllegalArgumentException(MessageFormat.format(
                        StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_START_AT_ROW_IDX,
                        testDataLink.getTestDataId(), Integer.toString(testData.getRowNumbers()),
                        testCaseLink.getTestCaseId(), Integer.toString(rowStart)));
            }

            if (rowEnd > testData.getRowNumbers()) {
                throw new IllegalArgumentException(MessageFormat.format(
                        StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_ENDS_AT_ROW_IDX,
                        testDataLink.getTestDataId(), Integer.toString(testData.getRowNumbers()),
                        testCaseLink.getTestCaseId(), Integer.toString(rowEnd)));
            }
            rowCount = rowEnd - rowStart + 1;

            int[] rowIndexes = new int[rowCount];
            for (int index = 0; index < rowCount; index++) {
                rowIndexes[index] = index + rowStart;
            }
            testDataExecutedEntity.setRowIndexes(rowIndexes);

            break;
        }
        case SPECIFIC:
            String[] rowIndexesString = testDataLink.getIterationEntity().getValue().replace(" ", "").split(",");
            rowCount = rowIndexesString.length;

            List<Integer> rowIndexArray = new ArrayList<Integer>();
            for (int index = 0; index < rowCount; index++) {
                if (rowIndexesString[index].isEmpty()) {
                    continue;
                }
                if (rowIndexesString[index].contains("-")) {
                    int rowStart = Integer.valueOf(rowIndexesString[index].split("-")[0]);
                    int rowEnd = Integer.valueOf(rowIndexesString[index].split("-")[1]);

                    if (rowStart > testData.getRowNumbers()) {
                        throw new IllegalArgumentException(MessageFormat.format(
                                StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_START_AT_ROW_IDX,
                                testDataLink.getTestDataId(), Integer.toString(testData.getRowNumbers()),
                                testCaseLink.getTestCaseId(), Integer.toString(rowStart)));
                    }

                    if (rowEnd > testData.getRowNumbers()) {
                        throw new IllegalArgumentException(MessageFormat.format(
                                StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_ENDS_AT_ROW_IDX,
                                testDataLink.getTestDataId(), Integer.toString(testData.getRowNumbers()),
                                testCaseLink.getTestCaseId(), Integer.toString(rowEnd)));
                    }
                    for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++) {
                        rowIndexArray.add(rowIndex);
                    }

                } else {
                    int rowIndex = Integer.valueOf(rowIndexesString[index]);

                    if (rowIndex < 1 || rowIndex > testData.getRowNumbers()) {
                        throw new IllegalArgumentException(MessageFormat.format(
                                StringConstants.UTIL_EXC_IDX_X_INVALID_TC_Y_TD_Z, rowIndexesString[index],
                                testCaseLink.getTestCaseId(), testDataLink.getTestDataId()));
                    }

                    rowIndexArray.add(rowIndex);
                }
            }
            testDataExecutedEntity.setRowIndexes(ArrayUtils.toPrimitive(rowIndexArray.toArray(new Integer[rowIndexArray
                    .size()])));

            break;
        }
        if (rowCount == 0) {
            return null;
        } else {
            return testDataExecutedEntity;
        }
    }

    public static File writeRunConfigToFile(IRunConfiguration runConfig) throws IOException {
        File executionFile = new File(runConfig.getExecutionSettingFilePath());
        if (!executionFile.exists()) {
            executionFile.createNewFile();
        }
        Gson gsonObj = new Gson();
        String strJson = gsonObj.toJson(runConfig.getExecutionSettingMap());
        FileUtils.writeStringToFile(executionFile, strJson);
        return executionFile;
    }

    public static Map<?, ?> readRunConfigSettingFromFile(String executionConfigFilePath) throws IOException {
        File executionConfigFile = new File(executionConfigFilePath);
        if (!executionConfigFile.exists()) {
            return null;
        }
        Gson gsonObj = new Gson();
        try {
            String executionConfigFileContent = FileUtils.readFileToString(executionConfigFile);
            Type collectionType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> result = gsonObj.fromJson(executionConfigFileContent, collectionType);
            if (result == null || !(result.get(RunConfiguration.EXECUTION_DRIVER_PROPERTY) instanceof Map<?, ?>)) {
                return new LinkedHashMap<String, Object>();
            }
            return (Map<?, ?>) result.get(RunConfiguration.EXECUTION_DRIVER_PROPERTY);
        } catch (IOException | JsonSyntaxException exception) {
            // reading file failed or parsing json failed --> return empty map;
        }
        return null;

    }
}
