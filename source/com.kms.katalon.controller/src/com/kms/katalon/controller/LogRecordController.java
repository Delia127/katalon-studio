package com.kms.katalon.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.logging.LogUtil;

public class LogRecordController {
    private static LogRecordController _instance;

    public static LogRecordController getInstance() {
        if (_instance == null) {
            _instance = new LogRecordController();
        }
        return (LogRecordController) _instance;
    }
    
    // Key: id of report entity, Value: the relevant TestSuiteLogRecord instance of the report
    private Map<String, TestSuiteLogRecord> suiteLogRecordMap = new HashMap<>();
    
    public TestSuiteLogRecord getTestSuiteLogRecord(String reportId) {
        if (StringUtils.isEmpty(reportId)) {
            return null;
        }
        try {
            ReportEntity report = ReportController.getInstance().getReportEntityByDisplayId(reportId,
                    ProjectController.getInstance().getCurrentProject());

            return report != null ? getTestSuiteLogRecord(report) : null;
        } catch (Exception e) {
            LogUtil.logError(e);
            return null;
        }
    }

    public TestSuiteLogRecord getTestSuiteLogRecordByFullPath(String reportFullpath) {
        if (StringUtils.isEmpty(reportFullpath)) {
            return null;
        }
        try {
            ReportEntity report = ReportController.getInstance().getReportEntityByFullPath(reportFullpath);

            return report != null ? getTestSuiteLogRecord(report) : null;
        } catch (Exception e) {
            LogUtil.logError(e);
            return null;
        }
    }

    public synchronized TestSuiteLogRecord getTestSuiteLogRecord(ReportEntity reportEntity) {
        TestSuiteLogRecord suiteLogRecord = suiteLogRecordMap.get(reportEntity.getId());
        if (suiteLogRecord == null) {
            try {
                suiteLogRecord = ReportUtil.generate(reportEntity.getLocation());
                suiteLogRecordMap.put(reportEntity.getId(), suiteLogRecord);
            } catch (Exception e) {
                LogUtil.logError(e);
            }
        }
        return suiteLogRecord;
    }

    public synchronized TestSuiteLogRecord getTestSuiteLogRecord(ReportEntity reportEntity,
            IProgressMonitor progressMonitor) {
        TestSuiteLogRecord suiteLogRecord = suiteLogRecordMap.get(reportEntity.getId());
        if (suiteLogRecord == null) {
            try {
                suiteLogRecord = ReportUtil.generate(reportEntity.getLocation(), progressMonitor);
                suiteLogRecordMap.put(reportEntity.getId(), suiteLogRecord);
            } catch (Exception e) {
                LogUtil.logError(e);
            }
        }
        return suiteLogRecord;
    }

    public void refreshLogRecord(ReportEntity reportEntity) {
        if (reportEntity != null) {
            try {
                TestSuiteLogRecord suiteLogRecord = ReportUtil.generate(reportEntity.getLocation());
                suiteLogRecordMap.put(reportEntity.getId(), suiteLogRecord);
            } catch (Exception e) {
                LogUtil.logError(e);
            }
        }
    }
    
    public void deleteReport(String reportId) {
        if (suiteLogRecordMap.containsKey(reportId)) {
            suiteLogRecordMap.remove(reportId);
        }
    }
    
    public void clear() {
        suiteLogRecordMap.clear();
    }
}
