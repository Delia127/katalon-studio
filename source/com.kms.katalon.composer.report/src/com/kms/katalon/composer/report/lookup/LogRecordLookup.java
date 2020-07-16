package com.kms.katalon.composer.report.lookup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.LogRecordController;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;

public class LogRecordLookup implements EventHandler {

    private static LogRecordLookup _instance;

    private LogRecordLookup() {
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
        return LogRecordController.getInstance().getTestSuiteLogRecord(reportId);
    }

    public TestSuiteLogRecord getTestSuiteLogRecordByFullPath(String reportFullpath) {
        return LogRecordController.getInstance().getTestSuiteLogRecordByFullPath(reportFullpath);
    }

    public synchronized TestSuiteLogRecord getTestSuiteLogRecord(ReportEntity reportEntity) {
        return LogRecordController.getInstance().getTestSuiteLogRecord(reportEntity);
    }

    public synchronized TestSuiteLogRecord getTestSuiteLogRecord(ReportEntity reportEntity,
            IProgressMonitor progressMonitor) {
        return LogRecordController.getInstance().getTestSuiteLogRecord(reportEntity, progressMonitor);
    }

    public void refreshLogRecord(ReportEntity reportEntity) {
        LogRecordController.getInstance().refreshLogRecord(reportEntity);
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
                LogRecordController.getInstance().deleteReport(updatedReportId);
                break;
            }
            case EventConstants.REPORT_DELETED: {
                // Remove TestSuiteLogRecord if it has been deleted
                String reportId = (String) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                LogRecordController.getInstance().deleteReport(reportId);
                break;
            }
            case EventConstants.PROJECT_OPENED: {
                LogRecordController.getInstance().clear();
                break;
            }
        }
    }
}
