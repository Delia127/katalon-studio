package com.kms.katalon.execution.configuration.impl;

import static com.kms.katalon.core.constants.StringConstants.TESTCASE_SETTINGS_FILE_NAME;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IExecutionSetting;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.setting.ExecutionSettingStore;
import com.kms.katalon.logging.LogUtil;


public class DefaultExecutionSetting implements IExecutionSetting {
    
    private IExecutedEntity executedEntity;
    
    private String folderPath;
    
    private int timeout;
    private File scriptFile;
    
    private Map<String, Object> generalProperties;
    
    public DefaultExecutionSetting() {
        timeout = 0;
    }

    @Override
    public int getTimeOut() {
        return timeout;
    }

    @Override
    public Map<String, Object> getGeneralProperties() {
        generalProperties = new HashMap<String, Object>();

        generalProperties.put("timeout", timeout);
        generalProperties.put(StringConstants.CONF_PROPERTY_REPORT, getReportProperties());
        generalProperties.put(RunConfiguration.EXCUTION_DEFAULT_FAILURE_HANDLING, getDefaultFailureHandling());

        return generalProperties;
    }

    private Map<String, Object> getReportProperties() {
        Map<String, Object> reportProps = new HashMap<String, Object>();
        try {
            reportProps.put(StringConstants.CONF_PROPERTY_SCREEN_CAPTURE_OPTION, new ExecutionSettingStore(
                    ProjectController.getInstance().getCurrentProject()).getScreenCaptureOption());
        } catch (IOException e) {
            LogUtil.logError(e);
        }

        return reportProps;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
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
    public String getName() {
        return FilenameUtils.getBaseName(getFolderPath());
    }

    public String getDefaultFailureHandling() {
        try {
            File configFile = new File(ProjectController.getInstance()
                    .getCurrentProject()
                    .getFolderLocation()
                    .replace(File.separator, "/")
                    + File.separator
                    + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME
                    + File.separator
                    + TESTCASE_SETTINGS_FILE_NAME + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION);
            String defaultFailureHandling = PropertySettingStoreUtil.getPropertyValue(RunConfiguration.EXCUTION_DEFAULT_FAILURE_HANDLING,
                    configFile);
            return defaultFailureHandling != null ? defaultFailureHandling : FailureHandling.STOP_ON_FAILURE.name(); 
        } catch (IOException ex) {
            return FailureHandling.STOP_ON_FAILURE.name();
        }
    }
}
