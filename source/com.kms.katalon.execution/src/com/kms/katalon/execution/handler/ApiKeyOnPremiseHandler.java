package com.kms.katalon.execution.handler;

import com.kms.katalon.execution.util.ApiKeyOnPremise;

public class ApiKeyOnPremiseHandler {

    public static void setApiKeyOnPremiseToProject(String apiKeyOnPremise) {
        ApiKeyOnPremise.set(apiKeyOnPremise);
    }
}
