package com.kms.katalon.execution.util;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.report.ReportTestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.IExecutionSetting;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.entity.DefaultRerunSetting;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.entity.TestCaseExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.logging.LogUtil;
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
            return StringConstants.DF_LOCAL_HOST_ADDRESS;
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

    private static ScopedPreferenceStore getStore() {
        return getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
    }

    public static IRunConfigurationContributor getDefaultExecutionConfiguration() {
        String selectedRunConfiguration = getStore()
                .getString(ExecutionPreferenceConstants.EXECUTION_DEFAULT_CONFIGURATION);
        IRunConfigurationContributor[] allBuiltinRunConfigurationContributor = RunConfigurationCollector.getInstance()
                .getAllBuiltinRunConfigurationContributors();
        for (IRunConfigurationContributor runConfigurationContributor : allBuiltinRunConfigurationContributor) {
            if (runConfigurationContributor.getId().equals(selectedRunConfiguration)) {
                return runConfigurationContributor;
            }
        }
        return null;
    }

    public static int getDefaultImplicitTimeout() {
        return getStore().getInt(ExecutionPreferenceConstants.EXECUTION_DEFAULT_TIMEOUT);
    }

    public static boolean openReportAfterExecuting() {
        return getStore().getBoolean(ExecutionPreferenceConstants.EXECUTION_OPEN_REPORT_AFTER_EXECUTING);
    }

    public static boolean isQuitDriversAfterExecutingTestCase() {
        return getStore().getBoolean(ExecutionPreferenceConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE);
    }

    public static boolean isQuitDriversAfterExecutingTestSuite() {
        return getStore().getBoolean(ExecutionPreferenceConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE);
    }

    public static Map<String, Object> escapeGroovy(Map<String, Object> propertiesMap) {
        for (Entry<String, Object> entry : propertiesMap.entrySet()) {
            if (entry.getValue() instanceof String) {
                entry.setValue(GroovyStringUtil.escapeGroovy((String) entry.getValue()));
            }
        }
        return propertiesMap;
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
     * 
     * @return a {@link LinkedHashMap} that contains all driver system and preferences properties
     * 
     * <pre>
     * Sample output in JSON:
     * { 
     *      "system": {
     *          "WebUI": {}
     *          }, 
     *      "preferences": { 
     *          "WebUI": {} 
     *          }
     * }
     * </pre>
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
            driverPerferencesProperties.put(kwDriverConnector.getKey(),
                    kwDriverConnector.getValue().getUserConfigProperties());
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
            ILauncherResult prevResult) throws IOException, Exception {
        TestSuiteEntity testSuite = null;
        try {
            testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(prevExecuted.getSourceId(),
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LogUtil.logError(e);
            return null;
        }

        DefaultRerunSetting rerunSetting = new DefaultRerunSetting(prevExecuted.getPreviousRerunTimes() + 1,
                prevExecuted.getRemainingRerunTimes() - 1, prevExecuted.isRerunFailedTestCasesOnly());

        TestSuiteExecutedEntity newExecutedEntity = new TestSuiteExecutedEntity(testSuite, rerunSetting);
        newExecutedEntity.setReportLocation(prevExecuted.getReportLocationSetting());
        newExecutedEntity.setTestDataMap(prevExecuted.getTestDataMap());

        List<IExecutedEntity> prevTestCaseExecutedEntities = prevExecuted.getExecutedItems();

        List<IExecutedEntity> newTestCaseExecutedEntities = new ArrayList<IExecutedEntity>();
        if (prevExecuted.isRerunFailedTestCasesOnly()) {
            TestStatusValue[] prevResultValues = prevResult.getResultValues();
            int rsIdx = 0;

            for (IExecutedEntity prevExecutedItem : prevTestCaseExecutedEntities) {
                TestCaseExecutedEntity prevExecutedTC = (TestCaseExecutedEntity) prevExecutedItem;
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

    public static void savePropertiesFile(Map<String, String> propertiesMap, String fileLocation) throws IOException {
        try (OutputStream output = new FileOutputStream(fileLocation)) {
            Properties prop = new Properties() {
                private static final long serialVersionUID = 1L;

                // Sort properties in alphabetical order
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
                }

            };
            for (Entry<String, String> propertyEntry : propertiesMap.entrySet()) {
                // set the properties value
                prop.setProperty(propertyEntry.getKey(), propertyEntry.getValue());
            }
            // save properties
            prop.store(output, null);
        }
    }

    public static ReportEntity newReportEntity(String id, TestSuiteExecutedEntity executedEntity) {
        try {
            TestSuiteEntity testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(executedEntity.getSourceId(),
                    ProjectController.getInstance().getCurrentProject());
            ReportEntity report = ReportController.getInstance().getReportEntity(testSuite, id);
            
            List<ReportTestCaseEntity> reportTestCases = new ArrayList<>();
            executedEntity.getExecutedItems().forEach(item -> {
                TestCaseExecutedEntity testCaseExecuted = (TestCaseExecutedEntity) item;
                reportTestCases.addAll(testCaseExecuted.reportTestCases());
            });
            report.setReportTestCases(reportTestCases);
            return report;
        } catch (Exception e) {
            return null;
        }

    }
}
