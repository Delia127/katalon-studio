package com.kms.katalon.composer.handlers;

public class OpenDockerPageHandler extends OpenIntegrationPluginPageHandler {

    @Override
    protected String getPageUrl() {
        return "https://hub.docker.com/r/katalonstudio/katalon";
    }
}
