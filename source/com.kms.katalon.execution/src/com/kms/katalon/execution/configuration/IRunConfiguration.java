package com.kms.katalon.execution.configuration;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;

public interface IRunConfiguration {	
    public String getName();
    
    public String getProjectFolderLocation();
    
    public Map<String, IDriverConnector> getDriverConnectors();
	
	public IExecutionSetting build(FileEntity fileEntity, IExecutedEntity entity) throws IOException, ExecutionException;
	
	public IHostConfiguration getHostConfiguration();
	
	public IExecutionSetting getExecutionSetting();
	
    public Map<String, Object> getProperties();
    
    public void generateExecutionProperties() throws IOException;
    
    public Map<String, String> getAdditionalEnvironmentVariables() throws IOException, ExecutionException;
    
    public IRunConfiguration cloneConfig() throws IOException, ExecutionException;

    boolean allowsRecording();
    
    ExecutionProfileEntity getExecutionProfile();
    
    public void setOverridingGlobalVariables(Map<String, Object> overridingGlobalVariables);
}
