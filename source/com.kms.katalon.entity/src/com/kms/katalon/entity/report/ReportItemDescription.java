package com.kms.katalon.entity.report;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;

public class ReportItemDescription {
    private String reportLocation;

    private RunConfigurationDescription runConfigDescription;

    public RunConfigurationDescription getRunConfigDescription() {
        return runConfigDescription;
    }

    public void setRunConfigDescription(RunConfigurationDescription runConfigDescription) {
        this.runConfigDescription = runConfigDescription;
    }

    public String getReportLocation() {
        if (reportLocation == null) {
            reportLocation = "";
        }
        return reportLocation;
    }

    public void setReportLocation(String reportLocation) {
        this.reportLocation = reportLocation;
    }

    public static ReportItemDescription from(String reportLocation, RunConfigurationDescription configDescription) {
        ReportItemDescription reportDescription = new ReportItemDescription();
        reportDescription.setRunConfigDescription(configDescription);
        reportDescription.setReportLocation(reportLocation);
        return reportDescription;
    }
}
