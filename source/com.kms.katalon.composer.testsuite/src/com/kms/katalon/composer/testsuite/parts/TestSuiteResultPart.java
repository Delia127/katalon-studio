package com.kms.katalon.composer.testsuite.parts;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.entity.report.ReportEntity;

public class TestSuiteResultPart {

    private static final String REPORT_PART_CLASS = "com.kms.katalon.composer.report.parts.ReportPart";

    private Object reportPart;

    public TestSuiteResultPart(Object part) {
        if (!REPORT_PART_CLASS.equals(part.getClass().getName())) {
            throw new IllegalArgumentException("Part must be an instance of ReportPart");
        }
        this.reportPart = part;
    }

    public void updateReport(ReportEntity report) {
        try {
            Method updateReportAndInputMethod = reportPart.getClass().getMethod("updateReportAndInput",
                    ReportEntity.class);
            updateReportAndInputMethod.invoke(reportPart, report);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            LoggerSingleton.logError(e);
        }
    }
}
