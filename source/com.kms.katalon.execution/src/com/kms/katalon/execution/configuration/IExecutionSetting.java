package com.kms.katalon.execution.configuration;

import java.io.File;
import java.util.Map;

import com.kms.katalon.execution.entity.IExecutedEntity;

public interface IExecutionSetting {    
    
    /**
     * Returns default time-out for almost driver's keywords
     */
    public int getTimeOut();

    public String getFolderPath();

    public String getSettingFileName();

    public String getLogFileName();
    
    public String getName();
    
    /**
     * @return Absolute path of setting file
     */
    public String getSettingFilePath();
    
    public File getScriptFile();

    public Map<String, Object> getGeneralProperties();
    
    public IExecutedEntity getExecutedEntity();
}
