package com.kms.katalon.integration.analytics.handler;

import com.kms.katalon.integration.analytics.util.ApiKey;

public class AnalyticsApiKeyHanlder {

	public void setApiKeyToProject(String apiKey) {
		ApiKey.set(apiKey);
	}
}
