package com.kms.katalon.composer.handlers;

public class OpenPluginRequestPageHandler extends OpenIntegrationPluginPageHandler {

    @Override
    protected String getPageUrl() {
        return "https://forum.katalon.com/c/katalon-studio/plugin-platform";
    }
}
