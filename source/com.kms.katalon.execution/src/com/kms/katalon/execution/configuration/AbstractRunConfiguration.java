package com.kms.katalon.execution.configuration;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.util.LogbackUtil;
import com.kms.katalon.custom.factory.PluginTestListenerFactory;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.configuration.impl.LocalHostConfiguration;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.DefaultRerunSetting.RetryStrategyValue;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.generator.FeatureFileScriptGenerator;
import com.kms.katalon.execution.generator.TestCaseScriptGenerator;
import com.kms.katalon.execution.generator.TestSuiteScriptGenerator;
import com.kms.katalon.execution.session.ExecutionSessionSocketServer;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.KSEFeature;

public abstract class AbstractRunConfiguration implements IRunConfiguration {

    protected IHostConfiguration hostConfiguration;

    protected DefaultExecutionSetting executionSetting;

    private ExecutionProfileEntity executionProfile;
    
    private Map<String, Object> overridingParameters = new HashMap<>();

    private Map<String, String> environmentVariables = new HashMap<>();
    
    private List<String> vmArgs = new ArrayList<>();
    
    private Map<String, String> additionalData = new HashMap<>();
    
    private Map<String, String> additionalInfo = new HashMap<>();

    private String executionUUID;
    
    private String executionSessionId;

    private static final String PATH = "PATH";
    
    private IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();

    public AbstractRunConfiguration() {
        doInitExecutionSetting();
        initVmArguments();
    }
    
