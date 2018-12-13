package com.kms.katalon.tracking.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.application.utils.FileUtil;
import com.kms.katalon.composer.integration.slack.util.SlackUtil;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.TestListenerEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.integration.jira.setting.JiraIntegrationSettingStore;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.GitToolbarExecutableStatus;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.tracking.model.ProjectStatistics;
import com.kms.katalon.tracking.osgi.service.IProjectStatisticsCollector;

public class ProjectStatisticsCollector implements IProjectStatisticsCollector {
    
    private static final String TEST_CASE_SCRIPT_FOLDER = "Scripts";
    
    private static final String GROOVY_FILE_EXTENSION = "groovy";
    
    private static final String EXECUTION_PROPERTIES_FILE_EXTENSION = "properties";
    
    private ProjectEntity project;
    
    private FolderController folderController = FolderController.getInstance();
    
    private ProjectStatistics statistics;
    
    public ProjectStatistics collect(ProjectEntity project) throws Exception {
        
        this.project = project;
        
        statistics = new ProjectStatistics();
        
        statistics.setProjectId(project.getUUID());
        
        countTestCasesAndJiraIntegratedTestCases();
        
        countTestSteps();
        
        countTestObjects();
        
        countTestSuitesAndTestCasesInTestSuitesAndTestSuiteCollections();
        
        countTestData();
        
        countCheckpoints();
        
        countCustomKeywords();
       
        countTestListeners();
        
        countReportsAndTestExecutions();
        
        countProfiles();
        
        countFeatureFiles();
        
        countGroovyScriptFiles();
        
        statistics.setGitIntegrated(isGitIntegrated());
        
        statistics.setJiraIntegrated(isJiraIntegrated());
        
        statistics.setKobitonIntegrated(isKobitonIntegrated());
        
        statistics.setqTestIntegrated(isqTestIntegrated());
        
        statistics.setSlackIntegrated(isSlackIntegrated());
        
        statistics.setKatalonAnalyticsIntegrated(isKatalonAnalyticsIntegrated());
        
        statistics.setRemoteWebDriverConfigured(isRemoteWebDriverConfigured());
        
        statistics.setContinueOnFailure(isAutoApplyNeighborXpathsEnabled());
        
        statistics.setWebLocatorConfig(getWebLocatorConfig());
        
        return statistics;
    }
    
    private void countTestCasesAndJiraIntegratedTestCases() throws Exception {
        int testCaseCount = 0;
        int jiraIntegratedTestCaseCount = 0;
        
        FolderEntity testCaseFolder = folderController.getTestCaseRoot(project);
        List<Object> entities = folderController.getAllDescentdantEntities(testCaseFolder);
        for (Object entity : entities) {
            if (entity instanceof TestCaseEntity) {
                testCaseCount++;
                TestCaseEntity testCase = (TestCaseEntity) entity;
                if (testCase.getIntegratedEntity("JIRA") != null) {
                    jiraIntegratedTestCaseCount++;
                }
            }
        }
        statistics.setTestCaseCount(testCaseCount);
        statistics.setJiraIntegratedTestCaseCount(jiraIntegratedTestCaseCount);
    } 
    
    private void countTestSteps() throws IOException {
        int callTestCaseTestStepCount = 0;
        int webTestStepCount = 0;
        int mobileTestStepCount = 0;
        int apiTestStepCount = 0;
        int customKeywordTestStepCount = 0;
        int totalTestStepCount = 0;
        
        String testCaseScriptFolderPath = project.getFolderLocation() + File.separator + TEST_CASE_SCRIPT_FOLDER;
        File testCaseScriptFolder = new File(testCaseScriptFolderPath);
        File[] scriptFiles = listFiles(testCaseScriptFolder, GROOVY_FILE_EXTENSION);
        for (File scriptFile : scriptFiles) {
            String script = FileUtils.readFileToString(scriptFile);
            StringTokenizer st = new StringTokenizer(script, "\n=");
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                if (token.startsWith("WebUI.callTestCase") || token.startsWith("WebUiBuiltInKeywords.callTestCase")) {
                    callTestCaseTestStepCount++;
                    totalTestStepCount++;
                } else if (token.startsWith("WebUI.") || token.startsWith("WebUiBuiltInKeywords.")) {
                    webTestStepCount++;
                    totalTestStepCount++;
                } else if (token.startsWith("Mobile.") || token.startsWith("MobileBuiltInKeywords.")) {
                    mobileTestStepCount++;
                    totalTestStepCount++;
                } else if (token.startsWith("WS.") || token.startsWith("WSBuiltInKeywords.")) {
                    apiTestStepCount++;
                    totalTestStepCount++;
                } else if (token.startsWith("CustomKeywords.")) {
                    customKeywordTestStepCount++;
                    totalTestStepCount++;
                } else {
                    if (token.startsWith("import ")
                        || token.startsWith("class ")
                        || token.startsWith("// ")
                        || token.startsWith("/** ")
                        || token.startsWith("**/ ")
                        || token.startsWith("'")
                        || token.startsWith("'''")) {
                        continue;
                    } else {
                        totalTestStepCount++;
                    }
                }
            
            }
        }
        
