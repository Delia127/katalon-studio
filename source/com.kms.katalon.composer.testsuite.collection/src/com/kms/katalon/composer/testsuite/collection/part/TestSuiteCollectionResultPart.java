package com.kms.katalon.composer.testsuite.collection.part;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class TestSuiteCollectionResultPart {
    
    private static final String REPORT_COLLECTION_PART_CLASS = "com.kms.katalon.composer.report.parts.ReportCollectionPart";
    
    private Object reportCollectionPart;
    
    public TestSuiteCollectionResultPart(Object part) {
        if (!REPORT_COLLECTION_PART_CLASS.equals(part.getClass().getName())) {
            throw new IllegalArgumentException("Part must be an instance of ReportCollectionPart");
        }
        this.reportCollectionPart = part;
    }
    
    public MPart getMPart() {
        Method getMPartMethod;
        try {
            getMPartMethod = reportCollectionPart.getClass().getMethod("getMPart");
            return (MPart) getMPartMethod.invoke(reportCollectionPart);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }
}
