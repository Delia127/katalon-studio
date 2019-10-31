package com.kms.katalon.composer.report.lookup;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.entity.report.ReportEntity;

public class LogRecordLookup implements EventHandler {

    private static LogRecordLookup _instance;

    // Key: id of report entity, Value: the relevant TestSuiteLogRecord instance of the report
    private Map<String, TestSuiteLogRecord> suiteLogRecordMap;

    private LogRecordLookup() {
        suiteLogRecordMap = new HashMap<String, TestSuiteLogRecord>();

        EventBrokerSingleton.getInstance().getEventBroker().subscribe(EventConstants.REPORT_DELETED, this);
        EventBrokerSingleton.getInstance().getEventBroker().subscribe(EventConstants.PROJECT_OPENED, this);
    }

    public static LogRecordLookup getInstance() {
        if (_instance == null) {
            _instance = new LogRecordLookup();
        }
        return _instance;
    }

    public TestSuiteLogRecord getTestSuiteLogRecord(String reportId) {
        if (StringUtils.isEmpty(reportId)) {
            return null;
        }
        try {
            ReportEntity report = ReportController.getInstance().getReportEntityByDisplayId(reportId,
                    ProjectController.getInstance().getCurrentProject());

            return report != null ? getTestSuiteLogRecord(report) : null;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
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
            LoggerSingleton.logError(e);
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
                LoggerSingleton.logError(e);
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
                LoggerSingleton.logError(e);
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
                LoggerSingleton.logError(e);
            }
        }
    }

    @Override
    public void handleEvent(Event event) {
        String topic = event.getTopic();
        switch (topic) {
            case EventConstants.REPORT_UPDATED: {
                // Remove TestSuiteLogRecord if it's out of date
                Object[] objects = (Object[]) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (objects == null || objects.length != 2) {
                    return;
                }
                String updatedReportId = (String) objects[0];
                if (suiteLogRecordMap.containsKey(updatedReportId)) {
                    suiteLogRecordMap.remove(updatedReportId);
                }
                break;
            }
            case EventConstants.REPORT_DELETED: {
                // Remove TestSuiteLogRecord if it has been deleted
                String reportId = (String) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (suiteLogRecordMap.containsKey(reportId)) {
                    suiteLogRecordMap.remove(reportId);
                }
                break;
            }
            case EventConstants.PROJECT_OPENED: {
                suiteLogRecordMap.clear();
                break;
            }
        }
    }
}
