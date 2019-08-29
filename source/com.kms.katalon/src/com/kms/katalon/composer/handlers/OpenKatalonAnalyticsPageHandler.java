package com.kms.katalon.composer.handlers;

import com.kms.katalon.application.utils.ApplicationInfo;

public class OpenKatalonAnalyticsPageHandler extends OpenIntegrationPluginPageHandler {

    @Override
    protected String getPageUrl() {
        return ApplicationInfo.getTestOpsServer();
    }
}
