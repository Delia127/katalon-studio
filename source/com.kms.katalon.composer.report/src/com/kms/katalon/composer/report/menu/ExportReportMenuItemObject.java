package com.kms.katalon.composer.report.menu;

import com.kms.katalon.composer.report.platform.ExportReportProviderPlugin;

public class ExportReportMenuItemObject {

    private final String formatType;

    private final ExportReportProviderPlugin exportReportPlugin;

    public ExportReportMenuItemObject(String formatType, ExportReportProviderPlugin exportReportPlugin) {
        this.formatType = formatType;
        this.exportReportPlugin = exportReportPlugin;
    }

    public String getFormatType() {
        return formatType;
    }

    public ExportReportProviderPlugin getExportReportPlugin() {
        return exportReportPlugin;
    }
}
