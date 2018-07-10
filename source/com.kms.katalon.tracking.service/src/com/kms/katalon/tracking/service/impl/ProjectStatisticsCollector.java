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
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.integration.jira.setting.JiraIntegrationSettingStore;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.GitToolbarExecutableStatus;
import com.kms.katalon.tracking.model.ProjectStatistics;
import com.kms.katalon.tracking.osgi.service.IProjectStatisticsCollector;

public class ProjectStatisticsCollector implements IProjectStatisticsCollector {
    
    private static final String SCRIPT_FOLDER = "Scripts";
    
    private static final String GROOVY_FILE_EXTENSION = "groovy";
    
    private static final String EXECUTION_PROPERTIES_FILE_EXTENSION = "properties";
    
    private ProjectEntity project;
    
    private FolderController folderController = FolderController.getInstance();
    
    private ProjectStatistics statistics;
    
    public ProjectStatistics collect(ProjectEntity project) throws Exception {
        this.project = project;
        
        statistics = new ProjectStatistics();
        
        statistics.setProjectId(project.getUUID());
        
        countTestCases();
        
        countTestSteps();
        
        countTestObjects();
        
        countTestSuitesAndTestCasesInTestSuitesAndTestSuiteCollections();
        
        countTestData();
        
        countCheckpoints();
        
        countCustomKeywords();
       
        countTestListeners();
        
        countReportsAndTestExecutions();
        
        countProfiles();
        
        statistics.setGitIntegrated(isGitIntegrated());
        
        statistics.setJiraIntegrated(isJiraIntegrated());
        
        statistics.setKobitonIntegrated(isKobitonIntegrated());
        
        statistics.setqTestIntegrated(isqTestIntegrated());
        
        statistics.setSlackIntegrated(isSlackIntegrated());
        
        statistics.setRemoteWebDriverConfigured(isRemoteWebDriverConfigured());
        
        return statistics;
    }
    
    private void countTestCases() throws Exception {
        String testCaseFolderPath = folderController.getTestCaseRoot(project).getLocation();
        File testCaseFolder = new File(testCaseFolderPath);
        File[] testCaseFiles = listFiles(testCaseFolder, TestCaseEntity.getTestCaseFileExtension());
        int testCaseCount = testCaseFiles.length;
        statistics.setTestCaseCount(testCaseCount);
    }
    
    private void countTestSteps() throws IOException {
        int callTestCaseTestStepCount = 0;
        int webTestStepCount = 0;
        int mobileTestStepCount = 0;
        int apiTestStepCount = 0;
        int customKeywordTestStepCount = 0;
        int totalTestStepCount = 0;
        
        String scriptFolderPath = project.getFolderLocation() + File.separator + SCRIPT_FOLDER;
        File scriptFolder = new File(scriptFolderPath);
        File[] scriptFiles = listFiles(scriptFolder, GROOVY_FILE_EXTENSION);
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
//                else if (token.startsWith("if ")
//                        || token.contains("else if")
//                        || token.contains(" else ")
//                        || token.startsWith("switch ")
//                        || token.startsWith("case ")
//                        || token.startsWith("default")
//                        || token.startsWith("for ")
//                        || token.startsWith("while ")
//                        || token.startsWith("break")
//                        || token.startsWith("continue")
//                        || token.startsWith("return")
//                        || token.startsWith("try ")
//                        || token.startsWith("catch ")
//                        || token.startsWith("finally")
//                        || token.startsWith("throw")
//                        || token.contains ("==")
//                        || token.contains(">")
//                        || token.contains(">=")
//                        || token.contains("<")
//                        || token.contains("<=")
//                        || token.startsWith("assert "))
//                        || token.matches("(\.[\s\n\r]*[\w]+)[\s\n\r]*(?=\(.*\))"){
//                    
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
    
//    public void countTestSteps1() throws IOException, GroovyParsingException {
//        int callTestCaseTestStepCount = 0;
//        int webTestStepCount = 0;
//        int mobileTestStepCount = 0;
//        int apiTestStepCount = 0;
//        int customKeywordTestStepCount = 0;
//        int totalTestStepCount = 0;
//        
//        String scriptFolderPath = project.getFolderLocation() + File.separator + SCRIPT_FOLDER;
//        File scriptFolder = new File(scriptFolderPath);
//        File[] scriptFiles = listFiles(scriptFolder, GROOVY_FILE_EXTENSION);
//        for (File scriptFile : scriptFiles) {
//            String script = FileUtils.readFileToString(scriptFile);
//            countTestStepsInTestScript(script);
//        }
//    }
//    
//    private void countTestStepsInTestScript(String script) throws GroovyParsingException {
//        GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(script);
//    }

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
//        File[] executionPropertiesFiles = listFiles(reportFolder, EXECUTION_PROPERTIES_FILE_EXTENSION);
//        int reportCount = executionPropertiesFiles.length;
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
        return QTestSettingStore.isIntegrationActive(project.getFolderLocation());
    }
    
