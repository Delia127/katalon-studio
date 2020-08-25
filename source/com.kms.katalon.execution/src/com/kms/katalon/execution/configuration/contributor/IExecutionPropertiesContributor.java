package com.kms.katalon.execution.configuration.contributor;

import java.util.Map;

public interface IExecutionPropertiesContributor {
    public String getKey();
    public Map<String, Object> getExecutionProperties();
}
