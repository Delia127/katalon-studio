package com.kms.katalon.core.reporting.export;

import java.io.File;

import com.kms.katalon.core.logging.model.TestSuiteCollectionLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.setting.ReportFormatType;

/**
 * This interface provides ability to custom reports of Test Suites or Test Suite Collections for custom keyword plugin.
 * 
 * <p>
 * The implementation class should be declared in katalon-plugin.json file with this template:
 * 
 * <pre>{@code
 * {
 *  "report": {
 *       "exportProvider": "implementationClassName"
 *    }
 * }</pre>
 * 
 * @since 6.1.2
 *
 */
public interface ExportReportProvider {

    default public ReportFormatType[] supportedFormatForTestSuite() {
        return new ReportFormatType[0];
    }

    default public ReportFormatType[] supportedFormatForTestSuiteCollection() {
        return new ReportFormatType[0];
    }

    default public void exportTestSuite(File destFile, ReportFormatType formatType, TestSuiteLogRecord testSuiteLogRecord) {
        // Children may override this
    }

    default public void exportTestSuiteCollection(File destFile, ReportFormatType formatType,
            TestSuiteCollectionLogRecord testSuiteCollectionLogRecord) {
        // Children may override this
    }
}
