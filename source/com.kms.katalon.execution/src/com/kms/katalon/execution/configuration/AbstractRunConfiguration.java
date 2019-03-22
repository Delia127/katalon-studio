package com.kms.katalon.execution.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.util.LogbackUtil;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.configuration.impl.LocalHostConfiguration;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.generator.FeatureFileScriptGenerator;
import com.kms.katalon.execution.generator.TestCaseScriptGenerator;
import com.kms.katalon.execution.generator.TestSuiteScriptGenerator;
import com.kms.katalon.execution.session.ExecutionSessionSocketServer;
import com.kms.katalon.execution.util.ExecutionUtil;

public abstract class AbstractRunConfiguration implements IRunConfiguration {

    protected IHostConfiguration hostConfiguration;

    protected DefaultExecutionSetting executionSetting;

    private ExecutionProfileEntity executionProfile;
    
    private Map<String, Object> overridingParameters = new HashMap<>();
    
    public AbstractRunConfiguration() {
        initExecutionSetting();
    }

    @Override
    public final IExecutionSetting build(FileEntity fileEntity, IExecutedEntity executedEntity)
            throws IOException, ExecutionException {
        init(fileEntity);

        executionSetting.setExecutedEntity(executedEntity);

        hostConfiguration = new LocalHostConfiguration();

        generateLogFolder(fileEntity);

        generateExecutionProperties();

        File scriptFile = generateTempScriptFile(fileEntity);

        executionSetting.setScriptFile(scriptFile);

        return executionSetting;
    }

    protected File generateTempScriptFile(FileEntity fileEntity) throws ExecutionException {
        try {
            if (fileEntity instanceof TestSuiteEntity) {
                return new TestSuiteScriptGenerator((TestSuiteEntity) fileEntity, this,
                        (TestSuiteExecutedEntity) this.getExecutionSetting().getExecutedEntity()).generateScriptFile();
            } else if (fileEntity instanceof TestCaseEntity)  {
                return new TestCaseScriptGenerator((TestCaseEntity) fileEntity, this).generateScriptFile();
            } else if (fileEntity instanceof SystemFileEntity) {
                return new FeatureFileScriptGenerator((SystemFileEntity) fileEntity, this).generateScriptFile();
            }
            throw new ExecutionException("The execution is not supported for this file");
        } catch (Exception ex) {
            throw new ExecutionException(ex.getMessage());
        }
    }

    protected void init(FileEntity fileEntity) throws IOException {
        if (fileEntity == null) {
            return;
        }

        int timeOut = (fileEntity instanceof TestSuiteEntity
                && !((TestSuiteEntity) fileEntity).isPageLoadTimeoutDefault())
                        ? ((TestSuiteEntity) fileEntity).getPageLoadTimeout()
                        : ExecutionUtil.getDefaultImplicitTimeout();
        executionSetting.setTimeout(timeOut);
    }

    protected void initExecutionSetting() {
        executionSetting = new DefaultExecutionSetting();
    }

    protected String getTemporaryLogFolderLocation(FileEntity testCase) {
        try {
            return ReportController.getInstance().generateTemporaryExecutionFolder(testCase);
        } catch (Exception e) {
            return "";
        }
    }

    protected String getLogFolderLocation(TestSuiteEntity testSuite) {
        try {
            return ReportController.getInstance().generateReportFolder(testSuite);
        } catch (Exception e) {
            return "";
        }
    }

    public void generateLogFolder(FileEntity fileEntity) {
        String logFolderPath = "";
        if (fileEntity instanceof TestCaseEntity || fileEntity instanceof SystemFileEntity) {
            logFolderPath = getTemporaryLogFolderLocation(fileEntity);
        } else if (fileEntity instanceof TestSuiteEntity) {
            logFolderPath = getLogFolderLocation((TestSuiteEntity) fileEntity);
        }

        executionSetting.setFolderPath(logFolderPath);
    }

