package com.kms.katalon.integration.analytics.query;

public class AnalyticsQueryFunction {
	private String key;
    private String function;
    private String[] parameters;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getFunction() {
        return function;
    }
    public void setFunction(String function) {
        this.function = function;
    }
    public String[] getParameters() {
        return parameters;
    }
    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }
    public AnalyticsQueryFunction(String key, String function, String[] parameters) {
        super();
        this.key = key;
        this.function = function;
        this.parameters = parameters;
    }
}
