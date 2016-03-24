package com.kms.katalon.execution.util;

import java.io.File;
import java.io.IOException;
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
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestDataCombinationType;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.IExecutionSetting;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.DefaultRerunSetting;
import com.kms.katalon.execution.entity.TestCaseExecutedEntity;
import com.kms.katalon.execution.entity.TestDataExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.launcher.ILauncherResult;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ExecutionUtil {
    private static final String BIT = "bit";

    private static final String OS_ARCHITECTURE_PROPERTY = "sun.arch.data.model";

    private static final String OS_NAME_PROPERTY = "os.name";

    private static final String UNKNOW_HOST = "Unknow host";

    public static String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

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
                PreferenceConstants.ExecutionPreferenceConstants.QUALIFIER);
        String selectedRunConfiguration = store
                .getString(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_DEFAULT_CONFIGURATION);
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
                PreferenceConstants.ExecutionPreferenceConstants.QUALIFIER);
        return store.getInt(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_DEFAULT_TIMEOUT);
    }

    public static boolean openReportAfterExecuting() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstants.QUALIFIER);
        return store.getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_OPEN_REPORT_AFTER_EXECUTING);
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
    public static TestSuiteExecutedEntity loadTestDataForTestSuite(TestSuiteEntity testSuite, ProjectEntity project)
            throws Exception {
        TestSuiteExecutedEntity testSuiteExecutedEntity = new TestSuiteExecutedEntity(testSuite);

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
                    DataFileEntity dataFile = TestDataController.getInstance().getTestDataByDisplayId(
                            testDataLink.getTestDataId());

                    TestData testData = testDataUsedMap.get(testDataLink.getTestDataId());

                    if (testData == null) {
                        testData = TestDataFactory.findTestDataForExternalBundleCaller(testDataLink.getTestDataId(),
                                projectDir);
                        testDataUsedMap.put(testDataLink.getTestDataId(), testData);
                    }

                    if (testData == null
                            || dataFile == null
                            || ((dataFile.isContainsHeaders() ? testData.getRowNumbers() - 1 : testData.getRowNumbers()) < 0)) {
                        throw new IllegalArgumentException(MessageFormat.format(
                                StringConstants.UTIL_EXC_TD_DATA_SRC_X_UNAVAILABLE, testDataLink.getTestDataId()));
                    } else {
                        TestDataExecutedEntity testDataExecutedEntity = getTestDataExecutedEntity(testCaseLink,
                                testDataLink, testData);
                        if (testDataExecutedEntity == null) {
                            continue;
                        }

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
     * Make sure all TestDataExecutedEntity in the given <code>testCaseExecutedEntity</code> has the same rows with the
     * given <code>numberTestCaseUsedOnce</code>
     * 
     * @param testCaseExecutedEntity
     * @see {@link TestCaseExecutedEntity}
     * @param numberTestCaseUsedOnce
     */
    private static void cutRedundantIndexes(TestCaseExecutedEntity testCaseExecutedEntity, int numberTestCaseUsedOnce) {
        if (numberTestCaseUsedOnce <= 1) {
            return;
        }

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
     * @throws Exception
     */
    private static TestDataExecutedEntity getTestDataExecutedEntity(TestSuiteTestCaseLink testCaseLink,
            TestCaseTestDataLink testDataLink, TestData testData) throws Exception {

        DataFileEntity dataFile = TestDataController.getInstance().getTestDataByDisplayId(testDataLink.getTestDataId());

        TestDataExecutedEntity testDataExecutedEntity = new TestDataExecutedEntity(testDataLink.getId(),
                testDataLink.getTestDataId());
        testDataExecutedEntity.setType(testDataLink.getCombinationType());

        int rowCount = 0;
        int totalRowCount = (dataFile.isContainsHeaders() ? testData.getRowNumbers() - 1 : testData.getRowNumbers());

        switch (testDataLink.getIterationEntity().getIterationType()) {
        case ALL: {
            rowCount = (dataFile.isContainsHeaders() ? testData.getRowNumbers() - 1 : testData.getRowNumbers());

            if (rowCount <= 0) {
                throw new IllegalArgumentException(MessageFormat.format(
                        StringConstants.UTIL_EXC_TD_X_DOES_NOT_CONTAIN_ANY_RECORDS, testDataLink.getTestDataId()));
            }

            int[] rowIndexes = new int[rowCount];
            for (int index = 0; index < rowCount; index++) {
                rowIndexes[index] = index + (dataFile.isContainsHeaders() ? 1 : 0);
            }
            testDataExecutedEntity.setRowIndexes(rowIndexes);

            break;
        }
        case RANGE: {
            int rowStart = testDataLink.getIterationEntity().getFrom();
            int rowEnd = testDataLink.getIterationEntity().getTo();

            if (rowStart > totalRowCount) {
                throw new IllegalArgumentException(MessageFormat.format(
                        StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_START_AT_ROW_IDX,
                        testDataLink.getTestDataId(), Integer.toString(totalRowCount), testCaseLink.getTestCaseId(),
                        Integer.toString(rowStart)));
            }

            if (rowEnd > totalRowCount) {
                throw new IllegalArgumentException(MessageFormat.format(
                        StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_ENDS_AT_ROW_IDX,
                        testDataLink.getTestDataId(), Integer.toString(totalRowCount), testCaseLink.getTestCaseId(),
                        Integer.toString(rowEnd)));
            }
            rowCount = rowEnd - rowStart + 1;

            int[] rowIndexes = new int[rowCount];
            for (int index = 0; index < rowCount; index++) {
                rowIndexes[index] = index + rowStart - (dataFile.isContainsHeaders() ? 0 : 1);
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

                    if (rowStart > totalRowCount) {
                        throw new IllegalArgumentException(MessageFormat.format(
                                StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_START_AT_ROW_IDX,
                                testDataLink.getTestDataId(), Integer.toString(totalRowCount),
                                testCaseLink.getTestCaseId(), Integer.toString(rowStart)));
                    }

                    if (rowEnd > totalRowCount) {
                        throw new IllegalArgumentException(MessageFormat.format(
                                StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_ENDS_AT_ROW_IDX,
                                testDataLink.getTestDataId(), Integer.toString(totalRowCount),
                                testCaseLink.getTestCaseId(), Integer.toString(rowEnd)));
                    }
                    for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++) {
                        if (dataFile.isContainsHeaders()) {
                            rowIndexArray.add(rowIndex);
                        } else {
                            rowIndexArray.add(rowIndex - 1);
                        }
                    }

                } else {
                    int rowIndex = Integer.valueOf(rowIndexesString[index]);

                    if (rowIndex < 1 || rowIndex > totalRowCount) {
                        throw new IllegalArgumentException(MessageFormat.format(
                                StringConstants.UTIL_EXC_IDX_X_INVALID_TC_Y_TD_Z, rowIndexesString[index],
                                testCaseLink.getTestCaseId(), testDataLink.getTestDataId()));
                    }
                    if (dataFile.isContainsHeaders()) {
                        rowIndexArray.add(rowIndex);
                    } else {
                        rowIndexArray.add(rowIndex - 1);
                    }
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

    public static Map<String, Object> getExecutionProperties(IExecutionSetting executionSetting,
            Map<String, IDriverConnector> driverConnectors) {
        Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();

        Map<String, Object> executionProperties = new LinkedHashMap<String, Object>();

        executionProperties.put(RunConfiguration.EXECUTION_GENERAL_PROPERTY, executionSetting.getGeneralProperties());

        executionProperties.put(RunConfiguration.EXECUTION_DRIVER_PROPERTY,
                getDriverExecutionProperties(driverConnectors));

        propertyMap.put(RunConfiguration.EXECUTION_PROPERTY, executionProperties);

        return propertyMap;
    }

    /**
     * Returns execution properties for drivers connector.
     * @return a {@link LinkedHashMap} that contains all driver system and preferences properties
     * </br>
     * Sample output in JSON:
     * <ul>
     * </br>{
     * </br>    "system": { 
     * </br>        "WebUI": {
     * </br>        }
     * </br>    },
     * </br>    "preferences": {
     * </br>        "WebUI": {
     * </br>        }
     * </br>    }
     * </br>}
     * </ul>
     */
    private static Map<String, Object> getDriverExecutionProperties(Map<String, IDriverConnector> driverConnectors) {
        Map<String, Object> driverProperties = new LinkedHashMap<String, Object>();
        Map<String, Object> driverSystemProperties = new HashMap<String, Object>();

        Map<String, Object> driverPerferencesProperties = new HashMap<String, Object>();

        for (Entry<String, IDriverConnector> kwDriverConnector : driverConnectors.entrySet()) {
            if (kwDriverConnector == null) {
                continue;
            }
            driverSystemProperties.put(kwDriverConnector.getKey(), kwDriverConnector.getValue().getSystemProperties());
            driverPerferencesProperties.put(kwDriverConnector.getKey(), kwDriverConnector.getValue()
                    .getUserConfigProperties());
        }

        driverProperties.put(RunConfiguration.EXECUTION_SYSTEM_PROPERTY, driverSystemProperties);
        driverProperties.put(RunConfiguration.EXECUTION_PREFS_PROPERTY, driverPerferencesProperties);

        return driverProperties;
    }

    public static File writeRunConfigToFile(IRunConfiguration runConfig) throws IOException {
        IExecutionSetting setting = runConfig.getExecutionSetting();
        File executionFile = new File(setting.getSettingFilePath());
        if (!executionFile.exists()) {
            executionFile.createNewFile();
        }
        Gson gsonObj = new Gson();
        String strJson = gsonObj.toJson(setting.getGeneralProperties());
        FileUtils.writeStringToFile(executionFile, strJson);
        return executionFile;
    }

    public static Map<String, Object> readRunConfigSettingFromFile(String executionConfigFilePath) throws IOException {
        RunConfiguration.setExecutionSettingFile(executionConfigFilePath);
        Map<String, Object> executionProps = new LinkedHashMap<String, Object>();

        Map<String, Object> generalProps = RunConfiguration.getExecutionGeneralProperties();
        if (generalProps != null && !generalProps.values().isEmpty()) {
            executionProps.putAll(RunConfiguration.getExecutionGeneralProperties());
        }
        
        Map<String, Object> preferenceProps = RunConfiguration.getDriverPreferencesProperties();
        if (preferenceProps != null && !preferenceProps.values().isEmpty()) {
            executionProps.putAll(RunConfiguration.getDriverPreferencesProperties());
        }

        return executionProps;
    }

    public static TestSuiteExecutedEntity getRerunExecutedEntity(TestSuiteExecutedEntity prevExecuted,
            ILauncherResult prevResult) {
        TestSuiteEntity testSuite = null;
        try {
            testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(prevExecuted.getSourceId(),
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            // TODO need to log here
        }

        DefaultRerunSetting rerunSetting = new DefaultRerunSetting(prevExecuted.getPreviousRerunTimes() + 1,
                prevExecuted.getRemainingRerunTimes() - 1, prevExecuted.isRerunFailedTestCasesOnly());

        TestSuiteExecutedEntity newExecutedEntity = new TestSuiteExecutedEntity(testSuite, rerunSetting);
        newExecutedEntity.setReportLocation(prevExecuted.getReportLocationSetting());
        newExecutedEntity.setTestDataMap(prevExecuted.getTestDataMap());

        List<TestCaseExecutedEntity> prevTestCaseExecutedEntities = prevExecuted.getTestCaseExecutedEntities();

        List<TestCaseExecutedEntity> newTestCaseExecutedEntities = new ArrayList<TestCaseExecutedEntity>();
        if (prevExecuted.isRerunFailedTestCasesOnly()) {
            TestStatusValue[] prevResultValues = prevResult.getResultValues();
            int rsIdx = 0;

            for (TestCaseExecutedEntity prevExecutedTC : prevTestCaseExecutedEntities) {
                for (int i = rsIdx; i < rsIdx + prevExecutedTC.getLoopTimes(); i++) {
                    if (prevResultValues[i] == TestStatusValue.FAILED || prevResultValues[i] == TestStatusValue.ERROR) {
                        newTestCaseExecutedEntities.add(prevExecutedTC);
                        break;
                    }
                }
                rsIdx += prevExecutedTC.getLoopTimes();
            }

            newExecutedEntity.setTestCaseExecutedEntities(newTestCaseExecutedEntities);
        } else {
            newExecutedEntity.setTestCaseExecutedEntities(prevTestCaseExecutedEntities);
        }

        return newExecutedEntity;
    }
}