    @Override
    public String getProjectFolderLocation() {
        return ProjectController.getInstance().getCurrentProject().getFolderLocation().replace(File.separator, "/");
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();

        propertyMap.put(StringConstants.NAME, getName());

        propertyMap.put(RunConfiguration.PROJECT_DIR_PROPERTY, getProjectFolderLocation());

        propertyMap.put(RunConfiguration.HOST, hostConfiguration.getProperties());

        if (executionSetting == null) {
            return propertyMap;
        }
        propertyMap.putAll(
                ExecutionUtil.getExecutionProperties(executionSetting, getDriverConnectors(), executionProfile));
        IExecutedEntity executedEntity = executionSetting.getExecutedEntity();
        if (executedEntity == null) {
            return propertyMap;
        }
        
        if(!overridingParameters.isEmpty()){
        	propertyMap.put(RunConfiguration.OVERRIDING_GLOBAL_VARIABLES, overridingParameters);
        }

        propertyMap.put(RunConfiguration.EXCUTION_SOURCE_ID, executedEntity.getSourceId());
        propertyMap.put(RunConfiguration.EXCUTION_SOURCE_NAME, executedEntity.getSourceName());
        propertyMap.put(RunConfiguration.EXCUTION_SOURCE_DESCRIPTION, executedEntity.getSourceDescription());
        propertyMap.put(RunConfiguration.EXCUTION_SOURCE, executedEntity.getSourcePath());

        ExecutionSessionSocketServer sessionServer = ExecutionSessionSocketServer.getInstance();
        propertyMap.put(RunConfiguration.SESSION_SERVER_HOST, sessionServer.getServerHost());
        propertyMap.put(RunConfiguration.SESSION_SERVER_PORT, sessionServer.getServerPort());
        
        String logbackConfigFileLocation = getLogbackConfigFileLocation();
        if (logbackConfigFileLocation != null) {
            propertyMap.put(RunConfiguration.LOGBACK_CONFIG_FILE_LOCATION, logbackConfigFileLocation);
        }
        
        propertyMap.put(RunConfiguration.RUNNING_MODE, ApplicationRunningMode.get().name());
        
        initializePluginPresence("com.katalon.katalon-studio-smart-xpath", propertyMap);
        
        return propertyMap;
    }
    
    private boolean initializePluginPresence(String pluginID, Map<String, Object> propertyMap){
		Plugin plugin = ApplicationManager.getInstance().getPluginManager().getPlugin(pluginID);
		if (plugin != null) {
			propertyMap.put(pluginID, true);
			return true;
		}
		return false;
    }
    
    private String getLogbackConfigFileLocation() {
        String logbackConfigFileLocation = null;
        try {
            File logbackConfigFile = LogbackUtil.getLogbackConfigFile();
            if (logbackConfigFile != null && logbackConfigFile.exists()) {
                logbackConfigFileLocation = logbackConfigFile.getAbsolutePath();
            }
        } catch (IOException ignored) {
        }
        
        return logbackConfigFileLocation;
    }

    @Override
    public String getName() {
        StringBuilder nameStringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (IDriverConnector driverConnector : getDriverConnectors().values()) {
            if (!isFirst) {
                nameStringBuilder.append(" + ");
            }
            nameStringBuilder.append(driverConnector.getDriverType().toString());
            isFirst = false;
        }
        return nameStringBuilder.toString();
    }

    public IHostConfiguration getHostConfiguration() {
        return hostConfiguration;
    }

    @Override
    public IExecutionSetting getExecutionSetting() {
        return executionSetting;
    }

    public final void generateExecutionProperties() throws IOException {
        File settingFile = new File(executionSetting.getSettingFilePath());

        Gson gsonObj = new Gson();
        String strJson = gsonObj.toJson(getProperties());
        FileUtils.writeStringToFile(settingFile, strJson);
    }

    @Override
    public Map<String, String> getAdditionalEnvironmentVariables() throws IOException, ExecutionException {
        return new HashMap<>();
    }

    @Override
    public boolean allowsRecording() {
        return false;
    }

    @Override
    public ExecutionProfileEntity getExecutionProfile() {
        return executionProfile;
    }

    public void setExecutionProfile(ExecutionProfileEntity executionProfile) {
        this.executionProfile = executionProfile;
    }
    
    public void setOverridingGlobalVariables(Map<String, Object> overridingGlobalVariables) {
    	if(overridingGlobalVariables == null) return;
    	overridingParameters.putAll(overridingGlobalVariables);
    }
}
