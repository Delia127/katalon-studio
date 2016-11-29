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

    /**
     * If not null then use this script to run the test instead of test case script
     * @return the raw script to execute, or null if running test case script
     */
    public String getRawScript();
}
