package com.kms.katalon.execution.platform;

import java.util.HashMap;
import java.util.Map;

public class ExecutionPlatformServiceProvider {
    private static ExecutionPlatformServiceProvider instance;
    
    private Map<String, Object> lookup = new HashMap<>();
    
    private ExecutionPlatformServiceProvider() {
        // Disable default constructor
    }
    
    public static ExecutionPlatformServiceProvider getInstance() {
        if (instance == null) {
            instance = new ExecutionPlatformServiceProvider();
        }
        
        return instance;
    }

    public <T> void addService(Class<T> serviceInterface, T service) {
        lookup.put(serviceInterface.getName(), service);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getPlatformService(Class<T> serviceInterfaceClazz) {
        return (T) lookup.get(serviceInterfaceClazz.getName());
    }
}
