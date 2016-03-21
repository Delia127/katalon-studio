package com.kms.katalon.execution.entity;

import org.apache.commons.lang.StringUtils;

public class ReportLocationSetting {
    public static final String DEFAULT_REPORT_FILE_NAME = "report";
    public static final boolean DEFAULT_CLEAN_REPORT_FOLDER_FLAG = false;
    private String reportFolderPath;
    private String reportFileName;
    private boolean cleanReportFolderFlag;
    private boolean reportFileNameFlag;

    public ReportLocationSetting() {
        cleanReportFolderFlag = DEFAULT_CLEAN_REPORT_FOLDER_FLAG;
        reportFileNameFlag = false;
    }
    
    public String getReportFolderPath() {
        return reportFolderPath;
    }

    public void setReportFolderPath(String reportFolderPath) {
        this.reportFolderPath = reportFolderPath;
    }

    public String getReportFileName() {
        if (reportFileName == null) {
            reportFileName = DEFAULT_REPORT_FILE_NAME;
        }
        return reportFileName;
    }

    public void setReportFileName(String reportFileName) {
        reportFileNameFlag = true;
        this.reportFileName = reportFileName;
    }

    public boolean isCleanReportFolderFlagActive() {
        return cleanReportFolderFlag;
    }

    public void setCleanReportFolder(boolean cleanReportFolderFlag) {
        this.cleanReportFolderFlag = cleanReportFolderFlag;
    }

    public boolean isReportFileNameSet() {
        return reportFileNameFlag;
    }
    
    public boolean isReportFolderPathSet() {
        return StringUtils.isNotBlank(reportFolderPath);
    }
}
