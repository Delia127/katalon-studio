package com.kms.katalon.composer.report.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kms.katalon.composer.report.parts.integration.ReportTestCaseIntegrationViewBuilder;
import com.kms.katalon.composer.report.platform.ExportReportProviderPlugin;
import com.kms.katalon.composer.report.platform.PlatformReportIntegrationViewBuilder;

public class ReportComposerIntegrationFactory {
    private static ReportComposerIntegrationFactory _instance;

    private PlatformReportIntegrationViewBuilder platformViewerBuilder;

    private Map<String, ReportTestCaseIntegrationViewBuilder> testCaseIntegrationViewMap = new LinkedHashMap<>();

    private List<ExportReportProviderPlugin> exportReportPluginProviders = new ArrayList<>();

    public Map<String, ReportTestCaseIntegrationViewBuilder> getIntegrationViewMap() {
        return testCaseIntegrationViewMap;
    }

    private ReportComposerIntegrationFactory() {
    }

    public static ReportComposerIntegrationFactory getInstance() {
        if (_instance == null) {
            _instance = new ReportComposerIntegrationFactory();
        }
        return _instance;
    }

    public void addNewTestCaseIntegrationView(String productName,
            ReportTestCaseIntegrationViewBuilder contributionView) {
        testCaseIntegrationViewMap.put(productName, contributionView);
    }

    public List<ReportTestCaseIntegrationViewBuilder> getSortedBuilder() {
        List<ReportTestCaseIntegrationViewBuilder> builders = new ArrayList<>();
        for (Entry<String, ReportTestCaseIntegrationViewBuilder> builderEntry : getInstance().getIntegrationViewMap()
                .entrySet()) {
            builders.add(builderEntry.getValue());
        }
        if (platformViewerBuilder != null) {
            builders.addAll(platformViewerBuilder.getIntegrationViews());
        }

        builders.sort((left, right) -> left.getName().compareToIgnoreCase(right.getName()));
        return builders;
    }

    public void addPlatformViewerBuilder(PlatformReportIntegrationViewBuilder platformViewerBuilder) {
        this.platformViewerBuilder = platformViewerBuilder;
    }

    public void addExportReportProvider(ExportReportProviderPlugin exportReportProviderPlugin) {
        this.exportReportPluginProviders.add(exportReportProviderPlugin);
    }
    
    public void onProjectChanged() {
        this.exportReportPluginProviders.clear();
    }

    public List<ExportReportProviderPlugin> getExportReportPluginProviders() {
        return Collections.unmodifiableList(exportReportPluginProviders);
    }

    public void setExportReportPluginProviders(List<ExportReportProviderPlugin> exportReportPluginProviders) {
        this.exportReportPluginProviders = exportReportPluginProviders;
    }
}
