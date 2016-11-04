package com.kms.katalon.integration.jira.entity;

import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.util.JsonUtil;
import com.kms.katalon.integration.jira.constant.StringConstants;

public abstract class JiraIntegratedObject {

    public Map<String, String> getIntegratedValue() {
        Map<String, String> properties = new HashMap<>();
        properties.put(StringConstants.INTEGRATED_VALUE_NAME, JsonUtil.toJson(this, false));
        return properties;
    }
}
