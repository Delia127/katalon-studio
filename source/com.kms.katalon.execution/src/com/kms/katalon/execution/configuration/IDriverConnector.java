package com.kms.katalon.execution.configuration;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;

public interface IDriverConnector {
	public DriverType getDriverType();
	
	public String getParentFolderPath();
	
    void setParentFolderPath(String parentFolderPath);
    
	public String getSettingFileName();
	
	public Map<String, Object> getSystemProperties();
	
    public Map<String, Object> getUserConfigProperties();
    
    public void setUserConfigProperties(Map<String, Object> userConfigProperties);
    
    public void saveUserConfigProperties() throws IOException;
    
    public Object getDriverPropertyValue(String rawKey);
    
    public void setDriverPropertyValue(String rawKey, String propertyValue);
}