    protected void doInitExecutionSetting() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        this.executionSessionId =  dateFormat.format(new Date());
        initExecutionSetting();
    }
    
    protected void initVmArguments() {
        if (featureService.canUse(KSEFeature.LAUNCH_ARGUMENTS_SETTINGS)) {
            vmArgs.addAll(Arrays.asList(ExecutionUtil.getVmArgs()));
        }
    }

    @Override
    public final IExecutionSetting build(FileEntity fileEntity, IExecutedEntity executedEntity)
            throws IOException, ExecutionException {
        init(fileEntity);

        executionSetting.setExecutedEntity(executedEntity);

        hostConfiguration = new LocalHostConfiguration();

        generateLogFolder(fileEntity);

        File scriptFile = generateTempScriptFile(fileEntity);
        
        generateExecutionProperties();

        executionSetting.setScriptFile(scriptFile);

        return executionSetting;
    }

    protected File generateTempScriptFile(FileEntity fileEntity) throws ExecutionException {
        try {
            if (fileEntity instanceof TestSuiteEntity) {
                TestSuiteExecutedEntity t = (TestSuiteExecutedEntity) this.getExecutionSetting().getExecutedEntity();
                String retryFailedExecutionsTcBindings = additionalData
                        .getOrDefault(RunConfiguration.TC_RETRY_FAILED_EXECUTIONS_ONLY, StringUtils.EMPTY);
                String retryImmediatelyTcBindings = additionalData
                        .getOrDefault(RunConfiguration.TC_RETRY_IMMEDIATELY_BINDINGS, StringUtils.EMPTY);
                if (shouldRetryImmediately(t, retryImmediatelyTcBindings)) {
                    return generatetTempScriptFileWithCustomRetry(fileEntity, retryImmediatelyTcBindings);
                }
                if (shouldRetryFailedExecutionsOnly(t, retryFailedExecutionsTcBindings)) {
                    return generatetTempScriptFileWithCustomRetry(fileEntity, retryFailedExecutionsTcBindings);
                }
                return generateTempScriptFileWithDefaultRetry(fileEntity);
            } else if (fileEntity instanceof TestCaseEntity) {
                return new TestCaseScriptGenerator((TestCaseEntity) fileEntity, this).generateScriptFile();
            } else if (fileEntity instanceof SystemFileEntity) {
                return new FeatureFileScriptGenerator((SystemFileEntity) fileEntity, this).generateScriptFile();
            }
            throw new ExecutionException("The execution is not supported for this file");
        } catch (Exception ex) {
            throw new ExecutionException(ex);
        }
    }

    private File generateTempScriptFileWithDefaultRetry(FileEntity fileEntity) throws Exception {
        return new TestSuiteScriptGenerator((TestSuiteEntity) fileEntity, this,
                (TestSuiteExecutedEntity) this.getExecutionSetting().getExecutedEntity()).generateScriptFile();
    }

    private File generatetTempScriptFileWithCustomRetry(FileEntity fileEntity, String retryImmediatelyTcBindings)
            throws Exception {
        return new TestSuiteScriptGenerator((TestSuiteEntity) fileEntity, this,
                (TestSuiteExecutedEntity) this.getExecutionSetting().getExecutedEntity())
                        .generateScriptFile(retryImmediatelyTcBindings);
    }

    private boolean shouldRetryImmediately(TestSuiteExecutedEntity t, String retryImmediatelyTcBindings) {
        return RetryStrategyValue.immediately.equals(t.getRetryStrategy()) && !StringUtils.EMPTY.equals(retryImmediatelyTcBindings);
    }

    private boolean shouldRetryFailedExecutionsOnly(TestSuiteExecutedEntity t, String retryFailedExecutionsTcBindings) {
        return RetryStrategyValue.failedExecutions.equals(t.getRetryStrategy())
                && !StringUtils.EMPTY.equals(retryFailedExecutionsTcBindings);
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
            return ReportController.getInstance().generateReportFolder(testSuite, executionSessionId);
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
        
        additionalInfo.forEach((key, value) -> {
        	propertyMap.put(key, value);
        });
        
        String logbackConfigFileLocation = getLogbackConfigFileLocation();
        if (logbackConfigFileLocation != null) {
            propertyMap.put(RunConfiguration.LOGBACK_CONFIG_FILE_LOCATION, logbackConfigFileLocation);
        }
        
        propertyMap.put(RunConfiguration.RUNNING_MODE, ApplicationRunningMode.get().name());
        
        propertyMap.put(RunConfiguration.PLUGIN_TEST_LISTENERS, PluginTestListenerFactory.getInstance().getListeners());
        propertyMap.put(RunConfiguration.ALLOW_IMAGE_RECOGNITION, featureService.canUse(KSEFeature.IMAGE_BASED_OBJECT_DETECTION));
        
//        initializePluginPresence(IdConstants.KATALON_SMART_XPATH_BUNDLE_ID, propertyMap);
        
        propertyMap.put(RunConfiguration.ALLOW_USING_SMART_XPATH, featureService.canUse(KSEFeature.SMART_XPATH));
        
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
        return Collections.unmodifiableMap(environmentVariables);
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
    
    @Override
    public Map<String, Object> getOverridingGlobalVariables() {
       return overridingParameters;
    }
    
    @Override
    public Map<String, String> getTestSuiteAdditionalData() {
        return Collections.unmodifiableMap(additionalData);
    }

    @Override
    public void setTestSuiteAdditionalData(Map<String, String> data) {
        if (data != null) {
            this.additionalData.putAll(data);
        }
    }
    
    @Override
    public String[] getVmArgs() {
        return vmArgs.toArray(new String[0]);
    }
    
    @Override
    public void setVmArgs(String[] args) {
        if (args == null) {
            return;
        }
        vmArgs.addAll(Arrays.asList(args));
    }
    
    @Override
    public void setAdditionalEnvironmentVariables(Map<String, String> addtionalEnv) {
        if (addtionalEnv != null) {
            this.environmentVariables.putAll(addtionalEnv);
        }
    }

    public String getExecutionUUID() {
        return executionUUID;
    }
    
    @Override
    public void setExecutionUUID(String executionUUID) {
        this.executionUUID = executionUUID;
    }

	public String getExecutionSessionId() {
		return executionSessionId;
	}

	public void setExecutionSessionId(String executionSessionId) {
		this.executionSessionId = executionSessionId;
	}
	
	public void setAdditionalInfo(Map<String, String> data) {
		if (data != null) {
			this.additionalInfo.putAll(data);
		}
	}
    
	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}
}
