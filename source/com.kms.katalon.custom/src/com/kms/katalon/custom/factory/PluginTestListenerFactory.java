package com.kms.katalon.custom.factory;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class PluginTestListenerFactory {

    private static PluginTestListenerFactory instance;
    
    private PluginTestListenerFactory() {
        listeners = new LinkedHashSet<>(); 
    }
    
    public static PluginTestListenerFactory getInstance() {
        if (instance == null) {
            instance = new PluginTestListenerFactory();
        }
        return instance;
    }
    private Set<String> listeners;
    
    public Set<String> getListeners() {
        return Collections.unmodifiableSet(listeners);
    }
    
    public void addListener(String className) {
        listeners.add(className);
    }
    
    public void clear() {
        listeners.clear();
    }
}
