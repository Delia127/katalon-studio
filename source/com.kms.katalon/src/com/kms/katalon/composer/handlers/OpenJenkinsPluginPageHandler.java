package com.kms.katalon.composer.handlers;

public class OpenJenkinsPluginPageHandler extends OpenIntegrationPluginPageHandler {
    @Override
    protected String getPageUrl() {
        return "https://plugins.jenkins.io/katalon";
    }
}
