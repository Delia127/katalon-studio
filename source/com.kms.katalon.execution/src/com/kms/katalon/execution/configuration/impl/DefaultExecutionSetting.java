package com.kms.katalon.execution.configuration.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.execution.configuration.IExecutionSetting;
import com.kms.katalon.execution.entity.IExecutedEntity;

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
        return generalProperties;
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

}
