package com.kms.katalon.composer.handlers;

public class OpenKatalonAnalyticsPageHandler extends OpenIntegrationPluginPageHandler {

    @Override
    protected String getPageUrl() {
        return "https://analytics.katalon.com";
    }
}
