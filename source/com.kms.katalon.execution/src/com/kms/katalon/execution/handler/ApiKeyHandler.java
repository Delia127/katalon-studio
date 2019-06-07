package com.kms.katalon.execution.handler;

import com.kms.katalon.execution.util.ApiKey;

public class ApiKeyHandler {

    public static void setApiKeyToProject(String apiKey) {
        ApiKey.set(apiKey);
    }
}
