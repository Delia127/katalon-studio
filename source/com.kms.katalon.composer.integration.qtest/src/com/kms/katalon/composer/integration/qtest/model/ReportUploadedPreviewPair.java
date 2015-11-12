package com.kms.katalon.composer.integration.qtest.model;

import java.util.List;

import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;

public class ReportUploadedPreviewPair {
    private ReportEntity reportEntity;
    private List<QTestLogUploadedPreview> qTestLogs;

    public ReportUploadedPreviewPair(ReportEntity left, List<QTestLogUploadedPreview> right) {
        reportEntity = left;
        setTestLogs(right);
    }

    public List<QTestLogUploadedPreview> getTestLogs() {
        return qTestLogs;
    }

    public void setTestLogs(List<QTestLogUploadedPreview> qTestLogs) {
        this.qTestLogs = qTestLogs;
    }

    public ReportEntity getReportEntity() {
        return reportEntity;
    }

    public void setReportEntity(ReportEntity reportEntity) {
        this.reportEntity = reportEntity;
    }

}
