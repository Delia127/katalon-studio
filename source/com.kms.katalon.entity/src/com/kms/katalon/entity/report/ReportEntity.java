package com.kms.katalon.entity.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.file.IntegratedFileEntity;

public class ReportEntity extends IntegratedFileEntity {

    public static final String EXTENSION_CSV_REPORT = ".csv";

    public static final String EXTENSION_HTML_REPORT = ".html";

    public static final String EXTENSION_PDF_REPORT = ".pdf";

    private static final long serialVersionUID = 1228763256416624714L;

    public static final String DF_LOG_FILE_NAME = "execution0.log";

    public static final String JUNIT_REPORT_NAME = "JUnit_Report.xml";

    public static final String VIDEO_RECORDED_FOLDER = "videos";

    private String displayName;
    
    private List<ReportTestCaseEntity> reportTestCases;

    @Override
    public String getRelativePathForUI() {
        if (parentFolder != null) {
            return parentFolder.getRelativePath() + File.separator + this.name;
        }
        return "";
    }

    @Override
    public String getLocation() {
        // return parentFolder.getLocation() + File.separator + this.name + getFileExtension();
        return parentFolder.getLocation() + File.separator + this.name;
    }

    @Override
    public String getFileExtension() {
        return getReportFileExtension();
    }

    public String getHtmlFile() {
        return getLocation() + File.separator + getName() + EXTENSION_HTML_REPORT;
    }

    public String getCSVFile() {
        return getLocation() + File.separator + getName() + EXTENSION_CSV_REPORT;
    }

    public String getJUnitFile() {
        return getLocation() + File.separator + JUNIT_REPORT_NAME;
    }

    public static String getReportFileExtension() {
        return "";
    }

    public String getDisplayName() {
        if (displayName == null) {
            return getName();
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<ReportTestCaseEntity> getReportTestCases() {
        if (reportTestCases == null) {
            reportTestCases = new ArrayList<>();
        }
        return reportTestCases;
    }

    public void setReportTestCases(List<ReportTestCaseEntity> reportTestCases) {
        this.reportTestCases = reportTestCases;
    }
}