    private boolean isSlackIntegrated() {
        return SlackUtil.getInstance().isSlackEnabled();
    }
    
    private boolean isRemoteWebDriverConfigured() throws IOException {
        RemoteWebDriverConnector driverConnector = new RemoteWebDriverConnector(project.getFolderLocation() 
                + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME);
        Map<String, Object> driverProperties = driverConnector.getUserConfigProperties();
        return (!StringUtils.isBlank(driverConnector.getRemoteServerUrl()) 
                || (driverProperties != null && !driverProperties.isEmpty()));
    }
    
    private File[] listFiles(File folder, String extension) {
        try {
            return FileUtil.getFiles(folder, extension, null);
        } catch (IOException e) {
            LogUtil.logError(e);
            return new File[0];
        }
    }
    
//    public class ProjectStatistics {
//        
//        private String projectId;
//        
//        private int testCaseCount = 0;
//        
//        private int webTestStepCount = 0;
//        
//        private int apiTestStepCount = 0;
//        
//        private int mobileTestStepCount = 0;
//        
//        private int customKeywordTestStepCount = 0;
//        
//        private int callTestCaseTestStepCount = 0;
//        
//        private int totalTestStepCount = 0;
//        
//        private int webTestObjectCount = 0;
//        
//        private int apiTestObjectCount = 0;
//        
//        private int testSuiteCount = 0;
//        
//        private int testCaseInTestSuiteCount = 0;
//        
//        private int testSuiteCollectionCount = 0;
//        
//        private int csvDataFileCount = 0;
//        
//        private int excelDataFileCount = 0;
//        
//        private int databaseDataFileCount = 0;
//        
//        private int internalDataFileCount = 0;
//        
//        private int checkpointCount = 0;
//
//        private int customKeywordCount = 0;
//
//        private int testListenerCount = 0;
//        
//        private int reportCount = 0;
//        
//        private int executionCount = 0;
//
//        private int profileCount = 0;
//        
//        private boolean gitIntegrated = false;
//        
//        private boolean jiraIntegrated = false;
//        
//        private boolean kobitonIntegrated = false;
//        
//        private boolean slackIntegrated = false;
//        
//        private boolean qTestIntegrated = false;
//        
//        private boolean remoteWebDriverConfigured = false;
//
//        public String getProjectId() {
//            return projectId;
//        }
//
//        public void setProjectId(String projectId) {
//            this.projectId = projectId;
//        }
//
//        public int getTestCaseCount() {
//            return testCaseCount;
//        }
//
//        public void setTestCaseCount(int testCaseCount) {
//            this.testCaseCount = testCaseCount;
//        }
//
//        public int getWebTestStepCount() {
//            return webTestStepCount;
//        }
//
//        public void setWebTestStepCount(int webTestStepCount) {
//            this.webTestStepCount = webTestStepCount;
//        }
//
//        public int getApiTestStepCount() {
//            return apiTestStepCount;
//        }
//
//        public void setApiTestStepCount(int apiTestStepCount) {
//            this.apiTestStepCount = apiTestStepCount;
//        }
//
//        public int getMobileTestStepCount() {
//            return mobileTestStepCount;
//        }
//
//        public void setMobileTestStepCount(int mobileTestStepCount) {
//            this.mobileTestStepCount = mobileTestStepCount;
//        }
//
//        public int getCustomKeywordTestStepCount() {
//            return customKeywordTestStepCount;
//        }
//
//        public void setCustomKeywordTestStepCount(int customKeywordTestStepCount) {
//            this.customKeywordTestStepCount = customKeywordTestStepCount;
//        }
//
//        public int getCallTestCaseTestStepCount() {
//            return callTestCaseTestStepCount;
//        }
//
//        public void setCallTestCaseTestStepCount(int callTestCaseTestStepCount) {
//            this.callTestCaseTestStepCount = callTestCaseTestStepCount;
//        }
//        
//        public int getTotalTestStepCount() {
//            return totalTestStepCount;
//        }
//
//        public void setTotalTestStepCount(int totalTestStepCount) {
//            this.totalTestStepCount = totalTestStepCount;
//        }
//
//        public int getWebTestObjectCount() {
//            return webTestObjectCount;
//        }
//
//        public void setWebTestObjectCount(int webTestObjectCount) {
//            this.webTestObjectCount = webTestObjectCount;
//        }
//
//        public int getApiTestObjectCount() {
//            return apiTestObjectCount;
//        }
//
//        public void setApiTestObjectCount(int apiTestObjectCount) {
//            this.apiTestObjectCount = apiTestObjectCount;
//        }
//
//        public int getTestSuiteCount() {
//            return testSuiteCount;
//        }
//
//        public void setTestSuiteCount(int testSuiteCount) {
//            this.testSuiteCount = testSuiteCount;
//        }
//
//        public int getTestCaseInTestSuiteCount() {
//            return testCaseInTestSuiteCount;
//        }
//
//        public void setTestCaseInTestSuiteCount(int testCaseInTestSuiteCount) {
//            this.testCaseInTestSuiteCount = testCaseInTestSuiteCount;
//        }
//        
//        public int getTestSuiteCollectionCount() {
//            return testSuiteCollectionCount;
//        }
//
//        public void setTestSuiteCollectionCount(int testSuiteCollectionCount) {
//            this.testSuiteCollectionCount = testSuiteCollectionCount;
//        }
//
//        public int getCsvDataFileCount() {
//            return csvDataFileCount;
//        }
//
//        public void setCsvDataFileCount(int csvDataFileCount) {
//            this.csvDataFileCount = csvDataFileCount;
//        }
//
//        public int getExcelDataFileCount() {
//            return excelDataFileCount;
//        }
//
//        public void setExcelDataFileCount(int excelDataFileCount) {
//            this.excelDataFileCount = excelDataFileCount;
//        }
//
//        public int getDatabaseDataFileCount() {
//            return databaseDataFileCount;
//        }
//
//        public void setDatabaseDataFileCount(int databaseDataFileCount) {
//            this.databaseDataFileCount = databaseDataFileCount;
//        }
//
//        public int getInternalDataFileCount() {
//            return internalDataFileCount;
//        }
//
//        public void setInternalDataFileCount(int internalDataFileCount) {
//            this.internalDataFileCount = internalDataFileCount;
//        }
//
//        public int getCheckpointCount() {
//            return checkpointCount;
//        }
//
//        public void setCheckpointCount(int checkpointCount) {
//            this.checkpointCount = checkpointCount;
//        }
//
//        public int getCustomKeywordCount() {
//            return customKeywordCount;
//        }
//
//        public void setCustomKeywordCount(int customKeywordCount) {
//            this.customKeywordCount = customKeywordCount;
//        }
//
//        public int getTestListenerCount() {
//            return testListenerCount;
//        }
//
//        public void setTestListenerCount(int testListenerCount) {
//            this.testListenerCount = testListenerCount;
//        }
//
//        public int getReportCount() {
//            return reportCount;
//        }
//
//        public void setReportCount(int reportCount) {
//            this.reportCount = reportCount;
//        }
//
//        public int getExecutionCount() {
//            return executionCount;
//        }
//
//        public void setExecutionCount(int executionCount) {
//            this.executionCount = executionCount;
//        }
//
//        public int getProfileCount() {
//            return profileCount;
//        }
//
//        public void setProfileCount(int profileCount) {
//            this.profileCount = profileCount;
//        }
//
//        public boolean isGitIntegrated() {
//            return gitIntegrated;
//        }
//
//        public void setGitIntegrated(boolean gitIntegrated) {
//            this.gitIntegrated = gitIntegrated;
//        }
//
//        public boolean isJiraIntegrated() {
//            return jiraIntegrated;
//        }
//
//        public void setJiraIntegrated(boolean jiraIntegrated) {
//            this.jiraIntegrated = jiraIntegrated;
//        }
//
//        public boolean isKobitonIntegrated() {
//            return kobitonIntegrated;
//        }
//
//        public void setKobitonIntegrated(boolean kobitonIntegrated) {
//            this.kobitonIntegrated = kobitonIntegrated;
//        }
//
//        public boolean isSlackIntegrated() {
//            return slackIntegrated;
//        }
//
//        public void setSlackIntegrated(boolean slackIntegrated) {
//            this.slackIntegrated = slackIntegrated;
//        }
//
//        public boolean isqTestIntegrated() {
//            return qTestIntegrated;
//        }
//
//        public void setqTestIntegrated(boolean qTestIntegrated) {
//            this.qTestIntegrated = qTestIntegrated;
//        }
//
//        public boolean isRemoteWebDriverConfigured() {
//            return remoteWebDriverConfigured;
//        }
//
//        public void setRemoteWebDriverConfigured(boolean remoteWebDriverConfigured) {
//            this.remoteWebDriverConfigured = remoteWebDriverConfigured;
//        }
//    }
}
