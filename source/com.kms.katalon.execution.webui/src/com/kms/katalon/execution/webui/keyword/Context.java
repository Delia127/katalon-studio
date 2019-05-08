package com.kms.katalon.execution.webui.keyword;

import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.keyword.IContext;

public class Context implements IContext {

    private Map<String, Object> contextMap = new HashMap<>();
    
    @Override
    public Map<String, Object> getContextMap() {
        return contextMap;
    }
    
    public void add(String key, Object value) {
        contextMap.put(key, value);
    }

}
