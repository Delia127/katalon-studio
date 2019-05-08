package com.kms.katalon.composer.report.platform;

import com.kms.katalon.custom.keyword.CustomKeywordPlugin;

public class ExportReportProviderPlugin {

    private final CustomKeywordPlugin plugin;

    private final Object exportProviderInstance;
    
    public ExportReportProviderPlugin(CustomKeywordPlugin plugin, Object exportProviderInstance) {
        this.plugin = plugin;
        this.exportProviderInstance = exportProviderInstance;
    }

    public CustomKeywordPlugin getPlugin() {
        return plugin;
    }

    public Object getProvider() {
        return exportProviderInstance;
    }
}
