package com.kms.katalon.execution.configuration.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.configuration.IExecutionSetting;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.execution.setting.ExecutionSettingStore;
import com.kms.katalon.execution.setting.TestCaseSettingStore;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.logging.LogUtil;

public class DefaultExecutionSetting implements IExecutionSetting {

    private IExecutedEntity executedEntity;

    private String folderPath;

    private int timeout;

    private File scriptFile;
    
    private String rawScript;
    
    private Boolean autoApplyNeighborXpaths;

    private Map<String, Object> generalProperties;

    public DefaultExecutionSetting() {
        timeout = 0;
        setAutoApplyNeighborXpaths(ExecutionUtil.getAutoApplyNeighborXpaths());        
    }

    @Override
    public int getTimeOut() {
        return timeout;
    }

    @Override
    public Map<String, Object> getGeneralProperties() {
        generalProperties = new HashMap<>();

        generalProperties.put(RunConfiguration.TIMEOUT_PROPERTY, timeout);
        generalProperties.put(StringConstants.CONF_PROPERTY_REPORT, getReportProperties());
        generalProperties.put(RunConfiguration.EXCUTION_DEFAULT_FAILURE_HANDLING, getDefaultFailureHandlingSetting());
        generalProperties.put(RunConfiguration.PROXY_PROPERTY, getJsonProxyInformation());
        generalProperties.put(RunConfiguration.TERMINATE_DRIVER_AFTER_TEST_CASE, ExecutionUtil.isQuitDriversAfterExecutingTestCase());
        generalProperties.put(RunConfiguration.TERMINATE_DRIVER_AFTER_TEST_SUITE, ExecutionUtil.isQuitDriversAfterExecutingTestSuite());
        
        if (executedEntity != null) {
            generalProperties.put(RunConfiguration.EXECUTION_TEST_DATA_INFO_PROPERTY, executedEntity.getCollectedDataInfo());
        }
        
        if(this.autoApplyNeighborXpaths != null){
        	generalProperties.put(RunConfiguration.AUTO_APPLY_NEIGHBOR_XPATHS, this.autoApplyNeighborXpaths);
        }
        
        return generalProperties;
    }

    private Map<String, Object> getReportProperties() {
        Map<String, Object> reportProps = new HashMap<String, Object>();
        try {
            ExecutionSettingStore reportSettings = new ExecutionSettingStore(
                    getCurrentProject());
            reportProps.put(RunConfiguration.REPORT_FOLDER_PATH_PROPERTY, getFolderPath());
            reportProps.put(StringConstants.CONF_PROPERTY_SCREEN_CAPTURE_OPTION, reportSettings.getScreenCaptureOption());
            reportProps.put(StringConstants.CONF_PROPERTY_VIDEO_RECORDER_OPTION, reportSettings.getVideoRecorderSetting());
        } catch (IOException e) {
            LogUtil.logError(e);
        }

        return reportProps;
    }

    protected ProjectEntity getCurrentProject() {
        return ProjectController.getInstance().getCurrentProject();
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public Boolean getAutoApplyNeighborXpaths(){
    	return this.autoApplyNeighborXpaths;
    }
    
    public void setAutoApplyNeighborXpaths(Boolean val){
    	this.autoApplyNeighborXpaths = val;
    }

    public String getLogFileName() {
        return "execution";
    }

    @Override
    public IExecutedEntity getExecutedEntity() {
        return executedEntity;
    }

    @Override
    public String getFolderPath() {
        return folderPath;
    }

    @Override
    public String getSettingFileName() {
        return "execution.properties";
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public void setExecutedEntity(IExecutedEntity executedEntity) {
        this.executedEntity = executedEntity;
    }

    @Override
    public String getSettingFilePath() {
        return new File(folderPath, getSettingFileName()).getAbsolutePath();
    }

    @Override
    public File getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(File scriptFile) {
        this.scriptFile = scriptFile;
    }
    
    @Override
    public String getRawScript() {
        return rawScript;
    }

    public void setRawScript(String rawScript) {
        this.rawScript = rawScript;
    }

    @Override
    public String getName() {
        return FilenameUtils.getBaseName(getFolderPath());
    }

    public String getDefaultFailureHandlingSetting() {
        return new TestCaseSettingStore(getCurrentProject().getFolderLocation()).getDefaultFailureHandling().name();
    }

    private String getJsonProxyInformation() {
        return new Gson().toJson(ProxyPreferences.getProxyInformation());
    }
}
