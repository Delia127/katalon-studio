package com.kms.katalon.execution.configuration;

import java.util.Map;

public interface IRunConfiguration {
	public IDriverConnector[] getDriverConnectors();

	public String getLogFilePath();

	public String getProjectFolderLocation();
	
	public int getTimeOut();
	
	public String getHostName();
	
	public String getOS();
	
	public String getSource();
	
	public String getSourceName();

	public String getSourceId();

	public String getSourceDescription();

	public Map<String, Object> getPropertyMap();
	
	public String getName();
	
	public String getExecutionSettingFilePath();
}