        statistics.setCallTestCaseTestStepCount(callTestCaseTestStepCount);
        statistics.setWebTestStepCount(webTestStepCount);
        statistics.setMobileTestStepCount(mobileTestStepCount);
        statistics.setApiTestStepCount(apiTestStepCount);
        statistics.setCustomKeywordTestStepCount(customKeywordTestStepCount);
        statistics.setTotalTestStepCount(totalTestStepCount);
    }

    private void countTestObjects() throws Exception {
        int apiTestObjectCount = 0;
        int webTestObjectCount = 0;
        
        FolderEntity testObjectFolder = folderController.getObjectRepositoryRoot(project);
        List<Object> entities = folderController.getAllDescentdantEntities(testObjectFolder);
        for (Object entity : entities) {
            if (entity instanceof WebServiceRequestEntity) {
                apiTestObjectCount++;
            } else if (entity instanceof WebElementEntity) {
                webTestObjectCount++;
            }
        }
        
        statistics.setApiTestObjectCount(apiTestObjectCount);
        statistics.setWebTestObjectCount(webTestObjectCount);
    }
    
    private void countTestSuitesAndTestCasesInTestSuitesAndTestSuiteCollections() throws Exception {
        int testSuiteCount = 0;
        int testCaseInTestSuiteCount = 0;
        int testSuiteCollectionCount = 0;
        
        FolderEntity testSuiteFolder = folderController.getTestSuiteRoot(project);
        List<Object> entities = folderController.getAllDescentdantEntities(testSuiteFolder);
        for (Object entity : entities) {
            if (entity instanceof TestSuiteEntity) {
                testSuiteCount++;
                testCaseInTestSuiteCount += ((TestSuiteEntity) entity).getTestSuiteTestCaseLinks().size();
            } else if (entity instanceof TestSuiteCollectionEntity) {
                testSuiteCollectionCount++;
            }
        }
        
        statistics.setTestSuiteCount(testSuiteCount);
        statistics.setTestCaseInTestSuiteCount(testCaseInTestSuiteCount);
        statistics.setTestSuiteCollectionCount(testSuiteCollectionCount);
    }
    
    private void countTestData() throws Exception {
        int csvDataFileCount = 0;
        int excelDataFileCount = 0;
        int databaseDataFileCount = 0;
        int internalDataFileCount = 0;
        
        FolderEntity testDataRootFolder = folderController.getTestDataRoot(project);
        List<Object> entities = folderController.getAllDescentdantEntities(testDataRootFolder);
        for (Object entity : entities) {
            if (entity instanceof DataFileEntity) {
                DataFileEntity dataFileEntity = (DataFileEntity) entity;
                switch (dataFileEntity.getDriver()) {
                    case CSV:
                        csvDataFileCount++;
                        break;
                    case ExcelFile:
                        excelDataFileCount++;
                        break;
                    case DBData:
                        databaseDataFileCount++;
                        break;
                    case InternalData:
                        internalDataFileCount++;
                        break;
                    default:
                        break;
                }
            }
        }
        
        statistics.setCsvDataFileCount(csvDataFileCount);
        statistics.setExcelDataFileCount(excelDataFileCount);
        statistics.setDatabaseDataFileCount(databaseDataFileCount);
        statistics.setInternalDataFileCount(internalDataFileCount);
    }
    
    private void countCheckpoints() throws Exception {
        String checkpointFolderPath = folderController.getCheckpointRoot(project).getLocation();
        File checkpointFolder = new File(checkpointFolderPath);
        File[] checkpointFiles = listFiles(checkpointFolder, CheckpointEntity.getCheckpointFileExtension());
        int checkpointCount = checkpointFiles.length;
        statistics.setCheckpointCount(checkpointCount);
    }
    
    private void countCustomKeywords() throws Exception {
        String keywordFolderPath = folderController.getKeywordRoot(project).getLocation();
        File keywordFolder = new File(keywordFolderPath);
        File[] keywordFiles = listFiles(keywordFolder, GROOVY_FILE_EXTENSION);
        int customKeywordCount = keywordFiles.length;
        statistics.setCustomKeywordCount(customKeywordCount);
    }
    
    private void countTestListeners() throws Exception {
        String testListenerFolderPath = folderController.getTestListenerRoot(project).getLocation();
        File testListenerFolder = new File(testListenerFolderPath);
        File[] testListenerFiles = listFiles(testListenerFolder, TestListenerEntity.FILE_EXTENSION);
        int testListenerCount = testListenerFiles.length;
        statistics.setTestListenerCount(testListenerCount);
    }
    
    private void countReportsAndTestExecutions() throws Exception {
        String reportFolderPath = folderController.getReportRoot(project).getLocation();
        File reportFolder = new File(reportFolderPath);
        File[] junitReportFiles = listFiles(reportFolder, ReportEntity.JUNIT_REPORT_NAME);
        statistics.setReportCount(junitReportFiles.length);
        
        int testExecutionCount = 0;
        for (File reportFile : junitReportFiles) {
            String reportContent = FileUtils.readFileToString(reportFile);
            testExecutionCount += StringUtils.countMatches(reportContent, "<testcase"); //count test case tags
        }
        statistics.setExecutionCount(testExecutionCount);
    }
    
    private void countProfiles() throws DALException {
        String profileFolderPath = folderController.getProfileRoot(project).getLocation();
        File profileFolder = new File(profileFolderPath);
        File[] profileFiles = listFiles(profileFolder, ExecutionProfileEntity.getGlobalVariableFileExtension());
        int profileCount = profileFiles.length;
        statistics.setProfileCount(profileCount);
    }
    
    private void countFeatureFiles() throws DALException {
        String featureFolderPath =  folderController.getIncludeRoot(project).getLocation();
        File featureFolder = new File(featureFolderPath);
        File[] featureFiles = listFiles(featureFolder, ".feature");
        int featureFileCount = featureFiles.length;
        statistics.setFeatureFileCount(featureFileCount);
    }
    
    private void countGroovyScriptFiles() throws DALException {
        String scriptFolderPath = folderController.getIncludeRoot(project).getLocation() + File.separator + "scripts";
        File scriptFolder = new File(scriptFolderPath);
        File[] scriptFiles = listFiles(scriptFolder, GROOVY_FILE_EXTENSION);
        int scriptFileCount = scriptFiles.length;
        statistics.setGroovyScriptFileCount(scriptFileCount);
    }
    
    private boolean isGitIntegrated() {
        return GitToolbarExecutableStatus.getValue();
    }
    
    private boolean isJiraIntegrated() throws IOException {
        JiraIntegrationSettingStore jiraIntegrationSettingStore = 
                new JiraIntegrationSettingStore(project.getFolderLocation());
        return jiraIntegrationSettingStore.isIntegrationEnabled();
    }
    
    private boolean isKobitonIntegrated() {
        return KobitonPreferencesProvider.isKobitonIntegrationEnabled();
    }
    
    private boolean isqTestIntegrated() {
        try {
            return new BundleSettingStore(project.getFolderLocation(), "com.kms.katalon.integration.qtest", false).getBoolean("enableIntegration", false);
        } catch (IOException e) {
            LogUtil.logError(e);
            return false;
        }
    }
    
    private boolean isSlackIntegrated() {
        return SlackUtil.getInstance().isSlackEnabled();
    }
    
    private String getWebLocatorConfig() throws IOException {
        WebUiExecutionSettingStore store = WebUiExecutionSettingStore.getStore();
        String ret =  store.getCapturedTestObjectSelectorMethod().toString();
        return ret;
    }
    
    private boolean isKatalonAnalyticsIntegrated() throws IOException {
        AnalyticsSettingStore store = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
        return store.isIntegrationEnabled();
    }
    private boolean isRemoteWebDriverConfigured() throws IOException {
        RemoteWebDriverConnector driverConnector = new RemoteWebDriverConnector(project.getFolderLocation() 
                + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME);
        Map<String, Object> driverProperties = driverConnector.getUserConfigProperties();
        return (!StringUtils.isBlank(driverConnector.getRemoteServerUrl()) 
                || (driverProperties != null && !driverProperties.isEmpty()));
    }
    
    private String getDefaultTestCaseView() {
        ScopedPreferenceStore testCasePrefStore = PreferenceStoreManager.getPreferenceStore("com.kms.katalon.composer.testcase");
        return testCasePrefStore.getString("default.startView");
    }
    
    private File[] listFiles(File folder, String extension) {
        try {
            return FileUtil.getFiles(folder, extension, null);
        } catch (IOException e) {
            LogUtil.logError(e);
            return new File[0];
        }
    }
    
    private boolean isAutoApplyNeighborXpathsEnabled() {
        ExecutionDefaultSettingStore store = ExecutionDefaultSettingStore.getStore();
        return store.getAutoApplyNeighborXpathsEnabled();
    }
}
