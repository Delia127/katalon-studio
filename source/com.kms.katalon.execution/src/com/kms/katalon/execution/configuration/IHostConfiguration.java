package com.kms.katalon.execution.configuration;

import java.util.Map;

public interface IHostConfiguration {
    public String getOS();
    
    public String getHostName();
    
    public String getHostAddress();
    
    public int getHostPort();
    
    public Map<String, Object> getProperties();
}
