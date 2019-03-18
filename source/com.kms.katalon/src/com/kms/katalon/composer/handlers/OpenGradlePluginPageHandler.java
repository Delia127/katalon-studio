package com.kms.katalon.composer.handlers;

public class OpenGradlePluginPageHandler extends OpenIntegrationPluginPageHandler {
    @Override
    protected String getPageUrl() {
        return "https://plugins.gradle.org/plugin/com.katalon.gradle-plugin";
    }
}
