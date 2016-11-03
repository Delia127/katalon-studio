package com.kms.katalon.composer.report.provider;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.report.ReportItemDescription;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public interface ReportItemDescriptionLabelProvider {

    default TestSuiteEntity getTestSuite(ReportItemDescription element) {
        try {
            return ReportController.getInstance().getTestSuiteByReport(getReport(element.getReportLocation()));
        } catch (Exception e) {
            return null;
        }
    }

    default ReportEntity getReport(String reportId) {
        if (StringUtils.isEmpty(reportId)) {
            return null;
        }
        try {
            return ReportController.getInstance().getReportEntityByDisplayId(reportId,
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception ex) {
            return null;
        }
    }

    default TestSuiteLogRecord getTestSuiteLogRecord(String reportId) {
        ReportEntity report = getReport(reportId);
        if (report == null) {
            return null;
        }
        return LogRecordLookup.getInstance().getTestSuiteLogRecord(report);
    }
}
